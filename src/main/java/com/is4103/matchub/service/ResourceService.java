/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.TerminateResourceException;
import com.is4103.matchub.exception.UpdateResourceException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.vo.ResourceVO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
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

    public Page<ResourceEntity> getHostedResources(Long profileId, Pageable pageable);

    Page<ResourceEntity> getSavedResourcesByAccountId(Long accountId, Pageable pageable);

    public ResourceEntity createResource(ResourceVO vo) throws ResourceCategoryNotFoundException, UserNotFoundException;

    public ResourceEntity updateResource(ResourceVO vo, Long updaterId, Long resourceId) throws ResourceNotFoundException, UpdateResourceException;

    public ResourceEntity setResourceProfilePic(Long resourceId, MultipartFile pic) throws ResourceNotFoundException;

    public ResourceEntity uploadPhotos(Long resourceId, MultipartFile[] photos) throws ResourceNotFoundException;

    public ResourceEntity uploadDocuments(Long resourceId, MultipartFile[] documents) throws ResourceNotFoundException;

    public ResourceEntity terminateResource(Long resourceId, Long terminatorId) throws ResourceNotFoundException, TerminateResourceException;

    public ResourceEntity deleteDocuments(Long resourceId, String[] docsToDelete) throws IOException, ResourceNotFoundException, UpdateResourceException;

    public ResourceEntity deletePhotos(Long resourceId, String[] photoToDelete) throws ResourceNotFoundException, IOException, UpdateResourceException;

    public ResourceEntity deleteResourceProfilePic(Long resourceId) throws ResourceNotFoundException, UpdateResourceException, IOException;
    public List<ResourceEntity> getResourcesByListOfId(List<Long> ids) throws ResourceNotFoundException;

    public Page<ResourceEntity> searchResourceByKeywords(String keyword, Pageable pageable);

    public Page<ResourceEntity> resourceGlobalSearch(String keyword, List<Long> categoryIds, Boolean availability, String startTime, String endTime, String country, Pageable pageable);


    public List<ResourceEntity> getMatchedResourcesByProjectId(Long projectId);

    public List<ResourceEntity> getSpotlightedResources();

    public Page<ResourceEntity> getSpotlightedResources(Pageable pageable);

}
    
   

