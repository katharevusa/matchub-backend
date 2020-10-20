/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UserNotFoundException;
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

        return newReview;
    }
}
