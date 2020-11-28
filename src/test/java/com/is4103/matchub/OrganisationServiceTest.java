/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.OrganisationNotFoundException;
import com.is4103.matchub.exception.UnableToAddKAHToOrganisationException;
import com.is4103.matchub.exception.UnableToAddMemberToOrganisationException;
import com.is4103.matchub.exception.UnableToRemoveMemberFromOrganisationException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.OrganisationEntityRepository;
import com.is4103.matchub.service.OrganisationService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author ngjin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class OrganisationServiceTest {

    public OrganisationServiceTest() {
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

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationEntityRepository organisationEntityRepository;

    @Test
    @Order(4)
    public void testAddMemberToOrganisation() {

        organisationService.addMemberToOrganisation(7L, 4L);

        try {
            OrganisationEntity organisation = organisationEntityRepository.findById(7L).get();
            Assert.assertTrue(organisation.getEmployees().contains(4L));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test(expected = OrganisationNotFoundException.class)
    @Order(1)
    public void testAddMemberToOrganisationOrganisationNotFoundException() {
        organisationService.addMemberToOrganisation(20L, 4L);
    }

    @Test(expected = UserNotFoundException.class)
    @Order(2)
    public void testAddMemberToOrganisationUserNotFoundException() {
        organisationService.addMemberToOrganisation(7L, 20L);
    }

    @Test(expected = UnableToAddMemberToOrganisationException.class)
    @Order(3)
    public void testAddMemberToOrganisationUnableToAddMemberToOrganisationException() {
        organisationService.addMemberToOrganisation(7L, 6L);
    }

    @Test
    @Order(8)
    public void testRemoveMemberFromOrganisation() {

        organisationService.removeMemberFromOrganisation(7L, 12L);

        try {
            OrganisationEntity organisation = organisationEntityRepository.findById(7L).get();
            Assert.assertTrue(!organisation.getEmployees().contains(9L));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test(expected = OrganisationNotFoundException.class)
    @Order(5)
    public void testRemoveMemberFromOrganisationOrganisationNotFoundException() {
        organisationService.removeMemberFromOrganisation(20L, 4L);
    }

    @Test(expected = UserNotFoundException.class)
    @Order(6)
    public void testRemoveMemberFromOrganisationUserNotFoundException() {
        organisationService.removeMemberFromOrganisation(7L, 20L);
    }

    @Test(expected = UnableToRemoveMemberFromOrganisationException.class)
    @Order(7)
    public void testRemoveMemberFromOrganisationUnableToRemoveMemberFromOrganisationException() {
        organisationService.removeMemberFromOrganisation(7L, 9L);
    }

    @Test
    @Order(10)
    public void testViewOrganisationMembers() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProfileEntity> results = organisationService.viewOrganisationMembers(8L, pageable);

        Assert.assertTrue(results.getContent().size() == 2);
    }

    @Test(expected = OrganisationNotFoundException.class)
    @Order(9)
    public void testViewOrganisationMembersOrganisationNotFoundException() {
        Pageable pageable = PageRequest.of(0, 20);
        organisationService.viewOrganisationMembers(20L, pageable);
    }

    @Test
    @Order(14)
    public void testAddKahToOrganisation() {
        organisationService.addMemberToOrganisation(7L, 11L);
        organisationService.addKahToOrganisation(7L, 11L);
        
        try {
            OrganisationEntity organisation = organisationEntityRepository.findById(7L).get();
            Assert.assertTrue(!organisation.getKahs().contains(12L));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test(expected = OrganisationNotFoundException.class)
    @Order(11)
    public void testAddKahToOrganisationOrganisationNotFoundException() {
        organisationService.addKahToOrganisation(20L, 4L);
    }

    @Test(expected = UserNotFoundException.class)
    @Order(12)
    public void testAddKahToOrganisationUserNotFoundException() {
        organisationService.addKahToOrganisation(7L, 20L);
    }

    @Test(expected = UnableToAddKAHToOrganisationException.class)
    @Order(13)
    public void testAddKahToOrganisationUnableToAddKAHToOrganisationException() {
        organisationService.addKahToOrganisation(7L, 6L);
    }
    
    @Test
    public void testRemoveKahFromOrganisation() {
        organisationService.removeKahFromOrganisation(7L, 6L);
    }

}
