/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.service.AnnouncementService;
import com.is4103.matchub.service.FirebaseService;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.AfterClass;
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
    public void testCreateProjectPublicAnnouncementSuccessful(){
        AnnouncementEn
    }

    
}
