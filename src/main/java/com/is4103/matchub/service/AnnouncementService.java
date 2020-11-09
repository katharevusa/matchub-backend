/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.CreateAnnouncementException;
import com.is4103.matchub.exception.DeleteAnnouncementException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.vo.AnnouncementSettingVO;
import com.is4103.matchub.vo.AnnouncementVO;
import java.util.List;

/**
 *
 * @author longluqian
 */
public interface AnnouncementService {

    public void deleteProjectInternalAnnouncement(Long announcementId, Long userId) throws DeleteAnnouncementException;

    public AnnouncementEntity getAnnouncementById(Long AnnouncementId);

    public List<AnnouncementEntity> getAnnouncementsByUserId(Long userId);

    public void viewAnnouncement(Long AnnouncementId, Long viewerId);

    public void deleteAnAnnouncementForUser(Long announcementId, Long userId);

    public List<AnnouncementEntity> viewProjectInternalAnnouncements(Long projectId);

    public AnnouncementEntity createProjectInternalAnnouncement(AnnouncementVO newAnnouncementVO) throws CreateAnnouncementException;

    public void deleteProjectPublicAnnouncement(Long announcementId, Long userId) throws DeleteAnnouncementException;

    public List<AnnouncementEntity> viewProjectPublicAnnouncements(Long projectId);

    public AnnouncementEntity createProjectPublicAnnouncement(AnnouncementVO newAnnouncementVO) throws CreateAnnouncementException;

    public void createNormalNotification(AnnouncementEntity announcementEntity);

    public void readAllAnnouncements(Long userId);
    
    public void clearAllAnnouncemnents(Long userId);
    
    public ProfileEntity updateAnnouncementSettinge(AnnouncementSettingVO vo)throws UserNotFoundException;
}
