/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.UnableToRewardRepPointsException;
import com.is4103.matchub.exception.UnableToSpotlightException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.vo.IssuePointsToResourceDonorsVO;
import java.time.LocalDateTime;
import java.util.Calendar;
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

}
