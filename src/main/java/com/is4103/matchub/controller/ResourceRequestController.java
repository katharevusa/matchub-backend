/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ResourceRequestEntity;
import com.is4103.matchub.exception.CreateResourceRequestException;
import com.is4103.matchub.exception.DeleteResourceRequestException;
import com.is4103.matchub.exception.ResourceRequestNotFoundException;
import com.is4103.matchub.exception.RespondToResourceRequestException;
import com.is4103.matchub.service.ResourceRequestService;
import com.is4103.matchub.vo.ResourceRequestCreateVO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author longluqian
 */
@RestController
@RequestMapping("/authenticated")
public class ResourceRequestController {

    @Autowired
    ResourceRequestService resourceRequestService;

    // create resource request: Project owner initiate request (projectownerId, projectId, resourceId, 
    @RequestMapping(method = RequestMethod.POST, value = "/createNewResourceRequestByProjectOwner")
    ResourceRequestEntity createNewResourceRequestByProjectOwner(@Valid @RequestBody ResourceRequestCreateVO vo) throws CreateResourceRequestException {
        return resourceRequestService.createResourceRequestProjectOwner(vo);
    }

    //Create Resource Donation Request by resource owner
    @RequestMapping(method = RequestMethod.POST, value = "/createNewResourceRequestByResourceOwner")
    ResourceRequestEntity createNewResourceRequestByResourceOwner(@Valid @RequestBody ResourceRequestCreateVO vo) throws CreateResourceRequestException {
        return resourceRequestService.createResourceRequestResourceOwner(vo);
    }

    //delete resource request
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteResourceRequest")
    void deleteResourceRequest(@RequestParam(value = "requestId", required = true) Long requestId, @RequestParam(value = "terminatorId", required = true) Long terminatorId) throws DeleteResourceRequestException {
        resourceRequestService.terminateResourceRequest(requestId, terminatorId);
    }

    //get resource request by id 
    @RequestMapping(method = RequestMethod.GET, value = "/getResourceRequestById")
    void getResourceRequestById(@RequestParam(value = "requestId", required = true) Long requestId) throws ResourceRequestNotFoundException {
        resourceRequestService.getResourceRequestById(requestId);
    }

    //get resource requests by requestor id
    @RequestMapping(method = RequestMethod.GET, value = "/getResourceRequestByRequestorId")
    void getResourceRequestByRequestorId(@RequestParam(value = "requestorId", required = true) Long requestorId, @RequestParam(value = "pageable", required = true) Pageable page) throws ResourceRequestNotFoundException {
        resourceRequestService.getResourceRequestByRequestorId(requestorId, page);
    }

    // get resource requests by resource Id 
    @RequestMapping(method = RequestMethod.GET, value = "/getResourceRequestByResourceId")
    void getResourceRequestByResourceId(@RequestParam(value = "resourceId", required = true) Long resourceId, @RequestParam(value = "pageable", required = true) Pageable page) throws ResourceRequestNotFoundException {
        resourceRequestService.getResourceRequestByResourceId(resourceId, page);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getResourceRequestByProjectId")
    void getResourceRequestByProjectId(@RequestParam(value = "projectId", required = true) Long projectId, @RequestParam(value = "pageable", required = true) Pageable page) throws ResourceRequestNotFoundException {
        resourceRequestService.getResourceRequestByProjectId(projectId, page);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/respondToResourceRequest")
    void respondToResourceRequest(@RequestParam(value = "requestId", required = true) Long requestId, @RequestParam(value = "responderId", required = true) Long responderId, @RequestParam(value = "response", required = true) boolean response) throws ResourceRequestNotFoundException, RespondToResourceRequestException {
        resourceRequestService.respondToResourceRequest(requestId,responderId, response);
    }

    //Terminate Resource Request
    //View Resource Request
    //Respond to Resource Request
    // Terminate Donation Resource Request
    //View Resource Donation Request
}
