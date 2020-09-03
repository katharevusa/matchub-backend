package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.exception.EmailExistException;
import com.is4103.matchub.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.OrganisationCreateVO;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AccountEntityRepository accountEntityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserVO createIndividual(IndividualCreateVO vo) {
        Optional<AccountEntity> oldAccount = accountEntityRepository.findByEmail(vo.getEmail());
        if (oldAccount.isPresent()) {
            throw new EmailExistException(vo.getEmail());
        }
//        AccountEntity account = new AccountEntity();
//        vo.updateAccount(account, passwordEncoder);
//        account = accountEntityRepository.save(account);
//        return UserVO.of(account);

        IndividualEntity newIndividual = new IndividualEntity();
        vo.updateIndividualAccount(newIndividual, passwordEncoder);
        //typecast the individual account into a generic account to persist it in DB
        AccountEntity newAccount = (AccountEntity) newIndividual;
        newAccount = accountEntityRepository.save(newAccount);
        return UserVO.of(newAccount);
    }

    @Transactional
    @Override
    public UserVO createOrganisation(OrganisationCreateVO vo) {
        Optional<AccountEntity> oldAccount = accountEntityRepository.findByEmail(vo.getEmail());
        if (oldAccount.isPresent()) {
            throw new EmailExistException(vo.getEmail());
        }
        
        OrganisationEntity newOrganisation = new OrganisationEntity();
        vo.updateOrganisationAccount(newOrganisation, passwordEncoder);
        //typecast the individual account into a generic account to persist it in DB
        AccountEntity newAccount = (AccountEntity) newOrganisation;
        newAccount = accountEntityRepository.save(newAccount);
        return UserVO.of(newAccount);
    }

    @Override
    public UserVO get(Long accountId) {
        AccountEntity account = accountEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));
        return UserVO.of(account);
    }

    @Override
    public UserVO get(String email) {
        AccountEntity account = accountEntityRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
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
