/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import static com.google.cloud.storage.Storage.GetHmacKeyOption.projectId;
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
import static io.grpc.internal.ConscryptLoader.isPresent;
import java.util.ArrayList;
import java.util.List;
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

        if (!project.getProjectOwners().contains(profileEntityRepository.findById(vo.getRequestorId()).get())) {
            throw new CreateResourceRequestException("Unable to create resource request: can only create resource request for owned projects");
        }

        // between resource and project, there can only be one on_hold request 
        if (resourceRequestEntityRepository.searchResourceRequestProjectByProjectAndResourceOnHold(project.getProjectId(), resource.getResourceId(), RequestStatusEnum.ON_HOLD).isPresent()) {
            throw new CreateResourceRequestException("There exits one request between resource and project with status on_hold, please make a decision before creating a new request");
        }

        if (vo.getUnitsRequired() > resource.getUnits()) {
            throw new CreateResourceRequestException("Unable to create resource request: the requested amount is more than the resource provided");
        }

        ResourceRequestEntity resourceRequest = new ResourceRequestEntity();
        vo.createResourceRequestProjectOwner(resourceRequest);
        System.err.println(resourceRequest);
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

        if (!resource.getResourceOwnerId().equals(vo.getRequestorId())) {
            throw new CreateResourceRequestException("Unable to create resource request: can only create request for owned resource");
        }

        if (resource.getMatchedProjectId() != null) {
            throw new CreateResourceRequestException("This resource is already matched to another project");
        }

        if (vo.getUnitsRequired() > resource.getUnits()) {
            throw new CreateResourceRequestException("Unable to create resource request: the requested amount is more than the resource provided");
        }

        // between resource and project, there can only be one on_hold request 
        if (resourceRequestEntityRepository.searchResourceRequestProjectByProjectAndResourceOnHold(project.getProjectId(), resource.getResourceId(), RequestStatusEnum.ON_HOLD).isPresent()) {
            throw new CreateResourceRequestException("There exits one request between resource and project with status on_hold, please make a decision before creating a new request");
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

        //Only resource request with ON_HOLD status can change status
        if(request.getStatus()!=RequestStatusEnum.ON_HOLD){
            throw new RespondToResourceRequestException("This resource request is "+ request.getStatus()+". Status can not be changed");
        }
        
        
        //response true: accept request
        if (response == true) {
            request.setStatus(RequestStatusEnum.ACCEPTED);
            resource.setMatchedProjectId(request.getProjectId());
            resource.setAvailable(Boolean.FALSE);
            //reject the rest resource request
            for(ResourceRequestEntity r: resource.getListOfRequests()){
                if(!r.getRequestId().equals(requestId)){
                    r.setStatus(RequestStatusEnum.REJECTED);
                }
            }

        } else {
            //response false: decline request
            request.setStatus(RequestStatusEnum.REJECTED);
        }
        return resourceRequestEntityRepository.saveAndFlush(request);

    }

    // get project owner's all incoming resource donation requests 
    @Override
    public List<ResourceRequestEntity> getAllIncomingResourceDonationRequests(Long userId) {
        Optional<ProfileEntity> userOptional = profileEntityRepository.findById(userId);
        ProfileEntity user = userOptional.get();
        List<ResourceRequestEntity> resourceRequests = new ArrayList<>();
        for (ProjectEntity p : user.getProjectsOwned()) {
            for (ResourceRequestEntity rr : p.getListOfRequests()) {
                if (rr.getRequestorEnum() == RequestorEnum.RESOURCE_OWNER) {
                    resourceRequests.add(rr);
                }
            }

        }
        return resourceRequests;
    }

    //get project owner's all outgoing resource requests
    @Override
    public List<ResourceRequestEntity> getAllOutgoingResourceRequests(Long userId) {
        Optional<ProfileEntity> userOptional = profileEntityRepository.findById(userId);
        ProfileEntity user = userOptional.get();
        List<ResourceRequestEntity> resourceRequests = new ArrayList<>();
        for (ProjectEntity p : user.getProjectsOwned()) {
            for (ResourceRequestEntity rr : p.getListOfRequests()) {
                if (rr.getRequestorEnum() == RequestorEnum.PROJECT_OWNER) {
                    resourceRequests.add(rr);
                }
            }

        }
        return resourceRequests;
    }

    //get resoure owner's all incoming resource requests
    @Override
    public List<ResourceRequestEntity> getAllIncomingResourceRequests(Long userId) {
        Optional<ProfileEntity> userOptional = profileEntityRepository.findById(userId);
        ProfileEntity user = userOptional.get();
        List<ResourceRequestEntity> resourceRequests = new ArrayList<>();
        for (ResourceEntity p : user.getHostedResources()) {
            for (ResourceRequestEntity rr : p.getListOfRequests()) {
                if (rr.getRequestorEnum() == RequestorEnum.PROJECT_OWNER) {
                    resourceRequests.add(rr);
                }
            }

        }
        return resourceRequests;
    }

    //get resource owner's all outgoing donation requests
    @Override
    public List<ResourceRequestEntity> getAllOutgoingDonationRequests(Long userId) {
        Optional<ProfileEntity> userOptional = profileEntityRepository.findById(userId);
        ProfileEntity user = userOptional.get();
        List<ResourceRequestEntity> resourceRequests = new ArrayList<>();
        for (ResourceEntity r : user.getHostedResources()) {
            for (ResourceRequestEntity rr : r.getListOfRequests()) {
                if (rr.getRequestorEnum() == RequestorEnum.RESOURCE_OWNER) {
                    resourceRequests.add(rr);
                }
            }

        }
        return resourceRequests;
    }

}
