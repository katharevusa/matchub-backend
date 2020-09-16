/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    ResourceEntityRepository resourceEntityRepository;
    @Autowired
    ResourceCategoryService resourceCategoryService;
    @Autowired
    ProfileEntityRepository profileEntityRepository;

    @Override
    public ResourceEntity createResource(ResourceEntity resourceEntity, Long categoryId, Long profileId) {
        ResourceCategoryEntity category = resourceCategoryService.getResourceCategoryById(categoryId);
        ProfileEntity profileEntity = profileEntityRepository.findById(profileId).get();

        resourceEntity.setResourceOwnerId(profileId);
        resourceEntity.setResourceCategoryId(categoryId);

        resourceEntity = resourceEntityRepository.saveAndFlush(resourceEntity);

        category.getResources().add(resourceEntity);
        profileEntity.getHostedResources().add(resourceEntity);

        return resourceEntity;
    }

    @Override
    public Page<ResourceEntity> getAllAvailableResources(Pageable pageble) {
        return resourceEntityRepository.getAllAvailableResources(pageble);
    }

    @Override
    public Page<ResourceEntity> getAllResources(Pageable pageble) {
        return resourceEntityRepository.findAll(pageble);
    }

    public ResourceEntity getResourceById(Long id) throws ResourceNotFoundException{

        Optional<ResourceEntity> resourceEntity = resourceEntityRepository.findById(id);
        if (resourceEntity.isPresent()) {
            return resourceEntity.get();
        } else {
            throw new ResourceNotFoundException("Resource is not found");
        }

    }

}
