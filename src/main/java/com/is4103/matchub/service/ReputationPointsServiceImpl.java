/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.DonationEntity;
import com.is4103.matchub.entity.DonationOptionEntity;
import com.is4103.matchub.entity.FundCampaignEntity;
import com.is4103.matchub.entity.GamificationPointTiers;
import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.entity.TaskEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.UnableToRewardRepPointsException;
import com.is4103.matchub.exception.UnableToSpotlightException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.repository.ReviewEntityRepository;
import com.is4103.matchub.repository.TaskColumnEntityRepository;
import com.is4103.matchub.vo.IssuePointsToResourceDonorsVO;
import com.is4103.matchub.vo.IssuePointsToTeamMembersVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author ngjin
 */
@Service
public class ReputationPointsServiceImpl implements ReputationPointsService {

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private ResourceRequestEntityRepository resourceRequestEntityRepository;

    @Autowired
    private ResourceEntityRepository resourceEntityRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceCategoryService resourceCategoryService;

    @Autowired
    private KanbanBoardService kanbanBoardService;

    @Autowired
    private TaskColumnEntityRepository taskColumnEntityRepository;

    @Autowired
    private ReviewEntityRepository reviewEntityRepository;

    @Override
    public Page<ResourceEntity> getResourceOfProject(Long projectId, Pageable pageable) throws ProjectNotFoundException {

        //check if the project exists
        ProjectEntity project = projectEntityRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("ProjectId: " + projectId + " not found."));

        return resourceEntityRepository.getResourcesByProject(projectId, pageable);
    }

    //*********** this method is triggered 2 weeks after completion of project
    @Override
    public void issuePointsToResourceDonors(IssuePointsToResourceDonorsVO vo) throws ProjectNotFoundException {

        //check if the project exists
        ProjectEntity project = projectEntityRepository.findById(vo.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("ProjectId: " + vo.getProjectId() + " not found."));

        //get all the matched resources of project
        List<ResourceEntity> resources = resourceEntityRepository.getResourcesByProject(vo.getProjectId());

        //giving out baseline points only 
        for (ResourceEntity r : resources) {
            //find the base points 
            ResourceCategoryEntity resourceCat = resourceCategoryService.getResourceCategoryById(r.getResourceCategoryId());
            Integer basePoints = resourceCat.getCommunityPointsGuideline();

            //find the resource donor 
            Long resourceDonorId = r.getResourceOwnerId();
            ProfileEntity resourceDonor = profileEntityRepository.findById(resourceDonorId)
                    .orElseThrow(() -> new UserNotFoundException(resourceDonorId));

            //award the resourceDonor the points
            resourceDonor.setReputationPoints(resourceDonor.getReputationPoints() + basePoints);
            resourceDonor = profileEntityRepository.saveAndFlush(resourceDonor);

            System.out.println("Awarded baseline points to resource donor: " + basePoints);
        }

        //get list of resourceDonors
        List<ProfileEntity> resourceDonors = new ArrayList<>();

        for (ResourceEntity r : resources) {

            ProfileEntity resourceDonor = profileEntityRepository.findById(r.getResourceOwnerId())
                    .orElseThrow(() -> new UserNotFoundException(r.getResourceOwnerId()));

            resourceDonors.add(resourceDonor);
        }

        if (vo.getHashmap() == null) {
            vo.setHashmap(new HashMap<Long, Integer>());
        }

        for (ProfileEntity p : resourceDonors) {
            //award additional points
            if (vo.getHashmap().containsKey(p.getAccountId())) {

                //additional points 
                Integer additionalPoints = vo.getHashmap().get(p.getAccountId());

                //check the extrapoints awarded exceeds total points in project
                if (project.getProjectPoolPoints() < additionalPoints) {
                    throw new UnableToRewardRepPointsException("Unable to Reward Reputation points to "
                            + "resource donor: additional points entered exceeded project's pool of points");
                }

                Integer pointsBefore = p.getReputationPoints();

                //award the resourceDonor the points
                p.setReputationPoints(p.getReputationPoints() + additionalPoints);
                p = profileEntityRepository.saveAndFlush(p);

                System.out.println("Awarded points to resource donor: " + additionalPoints);

                //deduct additionalPoints from the projectPoolPoints
                project.setProjectPoolPoints(project.getProjectPoolPoints() - additionalPoints);
                project = projectEntityRepository.saveAndFlush(project);

                checkPointsToAwardSpotlightChances(pointsBefore, p.getReputationPoints(), p);

            }
        }

    }

    // old method implementation
//    @Override
//    public void issuePointsToResourceDonors(IssuePointsToResourceDonorsVO vo) throws ProjectNotFoundException {
//
//        //check if the project exists
//        ProjectEntity project = projectEntityRepository.findById(vo.getProjectId())
//                .orElseThrow(() -> new ProjectNotFoundException("ProjectId: " + vo.getProjectId() + " not found."));
//
//        //get all the resources of project
//        List<ResourceEntity> resources = resourceEntityRepository.getResourcesByProject(vo.getProjectId());
//
//        if (vo.getHashmap() == null) {
//            vo.setHashmap(new HashMap<Long, Integer>());
//        }
//
//        for (ResourceEntity r : resources) {
//            //award additional points
//            if (vo.getHashmap().containsKey(r.getResourceId())) {
//
//                //find the base points 
//                ResourceCategoryEntity resourceCat = resourceCategoryService.getResourceCategoryById(r.getResourceCategoryId());
//                Integer basePoints = resourceCat.getCommunityPointsGuideline();
//
//                //additional points 
//                Integer additionalPoints = vo.getHashmap().get(r.getResourceId());
//
//                //check the extrapoints awarded exceeds total points in project
//                if (project.getProjectPoolPoints() < additionalPoints) {
//                    throw new UnableToRewardRepPointsException("Unable to Reward Reputation points to "
//                            + "resource donor: additional points entered exceeded project's pool of points");
//                }
//
//                //calculate the total points to award
//                Integer totalPointsToAward = additionalPoints + basePoints;
//
//                //find the resource donor 
//                Long resourceDonorId = r.getResourceOwnerId();
//                ProfileEntity resourceDonor = profileEntityRepository.findById(resourceDonorId)
//                        .orElseThrow(() -> new UserNotFoundException(resourceDonorId));
//
//                //award the resourceDonor the points
//                resourceDonor.setReputationPoints(resourceDonor.getReputationPoints() + totalPointsToAward);
//                resourceDonor = profileEntityRepository.saveAndFlush(resourceDonor);
//
//                System.out.println("Awarded points to resource donor: " + totalPointsToAward);
//
//                //deduct additionalPoints from the projectPoolPoints
//                project.setProjectPoolPoints(project.getProjectPoolPoints() - additionalPoints);
//                project = projectEntityRepository.saveAndFlush(project);
//
//                checkPointsToAwardSpotlightChances(resourceDonor);
//
//            } else { //award base points to resources if not found in HashMap
//
//                //find the base points 
//                ResourceCategoryEntity resourceCat = resourceCategoryService.getResourceCategoryById(r.getResourceCategoryId());
//                Integer basePoints = resourceCat.getCommunityPointsGuideline();
//
//                //find the resource donor 
//                Long resourceDonorId = r.getResourceOwnerId();
//                ProfileEntity resourceDonor = profileEntityRepository.findById(resourceDonorId)
//                        .orElseThrow(() -> new UserNotFoundException(resourceDonorId));
//
//                //award the resourceDonor the points
//                resourceDonor.setReputationPoints(resourceDonor.getReputationPoints() + basePoints);
//                resourceDonor = profileEntityRepository.saveAndFlush(resourceDonor);
//
//                System.out.println("Awarded points to resource donor (basepoints): " + basePoints);
//
//                checkPointsToAwardSpotlightChances(resourceDonor);
//            }
//        }
//
//    }
    @Override
    public void issuePointsToTeamMembers(IssuePointsToTeamMembersVO vo) throws ProjectNotFoundException {

        //check if the project exists
        ProjectEntity project = projectEntityRepository.findById(vo.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("ProjectId: " + vo.getProjectId() + " not found."));

        if (vo.getHashmap() == null) {
            vo.setHashmap(new HashMap<Long, Integer>());
        }

        List<ProfileEntity> teamMembers = project.getTeamMembers();

        for (ProfileEntity p : teamMembers) {

            if (vo.getHashmap().containsKey(p.getAccountId())) {
                //additional points 
                Integer additionalPoints = vo.getHashmap().get(p.getAccountId());

                //check the extrapoints awarded exceeds total points in project
                if (project.getProjectPoolPoints() < additionalPoints) {
                    throw new UnableToRewardRepPointsException("Unable to Reward Reputation points to "
                            + "team member: additional points entered exceeded project's pool of points");
                }

                Integer pointsBefore = p.getReputationPoints();

                //award the resourceDonor the points
                p.setReputationPoints(p.getReputationPoints() + additionalPoints);
                p = profileEntityRepository.saveAndFlush(p);

                System.out.println("Awarded points to resource donor: " + additionalPoints);

                //deduct additionalPoints from the projectPoolPoints
                project.setProjectPoolPoints(project.getProjectPoolPoints() - additionalPoints);
                project = projectEntityRepository.saveAndFlush(project);

                checkPointsToAwardSpotlightChances(pointsBefore, p.getReputationPoints(), p);
            }
        }
    }

    @Override
    public ProjectEntity spotlightProject(Long projectId, Long accountId) throws ProjectNotFoundException {

        //check if profile exists
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        //check if the project exists
        ProjectEntity project = projectEntityRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("ProjectId: " + projectId + " not found."));

        //check if profile is a projectOwner of the specified project
        if (!project.getProjectOwners().contains(profile)) {
            throw new UnableToSpotlightException("Unable to spotlight project: account " + accountId
                    + " is not authorised to spotlight project as he/she is not a project owner of project " + projectId);
        }

        //check if profile has sufficient rep points to perform a spotlight 
        if (profile.getReputationPoints() < 200) {
            throw new UnableToSpotlightException("Unable to spotlight project: account " + accountId
                    + " does not have sufficient reputation points to spotlight");
        }

        //check if profile has sufficient spotlight chances 
        if (profile.getSpotlightChances() == 0) {
            throw new UnableToSpotlightException("Unable to spotlight project: account " + accountId
                    + " does not have any more spotlight chances left");
        }

        //check if project is currently spotlighted
        if (project.getSpotlight() == true) {
            throw new UnableToSpotlightException("Unable to spotlight project: Cannot spotlight a "
                    + "project that is currently spotlighted");
        }

        //set project spotlight attributes
        project.setSpotlight(true);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusDays(1);
        project.setSpotlightEndTime(endTime);

        project = projectEntityRepository.saveAndFlush(project);

        //deduct 1 spotlight chance away from account
        profile.setSpotlightChances(profile.getSpotlightChances() - 1);
        profile = profileEntityRepository.saveAndFlush(profile);

        return project;
    }

    @Override
    public ResourceEntity spotlightResource(Long resourceId, Long accountId) throws ResourceNotFoundException {

        //check if profile exists
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        //check if resource exist6s
        ResourceEntity resource = resourceEntityRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("ResourceId: " + resourceId + " not found."));

        //check if profile is the rosourceOwner
        if (!resource.getResourceOwnerId().equals(accountId)) {
            throw new UnableToSpotlightException("Unable to spotlight resource: account " + accountId
                    + " is not authorised to spotlight resource as he/she is not the resource owner");
        }

        //check if profile has sufficient rep points to perform a spotlight 
        if (profile.getReputationPoints() < 200) {
            throw new UnableToSpotlightException("Unable to spotlight resource: account " + accountId
                    + " does not have sufficient reputation points to spotlight");
        }

        //check if profile has sufficient spotlight chances 
        if (profile.getSpotlightChances() == 0) {
            throw new UnableToSpotlightException("Unable to spotlight resource: account " + accountId
                    + " does not have any more spotlight chances left");
        }

        //check if resource is currently spotlighted
        if (resource.getSpotlight() == true) {
            throw new UnableToSpotlightException("Unable to spotlight resource: Cannot spotlight a "
                    + "resource that is currently spotlighted");
        }

        //set resource spotlight attributes
        resource.setSpotlight(true);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusDays(1);
        resource.setSpotlightEndTime(endTime);

        resource = resourceEntityRepository.saveAndFlush(resource);

        //deduct 1 spotlight chance away from account
        profile.setSpotlightChances(profile.getSpotlightChances() - 1);
        profile = profileEntityRepository.saveAndFlush(profile);

        return resource;
    }

    private void checkPointsToAwardSpotlightChances(Integer pointsBefore, Integer pointsAfter, ProfileEntity profile) {

        int tierBefore = pointsBefore / 200;
        int tierAfter = pointsAfter / 200;

        if (tierAfter > tierBefore) {
            Integer spotlightToGive = (tierAfter - tierBefore) * 5;

            profile.setSpotlightChances(profile.getSpotlightChances() + spotlightToGive);
            profileEntityRepository.saveAndFlush(profile);

        }

    }

    //************this method is triggered upon completedProject() method
    @Override
    public void issuePointsToFundDonors(ProjectEntity project) {

        System.out.println("Issue Points to Fund Donors method****************");

        List<FundCampaignEntity> fundCampaigns = project.getFundsCampaign();

        for (FundCampaignEntity f : fundCampaigns) {
            //get a list of donations for that fund campaign 
            List<DonationOptionEntity> donationOptions = f.getDonationOptions();

            for (DonationOptionEntity d : donationOptions) {

                List<DonationEntity> donations = d.getDonations();

                //for each donation, allocation points to fund donor
                for (DonationEntity donate : donations) {
                    //get donor
                    ProfileEntity fundDonor = donate.getDonator();

                    //get the amount donated 
                    BigDecimal donatedAmt = donate.getDonatedAmount();

                    Integer addRepPoints = donatedAmt.divide(BigDecimal.valueOf(Long.valueOf(10))).intValue();

                    //set rep point upper cap to be 50 per donation no matter the donatedAmt
                    //fundDonor can only receive up to a max ot 50 rep points for their donation
                    if (addRepPoints > 50) {
                        //add points 
                        fundDonor.setReputationPoints(fundDonor.getReputationPoints() + 50);
                        System.out.println("Fund Donor will only receive a max of 50 rep points for their donation.");
                    } else {
                        //add points 
                        fundDonor.setReputationPoints(fundDonor.getReputationPoints() + addRepPoints);
                    }

                    //save updated profile into db 
                    profileEntityRepository.saveAndFlush(fundDonor);
                }
            }

        }

    }

    @Override
    public void issuePointsForCompletedTasks(ProjectEntity project) throws ProjectNotFoundException {

        System.out.println("Issue Points for Completed Tasks method****************");
        //get all kanban boards in project
        List<KanbanBoardEntity> kanbans = kanbanBoardService.getAllKanbanBoardByProjectId(project.getProjectId());

        for (KanbanBoardEntity k : kanbans) {
            //get the task column which is the last column/completed column
            TaskColumnEntity lastTaskColumn = taskColumnEntityRepository.getCompletedTaskColumnByKanbanboardId(k.getKanbanBoardId());

            //get a list of all tasks under that completed task column
            List<TaskEntity> completedTasks = lastTaskColumn.getListOfTasks();

            //for each completed task
            for (TaskEntity t : completedTasks) {

                //find the list of task doers 
                List<ProfileEntity> taskDoers = t.getTaskdoers();

                //add task leader
                ProfileEntity taskLeader = profileEntityRepository.findById(t.getTaskLeaderId())
                        .orElseThrow(() -> new UserNotFoundException(t.getTaskLeaderId()));

                if (!taskDoers.contains(taskLeader)) {
                    taskDoers.add(taskLeader);
                }

                //award 1 point to each profile in the list
                //persist into db
                for (ProfileEntity p : taskDoers) {
                    p.setReputationPoints(p.getReputationPoints() + 1);

                    profileEntityRepository.saveAndFlush(p);
                }
            }
        }

    }

    //********* triggered after the 2 weeks window period for reviews to be made 
    //rep points only added to teamMembers
    @Override
    public void issuePointsByReviewRatings(Long projectId) throws ProjectNotFoundException {
        //find the project 
        ProjectEntity project = projectEntityRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("ProjectId: " + projectId + " not found."));

        System.out.println("Project " + projectId + " found");

        //get the list of reviews associated with the project
        List<ReviewEntity> reviews = project.getReviews();

        System.out.println("Reviews size " + reviews.size());

        //do not include team ownerss
        for (ProfileEntity p : project.getTeamMembers()) {
            //get all the reviews from this project 
            List<ReviewEntity> allReviews = reviewEntityRepository.getReviewsOfAccountByProjectId(projectId, p.getAccountId());

            System.out.println("all reviews by profile " + allReviews.size());

            double averageRating = calculateAverageRatings(allReviews);
            Integer addRepPoints = 0;

            if (averageRating < 2.5) {
                //award 0.5
                addRepPoints = (int) (0.5 * (project.getProjectPoolPoints() / project.getTeamMembers().size()));

            } else {
                //average rating is > 0.5
                addRepPoints = (int) (averageRating / 5.0 * (project.getProjectPoolPoints() / project.getTeamMembers().size()));
            }

            System.out.println("average rating" + averageRating);

            p.setReputationPoints(p.getReputationPoints() + addRepPoints);
            profileEntityRepository.saveAndFlush(p);
        }
    }

    @Override
    public GamificationPointTiers getGamificationPointTiers() {

        return new GamificationPointTiers();
    }

    private double calculateAverageRatings(List<ReviewEntity> allReviews) {
        BigDecimal ratings = BigDecimal.valueOf(0);
        for (ReviewEntity r : allReviews) {
            ratings = ratings.add(r.getRating());
        }
        Double doubleRatings = ratings.doubleValue();
        double average = doubleRatings / allReviews.size();

        return average;
    }

}
