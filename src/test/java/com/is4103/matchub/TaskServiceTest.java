package com.is4103.matchub;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.IndividualEntityRepository;
import com.is4103.matchub.service.UserServiceImpl;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.IndividualSetupVO;
import com.is4103.matchub.vo.UserVO;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author markt
 */
//@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)

@SpringBootTest
public class TaskServiceTest {

    public TaskServiceTest() {
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
    private UserServiceImpl userServiceImpl;

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Autowired
    private IndividualEntityRepository individualEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    UserVO testUser;

    @Test
    public void testCreateTask() {
        IndividualCreateVO vo = new IndividualCreateVO();

        vo.setEmail("testmemail@gmail.com");
        vo.setFirstName("tester");
        vo.setLastName("tester");
        vo.setPassword("password");
        vo.setRoles(new String[]{"USER"});

        try {
            testUser = userServiceImpl.createIndividual(vo);
            Assert.assertTrue(testUser != null);
        } catch (IOException | MessagingException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testSetupProfile() {

        IndividualSetupVO setUpVO = new IndividualSetupVO();
        setUpVO.setCity("Singapore");
        setUpVO.setCountry("Singapore");
        setUpVO.setPhoneNumber("91234567");
        setUpVO.setGenderEnum("MALE");
        setUpVO.setProfileDescription("Individual Passionate about the environment");
        try {
            UserVO updatedUser = userServiceImpl.setupIndividualProfile(testUser.getUuid(), setUpVO);
            IndividualEntity ie = individualEntityRepository.findById(updatedUser.getAccountId()).get();
            Assert.assertEquals(ie.getPhoneNumber(), "91234567");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
