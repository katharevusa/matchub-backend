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
import com.is4103.matchub.exception.UnableToRemoveKAHFromOrganisationException;
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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
    @Transactional
    public void testAddMemberToOrganisationOrganisationNotFoundException() {
        organisationService.addMemberToOrganisation(20L, 4L);
    }

    @Test(expected = UserNotFoundException.class)
    @Transactional
    public void testAddMemberToOrganisationUserNotFoundException() {
        organisationService.addMemberToOrganisation(7L, 20L);
    }

    @Test(expected = UnableToAddMemberToOrganisationException.class)
    @Transactional
    public void testAddMemberToOrganisationUnableToAddMemberToOrganisationException() {
        organisationService.addMemberToOrganisation(7L, 6L);
    }

    @Test
    @Transactional
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
    @Transactional
    public void testRemoveMemberFromOrganisationOrganisationNotFoundException() {
        organisationService.removeMemberFromOrganisation(20L, 4L);
    }

    @Test(expected = UserNotFoundException.class)
    @Transactional
    public void testRemoveMemberFromOrganisationUserNotFoundException() {
        organisationService.removeMemberFromOrganisation(7L, 20L);
    }

    @Test(expected = UnableToRemoveMemberFromOrganisationException.class)
    @Transactional
    public void testRemoveMemberFromOrganisationUnableToRemoveMemberFromOrganisationException() {
        organisationService.removeMemberFromOrganisation(7L, 9L);
    }

    @Test
    @Transactional
    public void testViewOrganisationMembers() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProfileEntity> results = organisationService.viewOrganisationMembers(8L, pageable);

        Assert.assertTrue(results.getContent().size() == 2);
    }

    @Test(expected = OrganisationNotFoundException.class)
    @Transactional
    public void testViewOrganisationMembersOrganisationNotFoundException() {
        Pageable pageable = PageRequest.of(0, 20);
        organisationService.viewOrganisationMembers(20L, pageable);
    }

    @Test
    @Transactional
    public void testAddKahToOrganisation() {
        organisationService.addMemberToOrganisation(7L, 11L);
        organisationService.addKahToOrganisation(7L, 11L);

        try {
            OrganisationEntity organisation = organisationEntityRepository.findById(7L).get();
            Assert.assertTrue(organisation.getKahs().contains(11L));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test(expected = OrganisationNotFoundException.class)
    @Transactional
    public void testAddKahToOrganisationOrganisationNotFoundException() {
        organisationService.addKahToOrganisation(20L, 4L);
    }

    @Test(expected = UserNotFoundException.class)
    @Transactional
    public void testAddKahToOrganisationUserNotFoundException() {
        organisationService.addKahToOrganisation(7L, 20L);
    }

    @Test(expected = UnableToAddKAHToOrganisationException.class)
    @Transactional
    public void testAddKahToOrganisationUnableToAddKAHToOrganisationException() {
        organisationService.addKahToOrganisation(7L, 6L);
    }

    @Test
    @Transactional
    public void testRemoveKahFromOrganisation() {

        organisationService.removeKahFromOrganisation(7L, 6L);

        try {
            OrganisationEntity organisation = organisationEntityRepository.findById(7L).get();
            Assert.assertTrue(!organisation.getKahs().contains(6L));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test(expected = OrganisationNotFoundException.class)
    @Transactional
    public void testRemoveKahFromOrganisationOrganisationNotFoundException() {
        organisationService.removeKahFromOrganisation(20L, 4L);
    }
    
    @Test(expected = UserNotFoundException.class)
    @Transactional
    public void testRemoveKahFromOrganisationUserNotFoundException() {
        organisationService.removeKahFromOrganisation(7L, 20L);
    }
    
    @Test(expected = UnableToRemoveKAHFromOrganisationException.class)
    @Transactional
    public void testRemoveKahFromOrganisationUnableToRemoveKAHFromOrganisationException() {
        organisationService.removeKahFromOrganisation(7L, 12L);
    }

    
}
