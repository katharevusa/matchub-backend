package com.is4103.matchub;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.IndividualEntityRepository;
import com.is4103.matchub.repository.OrganisationEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ReviewEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import com.is4103.matchub.repository.TaskEntityRepository;
import com.is4103.matchub.service.AnnouncementService;
import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.EmailService;
import com.is4103.matchub.service.UserServiceImpl;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.UserVO;
import java.io.IOException;
import java.util.Optional;
import javax.mail.MessagingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ExtendWith(MockitoExtension.class)
//@RunWith(JUnitPlatform.class)
//@RunWith(SpringRunner.class)
//@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Spy
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private AccountEntityRepository accountEntityRepository;
//
    @Mock
    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private EmailService emailService;
//
//    @Mock
//    private AttachmentService attachmentService;
//
//    @Mock
//    private SDGEntityRepository sdgEntityRepository;
//
//    @Mock
//    private TaskEntityRepository taskEntityRepository;
//
//    @Mock
//    private ResourceEntityRepository resourceEntityRepository;
//
//    @Mock
//    private ReviewEntityRepository reviewEntityRepository;
//
//    @Mock
//    private ProfileEntityRepository profileEntityRepository;

    @Mock
    private IndividualEntityRepository individualEntityRepository;
//
//    @Mock
//    private OrganisationEntityRepository organisationEntityRepository;
//
//    @Mock
//    private AnnouncementService announcementService;
//
//    @Mock
//    private AnnouncementEntityRepository announcementEntityRepository;
//    

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateIndividual() {
        IndividualCreateVO vo = new IndividualCreateVO();

        vo.setEmail("testmemail@gmail.com");
        vo.setFirstName("tester");
        vo.setLastName("tester");
        vo.setPassword("password");
        vo.setRoles(new String[]{"USER"});

        try {
            Mockito.when(accountEntityRepository.findByEmail("testmemail@gmail.com")).thenReturn(Optional.empty());
            Mockito.when(accountEntityRepository.save(any(AccountEntity.class))).thenReturn(new IndividualEntity());
            Mockito.when(passwordEncoder.encode(any(String.class))).thenReturn("password");
            UserVO newUser = userServiceImpl.createIndividual(vo);
            Mockito.verify(accountEntityRepository, times(1)).save(Mockito.any(AccountEntity.class));
//            Assert.assertTrue(newUser != null);
        } catch (IOException | MessagingException ex) {
            ex.printStackTrace();
        }
    }
}
