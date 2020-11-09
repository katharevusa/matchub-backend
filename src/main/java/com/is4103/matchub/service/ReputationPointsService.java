/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.GamificationPointTiers;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.vo.IssuePointsToResourceDonorsVO;
import com.is4103.matchub.vo.IssuePointsToTeamMembersVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author ngjin
 */
public interface ReputationPointsService {

    Page<ResourceEntity> getResourceOfProject(Long projectId, Pageable pageable) throws ProjectNotFoundException;

    void issuePointsToResourceDonors(IssuePointsToResourceDonorsVO vo) throws ProjectNotFoundException;

    void issuePointsToTeamMembers(IssuePointsToTeamMembersVO vo) throws ProjectNotFoundException;

    ProjectEntity spotlightProject(Long projectId, Long accountId) throws ProjectNotFoundException;

    ResourceEntity spotlightResource(Long resourceId, Long accountId) throws ResourceNotFoundException;

    void issuePointsToFundDonors(ProjectEntity project);

    void issuePointsForCompletedTasks(ProjectEntity project) throws ProjectNotFoundException;

    void issueBaselinePointsToResourceDonors(ProjectEntity project) throws ProjectNotFoundException;

    void issuePointsByReviewRatings(Long projectId) throws ProjectNotFoundException;

    GamificationPointTiers getGamificationPointTiers();

}
