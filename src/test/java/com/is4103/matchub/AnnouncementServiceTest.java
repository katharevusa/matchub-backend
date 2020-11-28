/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.exception.CreateAnnouncementException;
import com.is4103.matchub.exception.DeleteAnnouncementException;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.service.AnnouncementService;
import com.is4103.matchub.service.FirebaseService;
import com.is4103.matchub.service.ProjectService;
import com.is4103.matchub.vo.AnnouncementVO;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author longluqian
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class AnnouncementServiceTest {

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

    @Autowired
    AnnouncementService announcementService;

    @Autowired
    ProjectService projectService;

    public AnnouncementServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateProjectPublicAnnouncement() throws CreateAnnouncementException {
        AnnouncementVO announcementVO = new AnnouncementVO();
        announcementVO.setContent("new announcement");
        announcementVO.setCreatorId(5L);
        announcementVO.setProjectId(1L);
        announcementVO.setTitle("title");
        AnnouncementEntity announcementEntity = announcementService.createProjectPublicAnnouncement(announcementVO);
        announcementEntity = announcementEntityRepository.findById(announcementEntity.getAnnouncementId()).get();
        Assert.assertTrue(announcementEntity.getContent().equals("new announcement"));
    }

    @Test(expected = CreateAnnouncementException.class)
    public void testCreateProjectPublicAnnouncementcreateProjectPublicAnnouncement() throws CreateAnnouncementException {
        AnnouncementVO announcementVO = new AnnouncementVO();
        announcementVO.setContent("new announcement");
        announcementVO.setCreatorId(8L);
        announcementVO.setProjectId(1L);
        announcementVO.setTitle("title");
        AnnouncementEntity announcementEntity = announcementService.createProjectPublicAnnouncement(announcementVO);
    }

    @Test
    public void testViewProjectPublicAnnouncements() {
        ProjectEntity project = projectEntityRepository.findById(1L).get();
        Assert.assertTrue(announcementService.viewProjectPublicAnnouncements(1L) != null);
    }

    @Test
    public void testDeleteProjectPublicAnnouncement() throws DeleteAnnouncementException {
        announcementService.deleteProjectPublicAnnouncement(1L, 5L);
        Assert.assertTrue(!announcementEntityRepository.findById(1L).isPresent());

    }

    @Test(expected = DeleteAnnouncementException.class)
    public void testDeleteProjectPublicAnnouncementDeleteAnnouncementException() throws DeleteAnnouncementException {

        announcementService.deleteProjectPublicAnnouncement(1L, 7L);

    }

    @Test
    public void testDeleteAnAnnouncementForUser() {
        ProfileEntity profile = profileEntityRepository.findById(9L).get();
        int oldAnnouncementSize = profile.getAnnouncements().size();
        announcementService.deleteAnAnnouncementForUser(2L, 9L);
        profile = profileEntityRepository.findById(9L).get();
        Assert.assertTrue(profile.getAnnouncements().size() == oldAnnouncementSize - 1);
    }

    @Test
    public void testGetAnnouncementsByUserId() {
        ProfileEntity profile = profileEntityRepository.findById(5L).get();
        Assert.assertTrue(announcementService.getAnnouncementsByUserId(5L).size() == profile.getAnnouncements().size());
    }

    @Test
    public void testViewAnnouncement() {
        AnnouncementEntity announcementEntity = announcementEntityRepository.findById(2L).get();
        int oldViewedUserSize = announcementEntity.getViewedUserIds().size();
        announcementService.viewAnnouncement(2L, 9L);
        announcementEntity = announcementEntityRepository.findById(2L).get();
        Assert.assertTrue(oldViewedUserSize + 1 == announcementEntity.getViewedUserIds().size());
    }

    @Test
    public void testReadAllAnnouncements() {
        AnnouncementEntity announcementEntity1 = announcementEntityRepository.findById(2L).get();
        AnnouncementEntity announcementEntity2 = announcementEntityRepository.findById(3L).get();
        int oldAnnouncementEntity1ViewedUsersSize = announcementEntity1.getViewedUserIds().size();
        int oldAnnouncementEntity2ViewedUsersSize = announcementEntity2.getViewedUserIds().size();
        announcementService.readAllAnnouncements(9L);
        announcementEntity1 = announcementEntityRepository.findById(2L).get();
        announcementEntity2 = announcementEntityRepository.findById(3L).get();
        Assert.assertTrue((oldAnnouncementEntity1ViewedUsersSize + 1 == announcementEntity1.getViewedUserIds().size()) && (oldAnnouncementEntity2ViewedUsersSize + 1 == announcementEntity2.getViewedUserIds().size()) );
    }
    
    @Test
    public void testClearAllAnnouncemnents(){
        announcementService.clearAllAnnouncemnents(9L);
        ProfileEntity profile = profileEntityRepository.findById(9L).get();
        Assert.assertTrue(profile.getAnnouncements().size()==0);
    }
    
    @Test
    public void testGetFollowingProjectAnnouncements(){
        Assert.assertTrue(announcementService.getFollowingProjectAnnouncements(5L).size()!=0);
    }
    
    @Test
    public void testGetOwnedProjectAnnouncements(){
        Assert.assertTrue(announcementService.getOwnedProjectAnnouncements(5L).size()!=0);
    }
    
   

}
