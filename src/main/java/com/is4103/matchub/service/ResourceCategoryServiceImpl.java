/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.DeleteResourceCategoryException;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.repository.ResourceCategoryEntityRepository;
import com.is4103.matchub.vo.ResourceCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class ResourceCategoryServiceImpl implements ResourceCategoryService {

    @Autowired
    ResourceCategoryEntityRepository resourceCategoryEntityRepository;
    
    //for data init
    @Override
     public ResourceCategoryEntity createResourceCategory(ResourceCategoryEntity resourceCategoryEntity) {
        
        return resourceCategoryEntityRepository.saveAndFlush(resourceCategoryEntity);
    }

    @Override
    public ResourceCategoryEntity createResourceCategory(ResourceCategoryVO resourceCategoryVO) {
        ResourceCategoryEntity resourceCategory = new ResourceCategoryEntity();
        resourceCategoryVO.createResourceCategory(resourceCategory);

        return resourceCategoryEntityRepository.saveAndFlush(resourceCategory);
    }

    @Override
    public ResourceCategoryEntity updateResourceCategory(ResourceCategoryVO resourceCategoryVO) throws ResourceCategoryNotFoundException{
        ResourceCategoryEntity resourceCategory = resourceCategoryEntityRepository.findById(resourceCategoryVO.resourceCategoryId).orElseThrow(()-> new ResourceCategoryNotFoundException());
        resourceCategoryVO.updateResourceCategory(resourceCategory);

        return resourceCategoryEntityRepository.saveAndFlush(resourceCategory);
    }

    @Override
    public ResourceCategoryEntity getResourceCategoryById(Long id) {
        return resourceCategoryEntityRepository.findById(id).get();

    }

    @Override
    public Page<ResourceCategoryEntity> getAllResourceCategories(Pageable page) {
        return resourceCategoryEntityRepository.findAll(page);

    }
    
    @Override
    public void deleteResourceCategories(Long resourceCategoryId)throws ResourceCategoryNotFoundException, DeleteResourceCategoryException{
        ResourceCategoryEntity resourceCategory = resourceCategoryEntityRepository.findById(resourceCategoryId).orElseThrow(()-> new ResourceCategoryNotFoundException());
        if(resourceCategory.getResources()!=null){
            throw new DeleteResourceCategoryException("Unable to delete resource category because there are resources assoiciating with it.");
        }
        resourceCategoryEntityRepository.delete(resourceCategory);
    }

}
