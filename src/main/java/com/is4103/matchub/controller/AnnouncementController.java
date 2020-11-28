/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.CreateAnnouncementException;
import com.is4103.matchub.exception.DeleteAnnouncementException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.vo.AnnouncementVO;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.is4103.matchub.service.AnnouncementService;
import com.is4103.matchub.vo.AnnouncementSettingVO;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author longluqian
 */

@RestController
@RequestMapping("/authenticated")
public class AnnouncementController {
    @Autowired
    AnnouncementService announcementService;
    
    
    //create project announcementService for a project 
    @RequestMapping(method = RequestMethod.POST, value = "/createProjectPublicAnnouncement")
    public AnnouncementEntity createProjectPublicAnnouncement(@Valid @RequestBody AnnouncementVO newAnnouncementVO) throws CreateAnnouncementException{
        
        return announcementService.createProjectPublicAnnouncement(newAnnouncementVO);
         
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/viewProjectPublicAnnouncements")
    public List<AnnouncementEntity> viewProjectPublicAnnouncements(@RequestParam(value = "projectId", defaultValue = "")Long projectId){
        return announcementService.viewProjectPublicAnnouncements(projectId);
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteProjectPublicAnnouncement")
    public void deleteProjectPublicAnnouncement(@RequestParam(value = "announcementId",required = true)Long announcementId,@RequestParam(value = "userId",required = true) Long userId) throws DeleteAnnouncementException{
        announcementService.deleteProjectPublicAnnouncement(announcementId, userId);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/createProjectInternalAnnouncement")
    public AnnouncementEntity createProjectInternalAnnouncement(@Valid @RequestBody AnnouncementVO newAnnouncementVO) throws CreateAnnouncementException{
        return announcementService.createProjectInternalAnnouncement(newAnnouncementVO);
        
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/viewProjectInternalAnnouncements")
    public List<AnnouncementEntity> viewProjectInternalAnnouncements(@RequestParam(value = "projectId",required = true)Long projectId){
        return announcementService.viewProjectInternalAnnouncements(projectId);
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteProjectInternalAnnouncement")
    public void deleteProjectInternalAnnouncement(@RequestParam(value = "announcementId",required = true)Long announcementId,@RequestParam(value = "userId",required = true) Long userId) throws DeleteAnnouncementException{
        announcementService.deleteProjectInternalAnnouncement(announcementId,userId);
    }
    
    // delete an announcementService of an user(only remove from user's side)
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteAnAnnouncementForUser")
    public void deleteAnAnnouncementForUser(@RequestParam(value = "announcementId",required = true)Long announcementId, @RequestParam(value = "userId",required = true)Long userId){
        announcementService.deleteAnAnnouncementForUser(announcementId,userId);
    }

       
    // get a particular announcementService
    @RequestMapping(method = RequestMethod.GET, value = "/getAnnouncementById")
    public AnnouncementEntity getAnnouncementById(@RequestParam(value = "announcementId",required = true)Long announcementId){
       return announcementService.getAnnouncementById(announcementId);
    }

    // get user's all announcements
    @RequestMapping(method = RequestMethod.GET, value = "/getAnnouncementsByUserId")
    public List<AnnouncementEntity> getAnnouncementsByUserId(@RequestParam(value = "userId",required = true)Long userId){
        return announcementService.getAnnouncementsByUserId(userId);
    }
    
    //read-receipt to backend
    @RequestMapping(method = RequestMethod.PUT, value = "/viewAnnouncement")
    public void viewAnnouncement(@RequestParam(value = "announcementId",required = true)Long announcementId,@RequestParam(value = "viewerId",required = true) Long viewerId){
         announcementService.viewAnnouncement(announcementId, viewerId);
    }
    
    //clear all announcements
    @RequestMapping(method = RequestMethod.DELETE, value = "/clearAllAnnouncementsForUser")
    public void clearAllAnnouncementsForUser( @RequestParam(value = "userId",required = true) Long userId){
        announcementService.clearAllAnnouncemnents(userId);
    }
    // read all announcements
    @RequestMapping(method = RequestMethod.PUT, value = "/viewAllAnnouncements")
    public void viewAllAnnouncements(@RequestParam(value = "userId",required = true) Long userId){
         announcementService.readAllAnnouncements(userId);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "/updateAnnouncementSettinge")
     public ProfileEntity updateAnnouncementSettinge(@Valid @RequestBody AnnouncementSettingVO vo)throws UserNotFoundException{
      return  announcementService.updateAnnouncementSettinge(vo);
    }
     
     @RequestMapping(method = RequestMethod.GET, value = "/getFollowingProjectAnnouncements")
    public List<AnnouncementEntity> getFollowingProjectAnnouncements(@RequestParam(value = "userId", required = true)Long userId){
        return announcementService.getFollowingProjectAnnouncements(userId);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getOwnedProjectAnnouncements")
    public List<AnnouncementEntity> getOwnedProjectAnnouncements(@RequestParam(value = "userId", required = true)Long userId){
        return announcementService.getOwnedProjectAnnouncements(userId);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getJoinedProjectAnnouncements")
    public List<AnnouncementEntity> getJoinedProjectAnnouncements(@RequestParam(value = "userId", required = true)Long userId){
        return announcementService.getJoinedProjectAnnouncements(userId);
    }
    
    
}
