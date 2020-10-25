/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.TerminateResourceException;
import com.is4103.matchub.exception.UnableToSaveResourceException;
import com.is4103.matchub.exception.UpdateResourceException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceCategoryEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.vo.ResourceVO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    
    @Autowired
    ProjectEntityRepository projectEntityRepository;

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

    @Override
    public Page<ResourceEntity> resourceGlobalSearch(String keyword, List<Long> categoryIds, Boolean availability, String startTimeStr, String endTimeStr, String country, Pageable pageable) {
        List<ResourceEntity> initResource = new ArrayList();
        if (keyword.equals("")) {
            System.err.println("key word is null");
            initResource = resourceEntityRepository.findAll();
        } else {
            String[] keywords = keyword.split(" ");
            Set<ResourceEntity> temp = new HashSet<>();
            for (String s : keywords) {
                temp.addAll(resourceEntityRepository.getResourcesByKeyword(s));
            }

            for (ResourceEntity r : temp) {
                initResource.add(r);
            }
        }

        //filter by availability 
        List<ResourceEntity> resultFilterByAvailability = new ArrayList();
        if (availability != null) {
            for (ResourceEntity r : initResource) {
                if (availability == r.isAvailable()) {
                    resultFilterByAvailability.add(r);
                }
            }
        } else {
            resultFilterByAvailability = initResource;
        }

        //filter by categoryIds
        List<ResourceEntity> resultFilterByCategories = new ArrayList();
        if (!categoryIds.isEmpty()) {
            for (ResourceEntity r : resultFilterByAvailability) {
                if (categoryIds.contains(r.getResourceCategoryId())) {
                    resultFilterByCategories.add(r);
                }

            }
        } else {
            resultFilterByCategories = resultFilterByAvailability;
        }

        //filter by start date and end date  
        List<ResourceEntity> resultFilterByDate = new ArrayList();
        if (!startTimeStr.equals("")&& !endTimeStr.equals("")) {
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr);
            for (ResourceEntity r : resultFilterByCategories) {
                if (!(r.getStartTime().isBefore(startTime)&& r.getEndTime().isBefore(startTime)) &&
                     !(r.getStartTime().isAfter(endTime)&& r.getEndTime().isAfter(endTime))) {
                    resultFilterByDate.add(r);
                }

            }

        } else {
            resultFilterByDate = resultFilterByCategories;
        }
        

        //filter by country
        List<ResourceEntity> resultFilterByCountry = new ArrayList();
        if (!country.equals("")) {
            for (ResourceEntity r : resultFilterByDate) {
                if (r.getCountry().equals(country)) {
                    resultFilterByCountry.add(r);
                }
            }
        } else {
            resultFilterByCountry = resultFilterByDate;
        }

        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > resultFilterByCountry.size() ? resultFilterByCountry.size() : (start + pageable.getPageSize());
        Page<ResourceEntity> pages = new PageImpl<ResourceEntity>(resultFilterByCountry.subList(start.intValue(), end.intValue()), pageable, resultFilterByCountry.size());

        return pages;
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

    @Override
    public ResourceEntity getResourceById(Long id) throws ResourceNotFoundException {

        Optional<ResourceEntity> resourceEntity = resourceEntityRepository.findById(id);
        if (resourceEntity.isPresent()) {
            return resourceEntity.get();
        } else {
            throw new ResourceNotFoundException("Resource is not found");
        }

    }

    @Override
    public List<ResourceEntity> getResourcesByListOfId(List<Long> ids) throws ResourceNotFoundException {
        List<ResourceEntity> listOfResources = new ArrayList<>();
        ResourceEntity resourceEntity;
        for (Long id : ids) {
            Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(id);
            if (resourceOptional.isPresent()) {
                resourceEntity = resourceOptional.get();
                listOfResources.add(resourceEntity);
            } else {
                throw new ResourceNotFoundException("Resource is not found");
            }

        }
        return listOfResources;

    }

    @Override
    public Page<ResourceEntity> searchResourceByKeywords(String keyword, Pageable pageable) {
        return resourceEntityRepository.getResourcesByKeyword(keyword, pageable);
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
    public ResourceEntity deleteResourceProfilePic(Long resourceId) throws ResourceNotFoundException, UpdateResourceException, IOException {
        Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource not found");
        }
        ResourceEntity resource = resourceOptional.get();
        if (resource.getResourceProfilePic() == null) {
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
    public ResourceEntity deletePhotos(Long resourceId, String[] photoToDelete) throws ResourceNotFoundException, IOException, UpdateResourceException {
        Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource not exist");
        }
        ResourceEntity resource = resourceOptional.get();

        for (String s : photoToDelete) {
            if (!resource.getPhotos().contains(s)) {
                throw new UpdateResourceException("Unable to delete photos: photos not found");
            }
        }

        for (String s : photoToDelete) {
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
    public ResourceEntity deleteDocuments(Long resourceId, String[] docsToDelete) throws IOException, ResourceNotFoundException, UpdateResourceException {
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
    public ResourceEntity terminateResource(Long resourceId, Long terminatorId) throws ResourceNotFoundException, TerminateResourceException {
        Optional<ResourceEntity> resourceOptional = resourceEntityRepository.findById(resourceId);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource not exist");
        }
        ResourceEntity resource = resourceOptional.get();

        if (!resource.getResourceOwnerId().equals(terminatorId)) {
            throw new TerminateResourceException("Only resource owner can terminate this resource");
        }

        if (resource.getMatchedProjectId() != null) {
            throw new TerminateResourceException("This resource is already matched with another project hence can not be terminated");
        }
        if (!resource.getListOfRequests().isEmpty()) {
            throw new TerminateResourceException("This resource is used in some resource requests hence can not be deleted");
        }

        resource.setAvailable(Boolean.FALSE);
        return resourceEntityRepository.saveAndFlush(resource);

    }

    @Override
    public List<ResourceEntity> getMatchedResourcesByProjectId(Long projectId) {
        List<ResourceEntity> resources = new ArrayList<>();
        resources = resourceEntityRepository.getMatchedResourcesByProjectId(projectId);
        return resources;
    }

    

}
