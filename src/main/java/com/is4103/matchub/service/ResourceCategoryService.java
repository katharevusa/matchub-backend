/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.exception.DeleteResourceCategoryException;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.vo.ResourceCategoryVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author longluqian
 */
public interface ResourceCategoryService {

    public ResourceCategoryEntity createResourceCategory(ResourceCategoryEntity resourceCategoryEntity);

    public ResourceCategoryEntity getResourceCategoryById(Long id);

    public Page<ResourceCategoryEntity> getAllResourceCategories(Pageable page);

    public ResourceCategoryEntity updateResourceCategory(ResourceCategoryVO resourceCategoryVO) throws ResourceCategoryNotFoundException;

     public ResourceCategoryEntity createResourceCategory(ResourceCategoryVO resourceCategoryVO);
     
     public void deleteResourceCategories(Long resourceCategoryId)throws ResourceCategoryNotFoundException, DeleteResourceCategoryException;
}
