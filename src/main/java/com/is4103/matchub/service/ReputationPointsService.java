/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.vo.IssuePointsToResourceDonorsVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author ngjin
 */
public interface ReputationPointsService {

    Page<ResourceEntity> getResourceOfProject(Long projectId, Pageable pageable) throws ProjectNotFoundException;

    void issuePointsToResourceDonors(IssuePointsToResourceDonorsVO vo) throws ProjectNotFoundException;

}
