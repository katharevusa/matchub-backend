/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.NotificationEntity;
import com.is4103.matchub.service.NotificationService;
import com.is4103.matchub.vo.NotificationVO;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author longluqian
 */

@RestController
@RequestMapping("/authenticated")
public class NotificationController {
    @Autowired
    NotificationService notificationService;
    
    @RequestMapping(method = RequestMethod.POST, value = "/createNewNotification")
    NotificationEntity createNewNotification(@Valid @RequestBody NotificationVO vo){
        return notificationService.createNotification(vo);
        
    }
      
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteNotificationById")
    public void deleteNotificationById(Long notificationId){
        notificationService.deleteNotificationById(notificationId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getNotificationById")
    public NotificationEntity getNotificationById(Long notificationId){
       return notificationService.getNotificationById(notificationId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getNotificationsByUserId")
    public List<NotificationEntity> getNotificationsByUserId(Long userId){
        return notificationService.getNotificationsByUserId(userId);
    }
    
    //notification viewed changes to true
    @RequestMapping(method = RequestMethod.PUT, value = "/viewNotification")
    public void viewNotification(Long notificationId, Long viewerId){
         notificationService.viewNotification(notificationId, viewerId);
    }
    
    
}
