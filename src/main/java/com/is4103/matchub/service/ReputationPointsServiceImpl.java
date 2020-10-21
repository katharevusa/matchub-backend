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
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.vo.IssuePointsToResourceDonorsVO;
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
            }
        }
    }

}
