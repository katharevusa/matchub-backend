/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.exception.DeleteResourceCategoryException;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.service.ResourceCategoryService;
import com.is4103.matchub.vo.ResourceCategoryVO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author longluqian
 *
 */
@RestController
@RequestMapping("/authenticated")
public class ResourceCategoryController {

    @Autowired
    ResourceCategoryService resourceCategoryService;

    @RequestMapping(method = RequestMethod.GET, value = "/getAllResourceCategories")
    Page<ResourceCategoryEntity> getAllResourceCategories(Pageable pageable) {
        return resourceCategoryService.getAllResourceCategories(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getResourceCategoryById")
    ResourceCategoryEntity getResourceCategoryById(@RequestParam(value = "resourceCategoryId", required = true)Long resourceCategoryId) {
        return resourceCategoryService.getResourceCategoryById(resourceCategoryId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateResourceCategory")
    public ResourceCategoryEntity updateResourceCategory(@Valid @RequestBody ResourceCategoryVO resourceCategoryVO) throws ResourceCategoryNotFoundException {
        return resourceCategoryService.updateResourceCategory(resourceCategoryVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createResourceCategory")
    public ResourceCategoryEntity createResourceCategory(@Valid @RequestBody ResourceCategoryVO resourceCategoryVO) {
        return resourceCategoryService.createResourceCategory(resourceCategoryVO);
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteResourceCategories")
    public void deleteResourceCategories(@RequestParam(value = "resourceCategoryId", required = true)Long resourceCategoryId)throws ResourceCategoryNotFoundException, DeleteResourceCategoryException{
        resourceCategoryService.deleteResourceCategories(resourceCategoryId);
    }
    
}
