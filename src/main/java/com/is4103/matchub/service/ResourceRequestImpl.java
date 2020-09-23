/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.entity.ResourceRequestEntity;
import com.is4103.matchub.enumeration.RequestStatusEnum;
import com.is4103.matchub.enumeration.RequestorEnum;
import com.is4103.matchub.exception.CreateResourceRequestException;
import com.is4103.matchub.exception.DeleteResourceRequestException;
import com.is4103.matchub.exception.ResourceRequestNotFoundException;
import com.is4103.matchub.exception.RespondToResourceRequestException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.vo.ResourceRequestCreateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class ResourceRequestImpl implements ResourceRequestService {

    @Autowired
    ResourceRequestEntityRepository resourceRequestEntityRepository;

    @Autowired
    ProjectEntityRepository projectEntityRepository;

    @Autowired
    ResourceEntityRepository resourceEntityRepository;

    @Autowired
    ProfileEntityRepository profileEntityRepository;

    // create resource request: Project owner initiate request (projectownerId, projectId, resourceId, unitsRequired
    @Override
    public ResourceRequestEntity createResourceRequestProjectOwner(ResourceRequestCreateVO vo) throws CreateResourceRequestException {
        if (!resourceEntityRepository.findById(vo.getResourceId()).isPresent()) {
            throw new CreateResourceRequestException("Unable to create resource request: resource not found");
        }
        ResourceEntity resource = resourceEntityRepository.findById(vo.getResourceId()).get();

        if (!projectEntityRepository.findById(vo.getProjectId()).isPresent()) {
            throw new CreateResourceRequestException("Unable to create resource request: project not found");
        }
        ProjectEntity project = projectEntityRepository.findById(vo.getProjectId()).get();

        if (!profileEntityRepository.findById(vo.getRequestorId()).isPresent()) {
            throw new CreateResourceRequestException("Unable to create resource request: requestor not found");
        }

        ResourceRequestEntity resourceRequest = new ResourceRequestEntity();
        vo.createResourceRequestProjectOwner(resourceRequest);
        project.getListOfRequests().add(resourceRequest);
        resource.getListOfRequests().add(resourceRequest);
        return resourceRequestEntityRepository.saveAndFlush(resourceRequest);
    }

    //Create Resource Donation Request
    @Override
    public ResourceRequestEntity createResourceRequestResourceOwner(ResourceRequestCreateVO vo) throws CreateResourceRequestException {
        if (!resourceEntityRepository.findById(vo.getResourceId()).isPresent()) {
            throw new CreateResourceRequestException("Unable to create resource request: resource not found");
        }
        ResourceEntity resource = resourceEntityRepository.findById(vo.getResourceId()).get();

        if (!projectEntityRepository.findById(vo.getProjectId()).isPresent()) {
            throw new CreateResourceRequestException("Unable to create resource request: project not found");
        }
        ProjectEntity project = projectEntityRepository.findById(vo.getProjectId()).get();

        if (!profileEntityRepository.findById(vo.getRequestorId()).isPresent()) {
            throw new CreateResourceRequestException("Unable to create resource request: requestor not found");
        }

        ResourceRequestEntity resourceRequest = new ResourceRequestEntity();
        vo.createResourceRequestResourceOwner(resourceRequest);
        project.getListOfRequests().add(resourceRequest);
        resource.getListOfRequests().add(resourceRequest);
        return resourceRequestEntityRepository.saveAndFlush(resourceRequest);

    }

    // terminate resource request
    @Override
    public void terminateResourceRequest(Long requestId, Long terminatorId) throws DeleteResourceRequestException {
        //Check if terminator exists
        if (!profileEntityRepository.findById(terminatorId).isPresent()) {
            throw new DeleteResourceRequestException("Unable to create resource request: requestor not found");
        }
        //check if resource request exists
        if (!resourceRequestEntityRepository.findById(requestId).isPresent()) {
            throw new DeleteResourceRequestException("Unable to delete resource request: request not found");
        }
        ResourceRequestEntity request = resourceRequestEntityRepository.findById(requestId).get();
        if (!request.getRequestorId().equals(terminatorId)) {
            throw new DeleteResourceRequestException("Unable to delete resource request: only request creator can delete request");
        }
        // only project with on_hold status can be deleted
        if (request.getStatus() != RequestStatusEnum.ON_HOLD) {
            throw new DeleteResourceRequestException("Unable to delete resource request: only request with status on_hold can be deleted");
        }

        ResourceEntity resource = resourceEntityRepository.findById(request.getResourceId()).get();
        ProjectEntity project = projectEntityRepository.findById(request.getProjectId()).get();
        resource.getListOfRequests().remove(request);
        project.getListOfRequests().remove(request);

        resourceRequestEntityRepository.delete(request);

    }

    @Override
    public ResourceRequestEntity getResourceRequestById(Long requestId) throws ResourceRequestNotFoundException {
        if (!resourceRequestEntityRepository.findById(requestId).isPresent()) {
            throw new ResourceRequestNotFoundException("Unable to retrieve resource request: request not found");
        }
        ResourceRequestEntity request = resourceRequestEntityRepository.findById(requestId).get();
        return request;

    }

    @Override
    public Page<ResourceRequestEntity> getResourceRequestByRequestorId(Long requestorId, Pageable pageable) {
        return resourceRequestEntityRepository.getResourceRequestByRequestorId(requestorId, pageable);
    }

    @Override
    public Page<ResourceRequestEntity> getResourceRequestByResourceId(Long resourceId, Pageable pageable) {
        return resourceRequestEntityRepository.getResourceRequestByResourceId(resourceId, pageable);
    }

    @Override
    public Page<ResourceRequestEntity> getResourceRequestByProjectId(Long projectId, Pageable pageable) {
        return resourceRequestEntityRepository.getResourceRequestByProjectId(projectId, pageable);
    }

    //Respond to Resource Request
    @Override
    public ResourceRequestEntity respondToResourceRequest(Long requestId, Long responderId, boolean response) throws RespondToResourceRequestException {
        //get request
        ResourceRequestEntity request = resourceRequestEntityRepository.findById(requestId).get();
        ResourceEntity resource = resourceEntityRepository.findById(request.getResourceId()).get();
        ProjectEntity project = projectEntityRepository.findById(request.getProjectId()).get();
        ProfileEntity responder = profileEntityRepository.findById(responderId).get();
        // if requestor is projectOwner, check resource.ownerId = responderId
        if (request.getRequestorEnum().equals(RequestorEnum.PROJECT_OWNER)) {
            if (!resource.getResourceOwnerId().equals(responderId)) {
                throw new RespondToResourceRequestException("Only resource owner can respond to the request");
            }
        }
        //if requestor is resourceOwner, check responderId == one of the projectOwner of the project
        if (request.getRequestorEnum().equals(RequestorEnum.RESOURCE_OWNER)) {
            if (!project.getProjectOwners().contains(responder)) {
                throw new RespondToResourceRequestException("Only project owners can respond to the request");
            }
        }

        //response true: accept request
        if (response == true) {
            request.setStatus(RequestStatusEnum.ACCEPTED);
            resource.setMatchedProjectId(request.getProjectId());

        } else {
            //response false: decline request
            request.setStatus(RequestStatusEnum.REJECTED);
        }
        return resourceRequestEntityRepository.saveAndFlush(request);
       
    }

}
