/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author longluqian
 */
public interface ResourceService {
   public ResourceEntity createResource(ResourceEntity resourceEntity, Long categoryId, Long profileId);
   public Page<ResourceEntity> getAllAvailableResources(Pageable pageble);
   public Page<ResourceEntity> getAllResources(Pageable pageble);
}
