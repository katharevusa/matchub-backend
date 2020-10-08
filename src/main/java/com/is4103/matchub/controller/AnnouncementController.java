/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.exception.CreateAnnouncementException;
import com.is4103.matchub.exception.DeleteAnnouncementException;
import com.is4103.matchub.vo.AnnouncementVO;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.is4103.matchub.service.AnnouncementService;

/**
 *
 * @author longluqian
 */

@RestController
@RequestMapping("/authenticated")
public class AnnouncementController {
    @Autowired
    AnnouncementService announcement;
    
    
    //create project announcement for a project 
    @RequestMapping(method = RequestMethod.POST, value = "/createProjectPublicAnnouncement")
    public AnnouncementEntity createProjectPublicAnnouncement(AnnouncementVO newAnnouncementVO) throws CreateAnnouncementException{
        return announcement.createProjectPublicAnnouncement(newAnnouncementVO);
        
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/viewProjectPublicAnnouncements")
    public List<AnnouncementEntity> viewProjectPublicAnnouncements(Long projectId){
        return announcement.viewProjectPublicAnnouncements(projectId);
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/viewProjectPublicAnnouncements")
    public void deleteProjectPublicAnnouncement(Long announcementId,Long userId) throws DeleteAnnouncementException{
        announcement.deleteProjectPublicAnnouncement(announcementId, userId);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/createProjectInternalAnnouncement")
    public AnnouncementEntity createProjectInternalAnnouncement(AnnouncementVO newAnnouncementVO) throws CreateAnnouncementException{
        return announcement.createProjectInternalAnnouncement(newAnnouncementVO);
        
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/viewProjectInternalAnnouncements")
    public List<AnnouncementEntity> viewProjectInternalAnnouncements(Long projectId){
        return announcement.viewProjectInternalAnnouncements(projectId);
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteProjectInternalAnnouncement")
    public void deleteProjectInternalAnnouncement(Long announcementId, Long userId) throws DeleteAnnouncementException{
        announcement.deleteProjectInternalAnnouncement(announcementId,userId);
    }
    
    // delete an announcement of an user(only remove from user's side)
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteAnAnnouncementForUser")
    public void deleteAnAnnouncementForUser(Long announcementId, Long userId){
        announcement.deleteAnAnnouncementForUser(announcementId,userId);
    }

       
    // get a particular announcement
    @RequestMapping(method = RequestMethod.GET, value = "/getAnnouncementById")
    public AnnouncementEntity getAnnouncementById(Long announcementId){
       return announcement.getAnnouncementById(announcementId);
    }

    // get user's all announcements
    @RequestMapping(method = RequestMethod.GET, value = "/getAnnouncementsByUserId")
    public List<AnnouncementEntity> getAnnouncementsByUserId(Long userId){
        return announcement.getAnnouncementsByUserId(userId);
    }
    
    //read-receipt to backend
    @RequestMapping(method = RequestMethod.PUT, value = "/viewAnnouncement")
    public void viewAnnouncement(Long announcementId, Long viewerId){
         announcement.viewAnnouncement(announcementId, viewerId);
    }
    
    
}