/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ResourceCategoryEntity;

/**
 *
 * @author longluqian
 */
public interface ResourceCategoryService {
    public ResourceCategoryEntity createResourceCategory(ResourceCategoryEntity resourceCategoryEntity);
    
    public ResourceCategoryEntity getResourceCategoryById(Long id);
    
}
