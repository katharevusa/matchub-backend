/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.TerminateResourceException;
import com.is4103.matchub.exception.UpdateResourceException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ResourceCategoryEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.vo.ResourceVO;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author longluqian
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    ResourceEntityRepository resourceEntityRepository;
    @Autowired
    ResourceCategoryService resourceCategoryService;
    @Autowired
    ProfileEntityRepository profileEntityRepository;

    @Autowired
    ResourceCategoryEntityRepository resourceCategoryEntityRepository;

    @Autowired
    AttachmentService attachmentService;

    @Override
    public ResourceEntity createResource(ResourceVO vo) throws ResourceCategoryNotFoundException, UserNotFoundException {
        ResourceEntity newResource = new ResourceEntity();
        vo.createResource(newResource);
        Optional<ResourceCategoryEntity> categoryOptional = resourceCategoryEntityRepository.findById(newResource.getResourceCategoryId());

        // resource category association
        ResourceCategoryEntity category;
        if (categoryOptional.isPresent()) {
            category = categoryOptional.get();
        } else {
            throw new ResourceCategoryNotFoundException("Unable to find resource category");
        }

        //owner association
        ProfileEntity profile;
        Optional<ProfileEntity> profileOptional = profileEntityRepository.findById(newResource.getResourceOwnerId());
        if (profileOptional.isPresent()) {
            profile = profileOptional.get();
        } else {
            throw new UserNotFoundException("Unable to find user");
        }
        category.getResources().add(newResource);
        profile.getHostedResources().add(newResource);

        newResource = resourceEntityRepository.saveAndFlush(newResource);

        return newResource;

    }

    //This creation method is only for current data init
    @Override
    public ResourceEntity createResource(ResourceEntity resourceEntity, Long categoryId, Long profileId) {
        ResourceCategoryEntity category = resourceCategoryService.getResourceCategoryById(categoryId);
        ProfileEntity profileEntity = profileEntityRepository.findById(profileId).get();

        resourceEntity.setResourceOwnerId(profileId);
        resourceEntity.setResourceCategoryId(categoryId);

        category.getResources().add(resourceEntity);
        profileEntity.getHostedResources().add(resourceEntity);
        resourceEntity = resourceEntityRepository.saveAndFlush(resourceEntity);

        return resourceEntity;
    }

    public ResourceEntity updateResource(ResourceVO vo, Long updaterId, Long resourceId) throws ResourceNotFoundException, UpdateResourceException {
        Optional<ResourceEntity> oldResourceOptional = resourceEntityRepository.findById(resourceId);

        ResourceEntity oldResource;
        if (oldResourceOptional.isPresent()) {
            oldResource = oldResourceOptional.get();
        } else {
            throw new ResourceNotFoundException("Unable to update resource: resource not found");
        }

        if (oldResource.getResourceOwnerId().equals(updaterId)) {

            //delete resource from old category
            Optional<ResourceCategoryEntity> categoryOptional = resourceCategoryEntityRepository.findById(oldResource.getResourceCategoryId());
            categoryOptional.get().getResources().remove(oldResource);

            vo.updateResource(oldResource);
            categoryOptional = resourceCategoryEntityRepository.findById(oldResource.getResourceCategoryId());
            categoryOptional.get().getResources().add(oldResource);
            //add resource to the updated category 
            oldResource = resourceEntityRepository.saveAndFlush(oldResource);
            return oldResource;
        } else {
            throw new UpdateResourceException("Only resource owner can update this resource");
        }

    }

    @Override
    public Page<ResourceEntity> getAllAvailableResources(Pageable pageable) {
        return resourceEntityRepository.getAllAvailableResources(pageable);
    }

    @Override
    public Page<ResourceEntity> getAllResources(Pageable pageble) {
        return resourceEntityRepository.findAll(pageble);
    }

    public ResourceEntity getResourceById(Long id) throws ResourceNotFoundException {

        Optional<ResourceEntity> resourceEntity = resourceEntityRepository.findById(id);
        if (resourceEntity.isPresent()) {
            return resourceEntity.get();
        } else {
            throw new ResourceNotFoundException("Resource is not found");
        }

    }

    @Override
    public Page<ResourceEntity> getHostedResources(Long profileId, Pageable pageable) {
        return resourceEntityRepository.getHostedResources(profileId, pageable);
    }

    @Override
    public ResourceEntity setResourceProfilePic(Long resourceId, MultipartFile pic) throws ResourceNotFoundException {
        Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException();
        }
        ResourceEntity resource = resourceOptional.get();
        String path = attachmentService.upload(pic);
        resource.setResourceProfilePic(path);

        return resourceEntityRepository.saveAndFlush(resource);
    }
    
     @Override 
    public ResourceEntity deleteResourceProfilePic(Long resourceId)throws ResourceNotFoundException,UpdateResourceException, IOException  {
        Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource not found");
        }
        ResourceEntity resource = resourceOptional.get();
        if(resource.getResourceProfilePic()== null){
            throw new UpdateResourceException("Unable to delete profile picture: the resource currently does not have a profile picture  ");
        }
        attachmentService.deleteFile(resource.getResourceProfilePic());
        resource.setResourceProfilePic(null);
        
        return resourceEntityRepository.saveAndFlush(resource);
        
    }
    

    @Override
    public ResourceEntity uploadPhotos(Long resourceId, MultipartFile[] photos) throws ResourceNotFoundException {
        Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException();
        }
        ResourceEntity resource = resourceOptional.get();

        for (MultipartFile photo : photos) {
            String path = attachmentService.upload(photo);
            resource.getPhotos().add(path);

        }
        return resourceEntityRepository.saveAndFlush(resource);
    }
    @Override
    public ResourceEntity deletePhotos (Long resourceId, String[] photoToDelete)throws ResourceNotFoundException, IOException, UpdateResourceException{
        Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource not exist");
        }
        ResourceEntity resource = resourceOptional.get();
        
        for(String s : photoToDelete){
            if(!resource.getPhotos().contains(s)){
                throw new UpdateResourceException("Unable to delete photos: photos not found");
            }
        }
        
        for(String s: photoToDelete){
            resource.getPhotos().remove(s);
            attachmentService.deleteFile(s);      
        }
        
        return resourceEntityRepository.saveAndFlush(resource);     
    }

    @Override
    public ResourceEntity uploadDocuments(Long resourceId, MultipartFile[] documents) throws ResourceNotFoundException {
        Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource not exist");
        }
        ResourceEntity resource = resourceOptional.get();

        for (MultipartFile photo : documents) {
            String path = attachmentService.upload(photo);
            String name = photo.getOriginalFilename();
            System.err.println("name: " + name);
            resource.getDocuments().put(name, path);

        }
        return resourceEntityRepository.saveAndFlush(resource);
    }
    
    @Override 
    public ResourceEntity deleteDocuments(Long resourceId, String[] docsToDelete) throws IOException,ResourceNotFoundException, UpdateResourceException {
       Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource not exist");
        }
        ResourceEntity resource = resourceOptional.get();
        Map<String, String> hashmap = resource.getDocuments();

            //loop 1: check if all the documents are present
            for (String key : docsToDelete) {
                //get the path of the document to delete
                String selectedDocumentPath = hashmap.get(key);
                if (selectedDocumentPath == null) {
                    throw new UpdateResourceException("Unable to delete resource document (Document not found): " + key);
                }
            }
            
            //loop2: delete the actual file when all files are present
            for (String key : docsToDelete) {
                //get the path of the document to delete
                String selectedDocumentPath = hashmap.get(key);
                //if file is present, call attachmentService to delete the actual file from /build folder
                attachmentService.deleteFile(selectedDocumentPath);

                //successfully removed the actual file from /build folder, update organisation hashmap
                hashmap.remove(key);
            }
            
            //save once all documents are removed successfully
            resource.setDocuments(hashmap);
            return resourceEntityRepository.saveAndFlush(resource);
            
        
    }
    
    @Override
    public ResourceEntity terminateResource(Long resourceId, Long terminatorId)throws  ResourceNotFoundException, TerminateResourceException{
        Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource not exist");
        }
        ResourceEntity resource = resourceOptional.get();
        
        if (!resource.getResourceOwnerId().equals(terminatorId)){
            throw new TerminateResourceException("Only resource owner can terminate this resource");
        }
        
        if(resource.getMatchedProjectId()!= null){
            throw new TerminateResourceException("This resource is already matched with another project hence can not be terminated");
        }
        if(!resource.getListOfRequests().isEmpty()){
            throw new TerminateResourceException("This resource is used in some resource requests hence can not be deleted");
        }
        
        resource.setAvailable(Boolean.FALSE);
        return resourceEntityRepository.saveAndFlush(resource);
        
    }

}
