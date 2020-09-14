/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.repository.ResourceCategoryEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class ResourceCategoryImpl implements ResourceCategoryService{
    
    @Autowired
    ResourceCategoryEntityRepository resourceCategoryEntityRepository;
    
    public ResourceCategoryEntity createResourceCategory(ResourceCategoryEntity resourceCategoryEntity){
        return resourceCategoryEntityRepository.save(resourceCategoryEntity);
    }
    
    public ResourceCategoryEntity getResourceCategoryById(Long id){
     return resourceCategoryEntityRepository.findById(id).get();
        
    }
    public Page<ResourceCategoryEntity> getAllResourceCategories(Pageable page){
     return resourceCategoryEntityRepository.findAll(page);
        
    }
    
    
}
