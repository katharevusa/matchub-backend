/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import static com.google.cloud.storage.Storage.GetHmacKeyOption.projectId;
import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.entity.ResourceRequestEntity;
import com.is4103.matchub.entity.ResourceTransactionEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.enumeration.RequestStatusEnum;
import com.is4103.matchub.enumeration.RequestorEnum;
import com.is4103.matchub.exception.CreateResourceRequestException;
import com.is4103.matchub.exception.DeleteResourceRequestException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.ResourceRequestNotFoundException;
import com.is4103.matchub.exception.RespondToResourceRequestException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.repository.ResourceTransactionEntityRepository;
import com.is4103.matchub.vo.ResourceRequestCreateVO;
import com.is4103.matchub.vo.SendNotificationsToUsersVO;
import com.stripe.model.PaymentIntent;
import static io.grpc.internal.ConscryptLoader.isPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class ResourceRequestServiceImpl implements ResourceRequestService {

    @Autowired
    ResourceRequestEntityRepository resourceRequestEntityRepository;

    @Autowired
    ProjectEntityRepository projectEntityRepository;

    @Autowired
    ResourceEntityRepository resourceEntityRepository;

    @Autowired
    ProfileEntityRepository profileEntityRepository;

    @Autowired
    AnnouncementEntityRepository announcementEntityRepository;

    @Autowired
    FirebaseService firebaseService;

    @Autowired
    AnnouncementService announcementService;

    @Autowired
    ResourceTransactionEntityRepository resourceTransactionEntityRepository;

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

        // Only active project can recieving resource request
        if (project.getProjStatus() != ProjectStatusEnum.ACTIVE) {
            throw new CreateResourceRequestException("Sorry action is only allowed for activated projects");
        }

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

        if (resource.getMatchedProjectId() != null) {
            throw new CreateResourceRequestException("This resource is already matched to another project");
        }

        if (vo.getUnitsRequired() > resource.getUnits()) {
            throw new CreateResourceRequestException("Unable to create resource request: the requested amount is more than the resource provided");
        }

        ResourceRequestEntity resourceRequest = new ResourceRequestEntity();
        vo.createResourceRequestProjectOwner(resourceRequest);
        project.getListOfRequests().add(resourceRequest);
        resource.getListOfRequests().add(resourceRequest);
        resourceRequest = resourceRequestEntityRepository.saveAndFlush(resourceRequest);

        // create announcement (notify resource owner)
        ProfileEntity resourceOwner = profileEntityRepository.findById(resource.getResourceOwnerId()).get();
        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTitle("Project '" + project.getProjectTitle() + "' wants your resource " + resource.getResourceName());
        announcementEntity.setContent(resourceRequest.getMessage());
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setType(AnnouncementTypeEnum.REQUEST_FOR_RESOURCE);
        announcementEntity.setResourceRequestId(resourceRequest.getRequestId());
        // association
        announcementEntity.getNotifiedUsers().add(resourceOwner);
        resourceOwner.getAnnouncements().add(announcementEntity);
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification
        announcementService.createNormalNotification(announcementEntity);

        return resourceRequest;

    }

    //for data init only 
    public ResourceRequestEntity createResourceRequestResourceOwner(Long projectId, Long requestorId, Long resourceId, Integer units) throws CreateResourceRequestException {

        System.out.println("Here******: DATA INIT RESOURCE REQUESTS");

        if (!resourceEntityRepository.findById(resourceId).isPresent()) {
            throw new CreateResourceRequestException("Unable to create resource request: resource not found");
        }
        ResourceEntity resource = resourceEntityRepository.findById(resourceId).get();

        if (!projectEntityRepository.findById(projectId).isPresent()) {
            throw new CreateResourceRequestException("Unable to create resource request: project not found");
        }
        ProjectEntity project = projectEntityRepository.findById(projectId).get();

        ResourceRequestEntity resourceRequest = new ResourceRequestEntity();

        resourceRequest.setRequestorId(requestorId);
        resourceRequest.setProjectId(projectId);
        resourceRequest.setResourceId(resourceId);
        resourceRequest.setUnitsRequired(units);
        resourceRequest.setRequestorEnum(RequestorEnum.RESOURCE_OWNER);

        System.out.println("Here******1");
        resourceRequest = resourceRequestEntityRepository.save(resourceRequest);
        project.getListOfRequests().add(resourceRequest);
        resource.getListOfRequests().add(resourceRequest);
        System.out.println("Here******2");
        resourceRequest = resourceRequestEntityRepository.saveAndFlush(resourceRequest);
        System.out.println("Here******3");
        projectEntityRepository.saveAndFlush(project);
        System.out.println("Here******4");
        resourceEntityRepository.saveAndFlush(resource);
        resourceRequest.setMessage("Hello! I would like to make a donation.");

        // create announcement (notify project owner)
        List<ProfileEntity> projectOwners = project.getProjectOwners();
        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        ProfileEntity resourceOwner = profileEntityRepository.findById(resource.getResourceOwnerId()).get();
        String resourceOwnerName = "";
        if (resourceOwner instanceof IndividualEntity) {
            resourceOwnerName = ((IndividualEntity) resourceOwner).getFirstName() + " " + ((IndividualEntity) resourceOwner).getLastName();
        } else if (resourceOwner instanceof OrganisationEntity) {
            resourceOwnerName = ((OrganisationEntity) resourceOwner).getOrganizationName();
        }

        announcementEntity.setTitle(resourceOwnerName + " wants to donate '" + resource.getResourceName() + "' to your project '" + project.getProjectTitle() + "'.");
        announcementEntity.setContent(resourceRequest.getMessage());
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setType(AnnouncementTypeEnum.DONATE_TO_PROJECT);
        announcementEntity.setResourceRequestId(resourceRequest.getRequestId());
        // association
        announcementEntity.getNotifiedUsers().addAll(projectOwners);
        for (ProfileEntity p : projectOwners) {
            p.getAnnouncements().add(announcementEntity);
        }
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification         
        announcementService.createNormalNotification(announcementEntity);

        return resourceRequest;
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

        if (project.getProjStatus() != ProjectStatusEnum.ACTIVE) {
            throw new CreateResourceRequestException("Sorry action is only allowed for activated projects");
        }

        if (!resource.getResourceOwnerId().equals(vo.getRequestorId())) {
            throw new CreateResourceRequestException("Unable to create resource request: can only create request for owned resource");
        }

        // If resource already matched to project, can not request one more time
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
        resourceRequest = resourceRequestEntityRepository.saveAndFlush(resourceRequest);

        // create announcement (notify project owner)
        List<ProfileEntity> projectOwners = project.getProjectOwners();
        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        ProfileEntity resourceOwner = profileEntityRepository.findById(resource.getResourceOwnerId()).get();
        String resourceOwnerName = "";
        if (resourceOwner instanceof IndividualEntity) {
            resourceOwnerName = ((IndividualEntity) resourceOwner).getFirstName() + " " + ((IndividualEntity) resourceOwner).getLastName();
        } else if (resourceOwner instanceof OrganisationEntity) {
            resourceOwnerName = ((OrganisationEntity) resourceOwner).getOrganizationName();
        }

        announcementEntity.setTitle(resourceOwnerName + " wants to donate '" + resource.getResourceName() + "' to your project '" + project.getProjectTitle() + "'.");
        announcementEntity.setContent(resourceRequest.getMessage());
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setType(AnnouncementTypeEnum.DONATE_TO_PROJECT);
        announcementEntity.setResourceRequestId(resourceRequest.getRequestId());
        // association
        announcementEntity.getNotifiedUsers().addAll(projectOwners);
        for (ProfileEntity p : projectOwners) {
            p.getAnnouncements().add(announcementEntity);
        }
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification         
        announcementService.createNormalNotification(announcementEntity);

        return resourceRequest;

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
        // only resource request with on_hold status can be deleted
        if (request.getStatus() != RequestStatusEnum.ON_HOLD) {
            throw new DeleteResourceRequestException("Unable to delete resource request: only request with status on_hold can be deleted");
        }

        ResourceEntity resource = resourceEntityRepository.findById(request.getResourceId()).get();
        ProjectEntity project = projectEntityRepository.findById(request.getProjectId()).get();
        resource.getListOfRequests().remove(request);
        project.getListOfRequests().remove(request);

//        // create announcement (notify project owner)
//        List<ProfileEntity> notifiedUsers = new ArrayList<>();
//        if (request.getRequestorEnum() == RequestorEnum.PROJECT_OWNER) {
//            //notify resource owner
//            notifiedUsers.add(profileEntityRepository.findById(resource.getResourceOwnerId()).get());
//        } else {
//            notifiedUsers.addAll(project.getProjectOwners());
//            //notify project owners
//        }
//
//        AnnouncementEntity announcementEntity = new AnnouncementEntity();
//        announcementEntity.setTitle("One resource request has been terminated");
//        announcementEntity.setContent("project title: " + project.getProjectTitle() + "\n" + "resource name: " + resource.getResourceName());
//        announcementEntity.setTimestamp(LocalDateTime.now());
//        announcementEntity.setType(AnnouncementTypeEnum.DELETE_RESOURCE_REQUEST);
//        
//
//        // association
//        announcementEntity.getNotifiedUsers().addAll(notifiedUsers);
//        for (ProfileEntity p : notifiedUsers) {
//            p.getAnnouncements().add(announcementEntity);
//        }
//        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);
//
//        // create notification         
//        announcementService.createNormalNotification(announcementEntity);
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
        if (request.getStatus() != RequestStatusEnum.ON_HOLD) {
            throw new RespondToResourceRequestException("This resource request is " + request.getStatus() + ". Status can not be changed");
        }

        //response true: accept request
        if (response == true) {
            request.setStatus(RequestStatusEnum.ACCEPTED);
            resource.setMatchedProjectId(request.getProjectId());
            resource.setAvailable(Boolean.FALSE);
            //reject the rest resource request
            for (ResourceRequestEntity r : resource.getListOfRequests()) {
                if (!r.getRequestId().equals(requestId)) {
                    r.setStatus(RequestStatusEnum.REJECTED);
                }
            }

        } else {
            //response false: decline request
            request.setStatus(RequestStatusEnum.REJECTED);
        }

        request = resourceRequestEntityRepository.saveAndFlush(request);

        // create announcement (notify project owner)
        List<ProfileEntity> notifiedUsers = new ArrayList<>();
        if (request.getRequestorEnum() == RequestorEnum.PROJECT_OWNER) {

            //notify project owners
            notifiedUsers.addAll(project.getProjectOwners());

        } else {
            //notify resource owner
            notifiedUsers.add(profileEntityRepository.findById(resource.getResourceOwnerId()).get());
        }

        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setResourceRequestId(request.getRequestId());

        if (request.getRequestorEnum() == RequestorEnum.PROJECT_OWNER) {
            // resource request
            if (request.getStatus() == RequestStatusEnum.ACCEPTED) {
                announcementEntity.setTitle("Resource Request ACCEPTED ");
                announcementEntity.setContent("Your request for resource '" + resource.getResourceName() + "' for project '" + project.getProjectTitle() + "' has been approved");
                announcementEntity.setType(AnnouncementTypeEnum.RESOURCE_REQUEST_ACCEPTED);

            } else {
                announcementEntity.setTitle("Resource Request REJECTED ");
                announcementEntity.setContent("Your request for resource '" + resource.getResourceName() + "' for project '" + project.getProjectTitle() + "' has been declined");
                announcementEntity.setType(AnnouncementTypeEnum.RESOURCE_REQUEST_REJECTED);
            }

        } else {
            // resource donation request
            if (request.getStatus() == RequestStatusEnum.ACCEPTED) {
                announcementEntity.setTitle("Resource Donation ACCEPTED ");
                announcementEntity.setContent("Your donation of resource '" + resource.getResourceName() + "' to project '" + project.getProjectTitle() + "' has been accepted");
                announcementEntity.setType(AnnouncementTypeEnum.RESOURCE_DONATION_ACCEPTED);

            } else {
                announcementEntity.setTitle("Resource Donation REJECTED ");
                announcementEntity.setContent("Your donation of resource '" + resource.getResourceName() + "' to project '" + project.getProjectTitle() + "' has been rejected");
                announcementEntity.setType(AnnouncementTypeEnum.RESOURCE_DONATION_REJECTED);
            }

        }

        // association
        announcementEntity.getNotifiedUsers().addAll(notifiedUsers);
        for (ProfileEntity p : notifiedUsers) {
            p.getAnnouncements().add(announcementEntity);
        }
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification         
        announcementService.createNormalNotification(announcementEntity);

        return request;

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

    //problem
    @Override
    public List<ResourceTransactionEntity> getResourceTransactionForOwnedResources(Long userId) {
        ProfileEntity user = profileEntityRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<ResourceEntity> resources = user.getHostedResources();
        List<ResourceTransactionEntity> transactions = new ArrayList<>();
        for (ResourceEntity resource : resources) {
            if (resource.getResourceTransaction() != null) {
                transactions.add(resource.getResourceTransaction());
            }
        }
        return transactions;
    }

    @Override
    public List<ResourceTransactionEntity> getResourceTransactionForConsumedResources(Long userId) {
        ProfileEntity user = profileEntityRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return resourceTransactionEntityRepository.getResourceTransactionsByPayerId(userId);
    }

    @Override
    public ResourceTransactionEntity createResourceTransaction(String payerEmail, PaymentIntent paymentIntent) throws ResourceNotFoundException, ProjectNotFoundException {
        ResourceTransactionEntity transactionEntity = new ResourceTransactionEntity();
        transactionEntity.setAmountPaid(BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100)));
        ProfileEntity payer = profileEntityRepository.findByEmail(payerEmail).orElseThrow(() -> new UserNotFoundException(payerEmail));
        transactionEntity.setPayerId(payer.getAccountId());
        transactionEntity.setTransactionTime(LocalDateTime.now());

        ResourceEntity resource = resourceEntityRepository.findById(Long.parseLong(paymentIntent.getMetadata().get("resource_id"))).orElseThrow(() -> new ResourceNotFoundException());
        ProjectEntity project = projectEntityRepository.findById(Long.parseLong(paymentIntent.getMetadata().get("project_id"))).orElseThrow(() -> new ProjectNotFoundException());

        resource.setResourceTransaction(transactionEntity);
        
        transactionEntity.setResource(resource);

        project.getListOfResourceTransactions().add(transactionEntity);
        transactionEntity.setProject(project);    
        resource.setAvailable(false);
        
        resourceEntityRepository.save(resource);
        projectEntityRepository.flush();
        return resourceTransactionEntityRepository.saveAndFlush(transactionEntity);
    }

}
