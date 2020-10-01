/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.NotificationEntity;
import com.is4103.matchub.vo.NotificationVO;
import java.util.List;

/**
 *
 * @author longluqian
 */
public interface NotificationService {

    public void deleteNotificationById(Long notificationId);

    public NotificationEntity getNotificationById(Long notificationId);

    public List<NotificationEntity> getNotificationsByUserId(Long userId);

    public NotificationEntity createNotification(NotificationVO newNotificationVO);

    public void viewNotification(Long notificationId, Long viewerId);
}
