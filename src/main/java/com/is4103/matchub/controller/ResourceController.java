/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.service.ResourceService;
import com.is4103.matchub.vo.ResourceVO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author longluqian
 */
@RestController
@RequestMapping("/authenticated")
public class ResourceController {
    @Autowired
    ResourceService resourceService;
    
    //create Resources
     @RequestMapping(method = RequestMethod.POST, value = "/createNewResource")
    ResourceEntity createNewProject(@Valid @RequestBody ResourceVO vo) throws UserNotFoundException, ResourceCategoryNotFoundException{
       return resourceService.createResource(vo);           
    }
    
    
    
    
    //update resources
    
    
    //Download/Upload supporting documents for resource item
    
    //Terminate One Resource
  
    
    
    @RequestMapping(method = RequestMethod.GET, value = "/getAllAvailableResources")
    Page<ResourceEntity> getAllAvailableResources(Pageable pageable) {
       return resourceService.getAllAvailableResources(pageable);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getAllResources")
    Page<ResourceEntity> getAllResources(Pageable pageable) {
       return resourceService.getAllResources(pageable);
    }
    
     @RequestMapping(method = RequestMethod.GET, value = "/getResourceById")
     ResourceEntity getResourceById(Long resourceId) throws ResourceNotFoundException{
       return resourceService.getResourceById(resourceId);
    }
    
    //get list of hosted resources
    @RequestMapping(method = RequestMethod.GET, value = "/getHostedResources")
    Page<ResourceEntity> getHostedResources(Pageable pageable, Long profileId) {
       return resourceService.getHostedResources(profileId,pageable);
    }
    
}
