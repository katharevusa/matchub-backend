/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import com.is4103.matchub.exception.CreateAnnouncementException;
import com.is4103.matchub.exception.DeleteAnnouncementException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.vo.AnnouncementVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.vo.AnnouncementSettingVO;
import com.is4103.matchub.vo.SendNotificationsToUsersVO;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author longluqian
 */
@Service
public class AnnouncementImpl implements AnnouncementService {

    @Autowired
    ProfileEntityRepository profileEntityRepository;

    @Autowired
    AnnouncementEntityRepository announcementEntityRepository;

    @Autowired
    ProjectEntityRepository projectEntityRepository;
    
    @Autowired
    FirebaseService firebaseService;
    
    @Autowired
    ResourceRequestEntityRepository resourceRequestEntityRepository;

    // create project public announcement, only by project owners, associate with project, notify project followers
    @Override
    public AnnouncementEntity createProjectPublicAnnouncement(AnnouncementVO newAnnouncementVO) throws CreateAnnouncementException{
        System.err.println("New announcement VO:"+ newAnnouncementVO);
        AnnouncementEntity newAnnouncementEntity = new AnnouncementEntity();
        newAnnouncementVO.createProjectPublicAnnouncement(newAnnouncementEntity);
        System.err.println(newAnnouncementEntity.getProjectId());
        ProjectEntity project = projectEntityRepository.findById(newAnnouncementEntity.getProjectId()).get();
        ProfileEntity creator = profileEntityRepository.findById(newAnnouncementEntity.getCreatorId()).get();
        if(!project.getProjectOwners().contains(creator)){
            throw new CreateAnnouncementException("Only project owners can create project public announcement");
        }
        
        newAnnouncementEntity.getNotifiedUsers().addAll(project.getProjectFollowers());
        newAnnouncementEntity.setProjectId(newAnnouncementEntity.getProjectId());
        newAnnouncementEntity = announcementEntityRepository.save(newAnnouncementEntity);
        
        for(ProfileEntity p : project.getProjectFollowers()){
            p.getAnnouncements().add(newAnnouncementEntity);
        }
        //Incomplete: getFollowers and notify them        
        SendNotificationsToUsersVO sendNotificationsToUsersVO = new SendNotificationsToUsersVO();
        sendNotificationsToUsersVO.setTitle(newAnnouncementEntity.getTitle());
        sendNotificationsToUsersVO.setBody(newAnnouncementEntity.getContent());
        sendNotificationsToUsersVO.setType(newAnnouncementEntity.getType().toString());
        sendNotificationsToUsersVO.setImage("");
        List<String> uuids = new ArrayList<>();
        
        for(ProfileEntity p: newAnnouncementEntity.getNotifiedUsers()){
           uuids.add(p.getUuid().toString());
        }     
        sendNotificationsToUsersVO.setUuids(uuids);
        firebaseService.sendNotificationsToUsers(sendNotificationsToUsersVO);      
        return newAnnouncementEntity;
    }
    
    @Override
    public List<AnnouncementEntity> viewProjectPublicAnnouncements(Long projectId){
        return announcementEntityRepository.searchProjectAnnouncementProjectIdAndType(projectId, AnnouncementTypeEnum.PROJECT_PUBLIC_ANNOUNCEMENT);
          
    }
    
    // delete public announcement by project owners only  
    @Override
    public void deleteProjectPublicAnnouncement(Long announcementId,Long userId) throws DeleteAnnouncementException{
        AnnouncementEntity announcement = announcementEntityRepository.findById(announcementId).get();
        ProjectEntity project = projectEntityRepository.findById(announcement.getProjectId()).get();
        ProfileEntity creator = profileEntityRepository.findById(userId).get();    
        if(!project.getProjectOwners().contains(creator)){
            throw new DeleteAnnouncementException("Only project owners can delete project public announcement");
        }
        announcementEntityRepository.delete(announcement);      
    }
    
    
    
    // Create Project Internal Announcement, associate with project teammates and owners, created by project owners only
    @Override
    public AnnouncementEntity createProjectInternalAnnouncement(AnnouncementVO newAnnouncementVO) throws CreateAnnouncementException{
        AnnouncementEntity newAnnouncementEntity = new AnnouncementEntity();
        newAnnouncementVO.createProjectInternalAnnouncement(newAnnouncementEntity);   
        ProjectEntity project = projectEntityRepository.findById(newAnnouncementEntity.getProjectId()).get();
        ProfileEntity creator = profileEntityRepository.findById(newAnnouncementEntity.getCreatorId()).get();
        if(!project.getProjectOwners().contains(creator)){
            throw new CreateAnnouncementException("Only project owners can create project public announcement");
        }
        
        List<ProfileEntity> projectTeammembers = project.getTeamMembers();
        List<ProfileEntity> projectOwners = project.getProjectOwners();
       
        newAnnouncementEntity.getNotifiedUsers().addAll(projectTeammembers);
        newAnnouncementEntity.getNotifiedUsers().addAll(projectOwners);
        
        //add announcements to project teammember
        for(ProfileEntity p: projectTeammembers ){
            p.getAnnouncements().add(newAnnouncementEntity);
        }
        
        //add announcements to project owners
        for(ProfileEntity p: projectOwners ){
            p.getAnnouncements().add(newAnnouncementEntity);
        }
        
        //Incomplete: notify project teammembers and project owners       
        newAnnouncementEntity = announcementEntityRepository.saveAndFlush(newAnnouncementEntity);
        
        SendNotificationsToUsersVO sendNotificationsToUsersVO = new SendNotificationsToUsersVO();
        sendNotificationsToUsersVO.setTitle(newAnnouncementEntity.getTitle());
        sendNotificationsToUsersVO.setBody(newAnnouncementEntity.getContent());
        sendNotificationsToUsersVO.setType(newAnnouncementEntity.getType().toString());
        sendNotificationsToUsersVO.setImage("");
        List<String> uuids = new ArrayList<>();
        
        for(ProfileEntity p: newAnnouncementEntity.getNotifiedUsers()){
           uuids.add(p.getUuid().toString());
        }     
        sendNotificationsToUsersVO.setUuids(uuids);
        firebaseService.sendNotificationsToUsers(sendNotificationsToUsersVO);
        return newAnnouncementEntity;
    }
    
    
    @Override
    public List<AnnouncementEntity> viewProjectInternalAnnouncements(Long projectId){
        return announcementEntityRepository.searchProjectAnnouncementProjectIdAndType(projectId, AnnouncementTypeEnum.PROJECT_INTERNAL_ANNOUNCEMENT);         
    }
    
    
    // only project owners can delete project internal announcement
    @Override
    public void deleteProjectInternalAnnouncement(Long announcementId, Long userId) throws DeleteAnnouncementException{  
       if(!announcementEntityRepository.findById(announcementId).isPresent()){
           throw new DeleteAnnouncementException("Announcement not found");
       }
        
        AnnouncementEntity announcement = announcementEntityRepository.findById(announcementId).get();
        
        
        ProjectEntity project = projectEntityRepository.findById(announcement.getProjectId()).get();
        ProfileEntity user = profileEntityRepository.findById(userId).get();    
        
        if(!project.getProjectOwners().contains(user)){
            throw new DeleteAnnouncementException("Only project owners can delete project internal announcement");
        }
        
        List<ProfileEntity> projectTeammembers = project.getTeamMembers();
        List<ProfileEntity> projectOwners = project.getProjectOwners();
        
        //remove announcements of project teammember
        for(ProfileEntity p: projectTeammembers ){
            p.getAnnouncements().remove(announcement);
        }
        
        //remove announcements of project owners
        for(ProfileEntity p: projectOwners ){
            p.getAnnouncements().remove(announcement);
        }
        
        announcement.setNotifiedUsers(new ArrayList<>());      
        announcementEntityRepository.delete(announcement);

    }
    
    
    @Override
    public void deleteAnAnnouncementForUser(Long announcementId, Long userId){
        System.err.println("deleteAnAnnouncementForUser:"+announcementId );
        AnnouncementEntity announcement = announcementEntityRepository.findById(announcementId).get();
        ProfileEntity user = profileEntityRepository.findById(userId).get();         
        user.getAnnouncements().remove(announcement);
        profileEntityRepository.saveAndFlush(user);    
    }

    //retrieve Announcementx by userId
    @Override
    public List<AnnouncementEntity> getAnnouncementsByUserId(Long userId) {
        ProfileEntity user = profileEntityRepository.findById(userId).get();
        return user.getAnnouncements();
    }

    // get a Announcement by id 
    @Override
    public AnnouncementEntity getAnnouncementById(Long announcementId) {
        return announcementEntityRepository.findById(announcementId).get();
    }


    @Override
    public void viewAnnouncement(Long announcementId, Long viewerId) {
        ProfileEntity user = profileEntityRepository.findById(viewerId).get();
        AnnouncementEntity announcement = announcementEntityRepository.findById(announcementId).get();
        if (!announcement.getViewedUserIds().contains(viewerId)) {
            announcement.getViewedUserIds().add(viewerId);
            announcementEntityRepository.flush();
        }

    }
    @Override
    public void createNormalNotification(AnnouncementEntity announcementEntity){
        // create notification
        SendNotificationsToUsersVO sendNotificationsToUsersVO = new SendNotificationsToUsersVO();
        sendNotificationsToUsersVO.setTitle(announcementEntity.getTitle());
        sendNotificationsToUsersVO.setBody(announcementEntity.getContent());
        sendNotificationsToUsersVO.setType(announcementEntity.getType().toString());
        sendNotificationsToUsersVO.setImage("");
        List<String> uuids = new ArrayList<>();
        
        for(ProfileEntity p: announcementEntity.getNotifiedUsers()){
           uuids.add(p.getUuid().toString());
        }     
        sendNotificationsToUsersVO.setUuids(uuids);
        firebaseService.sendNotificationsToUsers(sendNotificationsToUsersVO);
    }
    
    
    @Override
    public void readAllAnnouncements(Long userId){
        ProfileEntity user = profileEntityRepository.findById(userId).get();
        for(AnnouncementEntity a : user.getAnnouncements()){
            if(!a.getViewedUserIds().contains(userId)){
                a.getViewedUserIds().add(userId);
                announcementEntityRepository.saveAndFlush(a);
            }
            
        }    
        
    }
    
    @Override
    public void clearAllAnnouncemnents(Long userId){
        ProfileEntity user = profileEntityRepository.findById(userId).get();
        user.setAnnouncements(new ArrayList<>());
        profileEntityRepository.saveAndFlush(user);   
    }
    
    
    @Override
    public ProfileEntity updateAnnouncementSettinge(AnnouncementSettingVO vo)throws UserNotFoundException{
        ProfileEntity user  = profileEntityRepository.findById(vo.getUserId()).orElseThrow(() -> new UserNotFoundException(vo.getUserId()));
        for(AnnouncementTypeEnum k :vo.getNewSetting().keySet()){
           user.getAnnouncementsSetting().replace(k, vo.getNewSetting().get(k));
        }
        return profileEntityRepository.saveAndFlush(user);
    }
    
}
