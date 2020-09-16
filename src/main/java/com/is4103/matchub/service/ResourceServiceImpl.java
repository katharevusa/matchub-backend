/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.UpdateResourceException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ResourceCategoryEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.vo.ResourceVO;
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

    @Autowired
    ResourceCategoryEntityRepository resourceCategoryEntityRepository;

    @Override
    public ResourceEntity createResource(ResourceVO vo) throws ResourceCategoryNotFoundException, UserNotFoundException {
        ResourceEntity newResource = new ResourceEntity();
        vo.updateResource(newResource);
        Optional<ResourceCategoryEntity> categoryOptional = resourceCategoryEntityRepository.findById(newResource.getResourceCategoryId());

        // resource category association
        ResourceCategoryEntity category;
        if (categoryOptional.isPresent()) {
            category = categoryOptional.get();
        } else {
            throw new ResourceCategoryNotFoundException("Unable to find resource category");
        }

        //owner association
        ProfileEntity profile;
        Optional<ProfileEntity> profileOptional = profileEntityRepository.findById(newResource.getResourceOwnerId());
        if (profileOptional.isPresent()) {
            profile = profileOptional.get();
        } else {
            throw new UserNotFoundException("Unable to find user");
        }
        category.getResources().add(newResource);
        profile.getHostedResources().add(newResource);

        newResource = resourceEntityRepository.saveAndFlush(newResource);

        return newResource;

    }

    //This creation method is only for current data init
    @Override
    public ResourceEntity createResource(ResourceEntity resourceEntity, Long categoryId, Long profileId) {
        ResourceCategoryEntity category = resourceCategoryService.getResourceCategoryById(categoryId);
        ProfileEntity profileEntity = profileEntityRepository.findById(profileId).get();

        resourceEntity.setResourceOwnerId(profileId);
        resourceEntity.setResourceCategoryId(categoryId);

        category.getResources().add(resourceEntity);
        profileEntity.getHostedResources().add(resourceEntity);
        resourceEntity = resourceEntityRepository.saveAndFlush(resourceEntity);

        return resourceEntity;
    }

    public ResourceEntity updateResource(ResourceVO vo, Long updaterId, Long resourceId) throws ResourceNotFoundException, UpdateResourceException {
        Optional<ResourceEntity> oldResourceOptional = resourceEntityRepository.findById(resourceId);

        ResourceEntity oldResource;
        if (oldResourceOptional.isPresent()) {
            oldResource = oldResourceOptional.get();
        } else {
            throw new ResourceNotFoundException("Unable to update resource: resource not found");
        }

        if (oldResource.getResourceOwnerId().equals(updaterId)) {

            //delete resource from old category
            Optional<ResourceCategoryEntity> categoryOptional = resourceCategoryEntityRepository.findById(oldResource.getResourceCategoryId());
            categoryOptional.get().getResources().remove(oldResource);

            vo.updateResource(oldResource);
            categoryOptional = resourceCategoryEntityRepository.findById(oldResource.getResourceCategoryId());
            categoryOptional.get().getResources().add(oldResource);
            //add resource to the updated category 
            oldResource = resourceEntityRepository.saveAndFlush(oldResource);
            return oldResource;
        } else {
            throw new UpdateResourceException("Only resource owner can update this resource");
        }

    }

    @Override
    public Page<ResourceEntity> getAllAvailableResources(Pageable pageable) {
        return resourceEntityRepository.getAllAvailableResources(pageable);
    }

    @Override
    public Page<ResourceEntity> getAllResources(Pageable pageble) {
        return resourceEntityRepository.findAll(pageble);
    }

    public ResourceEntity getResourceById(Long id) throws ResourceNotFoundException {

        Optional<ResourceEntity> resourceEntity = resourceEntityRepository.findById(id);
        if (resourceEntity.isPresent()) {
            return resourceEntity.get();
        } else {
            throw new ResourceNotFoundException("Resource is not found");
        }

    }

    @Override
    public Page<ResourceEntity> getHostedResources(Long profileId, Pageable pageable) {
        return resourceEntityRepository.getHostedResources(profileId, pageable);
    }

}
