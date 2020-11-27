/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.exception.OrganisationNotFoundException;
import com.is4103.matchub.exception.UnableToAddMemberToOrganisationException;
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

/**
 *
 * @author ngjin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
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

    @Test(expected = OrganisationNotFoundException.class)
    public void addMemberToOrganisationTest1() {
        organisationService.addMemberToOrganisation(20L, 4L);
    }

    @Test(expected = UserNotFoundException.class)
    public void addMemberToOrganisationTest2() {
        organisationService.addMemberToOrganisation(7L, 20L);
    }

    @Test(expected = UnableToAddMemberToOrganisationException.class)
    public void addMemberToOrganisationTest3() {
        organisationService.addMemberToOrganisation(7L, 6L);
    }

    @Test
    public void addMemberToOrganisationTest4() {
        
        organisationService.addMemberToOrganisation(7L, 4L);

        try {
            OrganisationEntity organisation = organisationEntityRepository.findById(7L).get();
            Assert.assertTrue(organisation.getEmployees().contains(4L));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    
    public void removeMemberFromOrganisation1() {
        
    }

}
