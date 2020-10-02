/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.NotificationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.repository.NotificationEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.vo.NotificationVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class NotificationImpl implements NotificationService{
    
    @Autowired
    ProfileEntityRepository profileEntityRepository;
    
    @Autowired
    NotificationEntityRepository notificationEntityRepository;
    
    // create Notification
    @Override
    public NotificationEntity createNotification(NotificationVO newNotificationVO){
        NotificationEntity newNotificationEntity = new NotificationEntity();
        newNotificationVO.createNotification(newNotificationEntity);
        ProfileEntity user =  profileEntityRepository.findById(newNotificationEntity.getNotifiedUserId()).get();
        newNotificationEntity = notificationEntityRepository.save(newNotificationEntity);
        user.getNotifications().add(newNotificationEntity);
        
        return newNotificationEntity;     
    }
            
    //retrieve notification by userId
    @Override
    public List<NotificationEntity> getNotificationsByUserId(Long userId){
       ProfileEntity user = profileEntityRepository.findById(userId).get();
       return user.getNotifications();
    }
    // get a notification by id 
    @Override
    public NotificationEntity getNotificationById(Long notificationId){
        return notificationEntityRepository.findById(notificationId).get();
    }
    
    @Override
    public void deleteNotificationById(Long notificationId) {
        NotificationEntity notificationEntity = notificationEntityRepository.findById(notificationId).get();
        ProfileEntity user = profileEntityRepository.findById(notificationEntity.getNotifiedUserId()).get();
        user.getNotifications().remove(notificationEntity);
        notificationEntityRepository.delete(notificationEntity);
        
    }
  
    @Override
    public void viewNotification(Long notificationId, Long viewerId){
        ProfileEntity user = profileEntityRepository.findById(viewerId).get();
        NotificationEntity notification = notificationEntityRepository.findById(notificationId).get();
        if(notification.getNotifiedUserId().equals(viewerId)){
            notification.setViewed(Boolean.TRUE);  
            notificationEntityRepository.flush();
        }
        
    }
    
    
}
