/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.TerminateResourceException;
import com.is4103.matchub.exception.UpdateResourceException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.vo.ResourceVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author longluqian
 */
public interface ResourceService {
   public ResourceEntity createResource(ResourceEntity resourceEntity, Long categoryId, Long profileId);
   public Page<ResourceEntity> getAllAvailableResources(Pageable pageble);
   public Page<ResourceEntity> getAllResources(Pageable pageble);
   public ResourceEntity getResourceById(Long id) throws ResourceNotFoundException;
   public Page<ResourceEntity> getHostedResources(Long profileId,Pageable pageable);
   public ResourceEntity createResource(ResourceVO vo)throws ResourceCategoryNotFoundException, UserNotFoundException;
   public ResourceEntity updateResource(ResourceVO vo, Long updaterId, Long resourceId) throws ResourceNotFoundException, UpdateResourceException;
   public ResourceEntity setResourceProfilePic(Long resourceId, MultipartFile pic) throws ResourceNotFoundException;
   public ResourceEntity uploadPhotos(Long resourceId, MultipartFile[] photos) throws ResourceNotFoundException;
   public ResourceEntity uploadDocuments(Long resourceId, MultipartFile[] documents) throws ResourceNotFoundException;
   public ResourceEntity terminateResource(Long resourceId, Long terminatorId)throws  ResourceNotFoundException, TerminateResourceException;
  
}