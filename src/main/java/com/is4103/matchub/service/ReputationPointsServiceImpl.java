/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.FundPledgeEntity;
import com.is4103.matchub.entity.FundsCampaignEntity;
import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.entity.TaskEntity;
import com.is4103.matchub.enumeration.FundStatusEnum;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.UnableToRewardRepPointsException;
import com.is4103.matchub.exception.UnableToSpotlightException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.repository.TaskColumnEntityRepository;
import com.is4103.matchub.vo.IssuePointsToResourceDonorsVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        //get all the resources of project
        List<ResourceEntity> resources = resourceEntityRepository.getResourcesByProject(vo.getProjectId());

        if (vo.getHashmap() == null) {
            vo.setHashmap(new HashMap<Long, Integer>());
        }

        for (ResourceEntity r : resources) {
            //award additional points
            if (vo.getHashmap().containsKey(r.getResourceId())) {

                //find the base points 
                ResourceCategoryEntity resourceCat = resourceCategoryService.getResourceCategoryById(r.getResourceCategoryId());
                Integer basePoints = resourceCat.getCommunityPointsGuideline();

                //additional points 
                Integer additionalPoints = vo.getHashmap().get(r.getResourceId());

                //check the extrapoints awarded exceeds total points in project
                if (project.getProjectPoolPoints() < additionalPoints) {
                    throw new UnableToRewardRepPointsException("Unable to Reward Reputation points to "
                            + "resource donor: additional points entered exceeded project's pool of points");
                }

                //calculate the total points to award
                Integer totalPointsToAward = additionalPoints + basePoints;

                //find the resource donor 
                Long resourceDonorId = r.getResourceOwnerId();
                ProfileEntity resourceDonor = profileEntityRepository.findById(resourceDonorId)
                        .orElseThrow(() -> new UserNotFoundException(resourceDonorId));

                //award the resourceDonor the points
                resourceDonor.setReputationPoints(resourceDonor.getReputationPoints() + totalPointsToAward);
                resourceDonor = profileEntityRepository.saveAndFlush(resourceDonor);

                System.out.println("Awarded points to resource donor: " + totalPointsToAward);

                //deduct additionalPoints from the projectPoolPoints
                project.setProjectPoolPoints(project.getProjectPoolPoints() - additionalPoints);
                project = projectEntityRepository.saveAndFlush(project);

                checkPointsToAwardSpotlightChances(resourceDonor);

            } else { //award base points to resources if not found in HashMap

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

                System.out.println("Awarded points to resource donor (basepoints): " + basePoints);

                checkPointsToAwardSpotlightChances(resourceDonor);
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

    private void checkPointsToAwardSpotlightChances(ProfileEntity profile) {
        if (profile.getReputationPoints() > 200) {

            profile.setSpotlightChances(profile.getSpotlightChances() + 5);
            profileEntityRepository.saveAndFlush(profile);
        }
    }

    //************this method is triggered upon completedProject() method
    @Override
    public void issuePointsToFundDonors(ProjectEntity project) {

        System.out.println("Issue Points to Fund Donors method****************");

        List<FundsCampaignEntity> fundCampaigns = project.getFundsCampaign();

        for (FundsCampaignEntity f : fundCampaigns) {
            //get a list of donations for that fund campaign 
            List<FundPledgeEntity> donations = f.getFundPledges();

            //for each donation, allocation points to fund donor
            for (FundPledgeEntity donate : donations) {
                //get donor
                ProfileEntity fundDonor = donate.getProfile();

                //get the amount donated 
                BigDecimal donatedAmt = donate.getDonatedAmount();

                //if donation is received, then rep points is given
                //if it is any enum other than received then rep points is not awarded
                if (donate.getFundStatus() == FundStatusEnum.RECEIVED) {
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

                //add task creator and task leader
                ProfileEntity taskCreator = profileEntityRepository.findById(t.getTaskCreatorId())
                        .orElseThrow(() -> new UserNotFoundException(t.getTaskCreatorId()));

                ProfileEntity taskLeader = profileEntityRepository.findById(t.getTaskLeaderId())
                        .orElseThrow(() -> new UserNotFoundException(t.getTaskLeaderId()));

                if (!taskDoers.contains(taskCreator)) {
                    taskDoers.add(taskCreator);
                }
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

}
