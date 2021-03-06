package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import com.is4103.matchub.repository.AccountEntityRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    AccountEntityRepository accountEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<AccountEntity> optionalAccount = accountEntityRepository.findByEmail(email);
        User.UserBuilder builder = null;
        if (optionalAccount.isPresent()) {
            AccountEntity account = optionalAccount.get();
            builder = User.withUsername(email);
            builder.password(account.getPassword());
            builder.accountLocked(account.getAccountLocked());
            builder.accountExpired(account.getAccountExpired());
            builder.disabled(account.getDisabled());
            String[] roles = account.getRoles().toArray(new String[account.getRoles().size()]);
            builder.roles(roles);
//            builder.isVerified = account.getIsVerified();
//            builder.joinDate = account.getJoinDate();

        } else {
            throw new UsernameNotFoundException("User not found.");
        }

        return builder.build();
    }
}
