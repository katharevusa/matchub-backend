/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author ngjin
 */
public interface MatchingService {

    List<ResourceEntity> recommendResources(Long projectId) throws ProjectNotFoundException;

    Page<ResourceEntity> recommendResourcesAsPageable(Long projectId, Pageable pageable) throws ProjectNotFoundException;

    List<ProjectEntity> recommendProjects(Long resourceId) throws ResourceNotFoundException;

    Page<ProjectEntity> recommendProjectsAsPageable(Long resourceId, Pageable pageable) throws ResourceNotFoundException;

}
