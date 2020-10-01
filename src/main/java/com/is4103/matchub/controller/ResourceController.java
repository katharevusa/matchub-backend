/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.TerminateResourceException;
import com.is4103.matchub.exception.UpdateResourceException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.service.ResourceService;
import com.is4103.matchub.vo.ResourceVO;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    ResourceEntity createNewResource(@Valid @RequestBody ResourceVO vo) throws UserNotFoundException, ResourceCategoryNotFoundException {
        return resourceService.createResource(vo);
    }

    //update resources
    @RequestMapping(method = RequestMethod.PUT, value = "/updateResource")
    ResourceEntity updateResourceById(@Valid @RequestBody ResourceVO vo,
            @RequestParam(value = "updaterId", defaultValue = "") Long updaterId, @RequestParam(value = "resourceId", defaultValue = "") Long resourceId) throws UpdateResourceException, ResourceNotFoundException {
        return resourceService.updateResource(vo, updaterId, resourceId);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllAvailableResources")
    Page<ResourceEntity> getAllAvailableResources(Pageable pageable) {
        return resourceService.getAllAvailableResources(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllResources")
    Page<ResourceEntity> getAllResources(Pageable pageable) {
        return resourceService.getAllResources(pageable);
    }
    
    //get list of resources by list of resource ids
    @RequestMapping(method = RequestMethod.GET, value = "/getListOfResourcesByIds")
    List<ResourceEntity> getListOfResourcesByIds(List<Long> ids) throws ResourceNotFoundException{
        return resourceService.getResourcesByListOfId(ids);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getResourceById")
    ResourceEntity getResourceById(Long resourceId) throws ResourceNotFoundException {
        return resourceService.getResourceById(resourceId);
    }

    //get list of hosted resources
    @RequestMapping(method = RequestMethod.GET, value = "/getHostedResources")
    Page<ResourceEntity> getHostedResources(Pageable pageable, Long profileId) {
        return resourceService.getHostedResources(profileId, pageable);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateResource/updateResourceProfilePic")
    public ResourceEntity updateResourceProfilePic(@RequestParam(value = "profilePic") MultipartFile profilePic, @RequestParam("resourceId") Long resourceId) throws ResourceNotFoundException {
        return resourceService.setResourceProfilePic(resourceId, profilePic);
    }
    
     @RequestMapping(method = RequestMethod.DELETE, value = "/updateResource/deleteProfilePic")
    public ResourceEntity deleteResourceProfilePic(@RequestParam(value = "resourceId") Long resourceId) throws ResourceNotFoundException,UpdateResourceException,IOException{
        return resourceService.deleteResourceProfilePic(resourceId);
    }

    //upload list of photos
    @RequestMapping(method = RequestMethod.POST, value = "/updateResource/uploadPhotos")
    public ResourceEntity uploadPhotos(@RequestParam(value = "photos") MultipartFile[] photos, @RequestParam("resourceId") Long resourceId) throws ResourceNotFoundException {
        return resourceService.uploadPhotos(resourceId, photos);
    }
    
     @RequestMapping(method = RequestMethod.DELETE, value = "/updateResource/deletePhotos")
    public ResourceEntity deletePhotos(@RequestParam(value = "resourceId") Long resourceId, @RequestParam(value = "photosToDelete") String[] photosToDelete ) throws ResourceNotFoundException,UpdateResourceException,IOException{
        return resourceService.deletePhotos(resourceId, photosToDelete);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateResource/uploadDocuments")
    public ResourceEntity uploadDocuments(@RequestParam(value = "documents") MultipartFile[] documents, @RequestParam(value = "resourceId") Long resourceId) throws ResourceNotFoundException {
        return resourceService.uploadDocuments(resourceId, documents);
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/updateResource/deleteDocuments")
    public ResourceEntity deleteDocuments(@RequestParam(value = "resourceId") Long resourceId, @RequestParam(value = "docsToDelete") String[] docsToDelete ) throws ResourceNotFoundException,UpdateResourceException,IOException{
        return resourceService.deleteDocuments(resourceId, docsToDelete);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/terminateResource")
    public ResourceEntity terminateResource(@RequestParam(value = "resourceId") Long resourceId, @RequestParam(value = "terminatorId") Long terminatorId) throws ResourceNotFoundException, TerminateResourceException {
        return resourceService.terminateResource(resourceId, terminatorId);
    }

    
 
}
