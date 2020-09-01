package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.exception.UsernameConflictException;
import com.is4103.matchub.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.vo.IndividualCreateVO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AccountEntityRepository accountEntityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserVO create(IndividualCreateVO vo) {
        Optional<AccountEntity> oldAccount = accountEntityRepository.findByUsername(vo.getUsername());
        if (oldAccount.isPresent()) {
            throw new UsernameConflictException(vo.getUsername());
        }
        AccountEntity account = new AccountEntity();
        vo.updateAccount(account, passwordEncoder);
        account = accountEntityRepository.save(account);
        return UserVO.of(account);
    }

    @Override
    public UserVO get(Long accountId) {
        AccountEntity account = accountEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));
        return UserVO.of(account);
    }

    @Override
    public UserVO get(String username) {
        AccountEntity account = accountEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return UserVO.of(account);
    }

    @Transactional
    @Override
    public void delete(Long accountId) {
        AccountEntity account = accountEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));
        accountEntityRepository.delete(account);
    }

    @Override
    public Page<UserVO> search(String search, Pageable pageable) {
        Page<AccountEntity> accountPage;
        if (search.isEmpty()) {
            accountPage = accountEntityRepository.findAll(pageable);
        } else {
            accountPage = accountEntityRepository.search(search, pageable);
        }
        return accountPage.map((a) -> UserVO.of(a));
    }
}
