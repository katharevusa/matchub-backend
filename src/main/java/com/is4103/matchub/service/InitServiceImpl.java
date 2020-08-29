package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.is4103.matchub.repository.AccountEntityRepository;

@Service
public class InitServiceImpl implements InitService {

    @Autowired
    AccountEntityRepository accountEntityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

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
                        account = accountEntityRepository.save(new AccountEntity(a, passwordEncoder.encode("password"), a + "@gmail.com"));
                        account.getRoles().add(ProfileEntity.ROLE_ADMIN);
                        account.getRoles().add(ProfileEntity.ROLE_USER);
                    } else if (a.equalsIgnoreCase("user1")) {
                        account = accountEntityRepository.save(new IndividualEntity(a, passwordEncoder.encode("password"), a + "@gmail.com", "Phil", "Lim", GenderEnum.MALE));
                        account.getRoles().add(ProfileEntity.ROLE_USER);
                    } else {
                        account = accountEntityRepository.save(new OrganisationEntity(a, passwordEncoder.encode("password"), a + "@gmail.com", "NUS"));
                        account.getRoles().add(ProfileEntity.ROLE_USER);
                    }
                    accountEntityRepository.save(account);
                });
    }
}
