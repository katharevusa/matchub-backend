/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UnableToCreateReviewException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ReviewEntityRepository;
import com.is4103.matchub.vo.ReviewCreateVO;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author ngjin
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewEntityRepository reviewEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementEntityRepository announcementEntityRepository;

    @Override
    public Page<ReviewEntity> getReviewsReceivedByAccountId(Long id, Pageable pageable) {
        ProfileEntity profile = profileEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return reviewEntityRepository.getReviewsReceivedByAccountId(id, pageable);
    }

    @Override
    public Page<ReviewEntity> getReviewsGivenByAccountId(Long id, Pageable pageable) {
        ProfileEntity profile = profileEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return reviewEntityRepository.getReviewsGivenByAccountId(id, pageable);
    }

    @Override
    public ReviewEntity createReview(ReviewCreateVO vo) throws ProjectNotFoundException {
        //check if project exists
        ProjectEntity project = projectEntityRepository.findById(vo.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project id: " + vo.getProjectId() + " does not exist."));

        //check if reviewer exists
        ProfileEntity reviewer = profileEntityRepository.findById(vo.getReviewerId())
                .orElseThrow(() -> new UserNotFoundException(vo.getReviewerId()));

        //check if reviewReceiver exists
        ProfileEntity reviewReceiver = profileEntityRepository.findById(vo.getReviewReceiverId())
                .orElseThrow(() -> new UserNotFoundException(vo.getReviewReceiverId()));

        //check that reviewer and reviewReceiver are not the same person 
        if (reviewReceiver.getAccountId().equals(reviewer.getAccountId())) {
            throw new UnableToCreateReviewException("Unable to create review: You are not allowed to create a review for yourself");
        }

        //***************add in additional checks 
        //check if review has been made to the same person already or not 
        for (ReviewEntity r : reviewReceiver.getReviewsReceived()) {
            //do not allow review to be made to the same person of the same project 
            //ie. only 1 review can be made from person A to B of a particular project
            if (r.getReviewer() == reviewer && r.getProject() == project) {
                throw new UnableToCreateReviewException("Unable to create review: a review can only be made once: account "
                        + reviewer.getAccountId() + " has already left a review for account " + reviewReceiver.getAccountId() + " for project id " + project.getProjectId());
            }
        }

        //review can only be made within the same project
        if ((!project.getTeamMembers().contains(reviewer) && !project.getProjectOwners().contains(reviewer))
                || (!project.getTeamMembers().contains(reviewReceiver) && !project.getProjectOwners().contains(reviewReceiver))) {
            throw new UnableToCreateReviewException("Unable to create review: accountId "
                    + reviewer.getAccountId() + " and accountId " + reviewReceiver.getAccountId()
                    + " do not belong to the same project specified.");
        }

        ReviewEntity newReview = new ReviewEntity();
        newReview.setTimeCreated(LocalDateTime.now());

        newReview.setProject(project);
        newReview.setReviewer(reviewer);
        newReview.setReviewReceiver(reviewReceiver);

        vo.updateReview(newReview);

        //update associations both ways
        reviewReceiver.getReviewsReceived().add(newReview);
        reviewReceiver = profileEntityRepository.save(reviewReceiver);

        newReview = reviewEntityRepository.saveAndFlush(newReview);

        // get the name of the person giving reviews
        String reviewerName = "";
        if (reviewer instanceof IndividualEntity) {
            reviewerName = ((IndividualEntity) reviewer).getFirstName() + " " + ((IndividualEntity) reviewer).getLastName();
        } else if (reviewer instanceof OrganisationEntity) {
            reviewerName = ((OrganisationEntity) reviewer).getOrganizationName();
        }

        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTitle("Check out your new review from " + reviewerName + "!");
        announcementEntity.setContent(vo.getContent());
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setType(AnnouncementTypeEnum.RECEIVING_NEW_REVIEW);
        announcementEntity.setReviewId(newReview.getReviewId());
        // association
        announcementEntity.getNotifiedUsers().add(reviewReceiver);
        reviewReceiver.getAnnouncements().add(announcementEntity);
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification
        announcementService.createNormalNotification(announcementEntity);

        return newReview;

    }
}
