package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class InitServiceImpl implements InitService {

    @Autowired
    AccountEntityRepository accountEntityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    ProjectEntityRepository projectEntityRepository;
    
    @Autowired 
    ProjectService projectService;

    @Transactional
    public void init() {
        initUsers();
    }

    private void initUsers() {
        if (accountEntityRepository.count() > 0) {
            return;
        }
        Arrays.asList("admin,user1,user2".split(","))
                .forEach(a -> {
                    AccountEntity account;
                    if (a.equalsIgnoreCase("admin")) {
                        account = accountEntityRepository.save(new AccountEntity(a + "@gmail.com", passwordEncoder.encode("password")));
                        account.getRoles().add(ProfileEntity.ROLE_SUPERUSER);
                        account.getRoles().add(ProfileEntity.ROLE_SYSADMIN);
                        account.getRoles().add(ProfileEntity.ROLE_USER);
                    } else if (a.equalsIgnoreCase("user1")) {
                        account = accountEntityRepository.save(new IndividualEntity(a + "@gmail.com", passwordEncoder.encode("password"), "Phil", "Lim", GenderEnum.MALE));
                        account.getRoles().add(ProfileEntity.ROLE_USER);
                        ProjectEntity projectEntity = new ProjectEntity("Project 1 title","Project 1 description", "Singapore", LocalDateTime.now(),LocalDateTime.parse("2018-05-05T11:50:55"));
                        projectService.createProject(projectEntity, account.getAccountId());

                        
                    } else {
                        account = accountEntityRepository.save(new OrganisationEntity(a + "@gmail.com", passwordEncoder.encode("password"), "NUS", "description", "address"));
                        account.getRoles().add(ProfileEntity.ROLE_USER);
                        ProjectEntity projectEntity = new ProjectEntity("Project 2 title","Project 2 description", "China", LocalDateTime.now(),LocalDateTime.parse("2019-06-05T11:50:55"));
                        projectService.createProject(projectEntity, account.getAccountId());

                    }
                    accountEntityRepository.save(account);
                });
    }
}
