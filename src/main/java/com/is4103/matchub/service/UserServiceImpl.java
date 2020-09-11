/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.exception.DeleteProfilePictureException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.exception.EmailExistException;
import com.is4103.matchub.exception.UnableToFollowProfileException;
import com.is4103.matchub.exception.UnableToUnfollowProfileException;
import com.is4103.matchub.exception.UpdateProfileException;
import com.is4103.matchub.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import com.is4103.matchub.repository.TaskEntityRepository;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.IndividualSetupVO;
import com.is4103.matchub.vo.OrganisationCreateVO;
import com.is4103.matchub.vo.OrganisationSetupVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ReviewEntityRepository;
import com.is4103.matchub.vo.IndividualUpdateVO;
import com.is4103.matchub.vo.OrganisationUpdateVO;
import com.is4103.matchub.vo.ChangePasswordVO;

/**
 *
 * @author ngjin
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AccountEntityRepository accountEntityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SDGEntityRepository sdgEntityRepository;

    @Autowired
    private TaskEntityRepository taskEntityRepository;

    @Autowired
    private ResourceEntityRepository resourceEntityRepository;

    @Autowired
    private ReviewEntityRepository reviewEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Transactional
    @Override
    public UserVO createIndividual(IndividualCreateVO vo) throws MessagingException, IOException {
        Optional<AccountEntity> oldAccount = accountEntityRepository.findByEmail(vo.getEmail());
        if (oldAccount.isPresent()) {
            throw new EmailExistException(vo.getEmail());
        }

        IndividualEntity newIndividual = new IndividualEntity();
        vo.updateIndividualAccount(newIndividual, passwordEncoder);
        //typecast the individual account into a generic account to persist it in DB
        AccountEntity newAccount = (AccountEntity) newIndividual;
        newAccount = accountEntityRepository.save(newAccount);

        //auto trigger the sendVerificationEmail method
        emailService.sendVerificationEmail(newAccount.getEmail());

        return UserVO.of(newAccount);

    }

    @Transactional
    @Override
    public UserVO createOrganisation(OrganisationCreateVO vo) throws MessagingException, IOException {
        Optional<AccountEntity> oldAccount = accountEntityRepository.findByEmail(vo.getEmail());
        if (oldAccount.isPresent()) {
            throw new EmailExistException(vo.getEmail());
        }

        OrganisationEntity newOrganisation = new OrganisationEntity();
        vo.updateOrganisationAccount(newOrganisation, passwordEncoder);
        //typecast the organisation account into a generic account to persist it in DB
        AccountEntity newAccount = (AccountEntity) newOrganisation;
        newAccount = accountEntityRepository.save(newAccount);

        //auto trigger the sendVerificationEmail method
        emailService.sendVerificationEmail(newAccount.getEmail());

        return UserVO.of(newAccount);
    }

    @Transactional
    @Override
    public UserVO setupIndividualProfile(UUID uuid, IndividualSetupVO vo) {
        Optional<AccountEntity> currentAccount = accountEntityRepository.findByUuid(uuid);

        if (!currentAccount.isPresent()) {
            throw new UserNotFoundException(uuid);
        }

        //type cast into individual entity first 
        IndividualEntity individual = (IndividualEntity) currentAccount.get();
        vo.setupIndividualAccount(individual);

        //find the SDG and associate with individual 
        individual.getSdgs().clear();
        for (int i = 0; i < vo.getSdgIds().length; i++) {
            SDGEntity sdg = sdgEntityRepository.findBySdgId(vo.getSdgIds()[i]);
            individual.getSdgs().add(sdg);
        }

        individual.setDisabled(Boolean.FALSE);
        individual.setIsVerified(Boolean.TRUE);

        AccountEntity updatedAccount = (AccountEntity) individual;
        updatedAccount = accountEntityRepository.save(updatedAccount);

        return UserVO.of(updatedAccount);
    }

    @Transactional
    @Override
    public UserVO setupOrganisationProfile(UUID uuid, OrganisationSetupVO vo) {
        Optional<AccountEntity> currentAccount = accountEntityRepository.findByUuid(uuid);

        if (!currentAccount.isPresent()) {
            throw new UserNotFoundException(uuid);
        }

        //type cast into organisation entity first 
        OrganisationEntity organisation = (OrganisationEntity) currentAccount.get();
        vo.setupOrganisationAccount(organisation);

        //find the SDG and associate with organisation 
        organisation.getSdgs().clear();
        for (int i = 0; i < vo.getSdgIds().length; i++) {
            SDGEntity sdg = sdgEntityRepository.findBySdgId(vo.getSdgIds()[i]);
            organisation.getSdgs().add(sdg);
        }

        organisation.setDisabled(Boolean.FALSE);
        organisation.setIsVerified(Boolean.TRUE);

        AccountEntity updatedAccount = (AccountEntity) organisation;
        updatedAccount = accountEntityRepository.save(updatedAccount);

        return UserVO.of(updatedAccount);
    }

    @Override
    public AccountEntity setProfilePic(UUID uuid, String directory) {
        Optional<AccountEntity> currentAccount = accountEntityRepository.findByUuid(uuid);

        if (!currentAccount.isPresent()) {
            throw new UserNotFoundException(uuid);
        }

        if (currentAccount.get() instanceof IndividualEntity) {
            IndividualEntity individual = (IndividualEntity) currentAccount.get();
            individual.setProfilePhoto(directory);

            AccountEntity updatedAccount = (AccountEntity) individual;
            updatedAccount = accountEntityRepository.save(updatedAccount);
            return updatedAccount;
        } else {// must be organisation
            OrganisationEntity organisation = (OrganisationEntity) currentAccount.get();
            organisation.setProfilePhoto(directory);

            AccountEntity updatedAccount = (AccountEntity) organisation;
            updatedAccount = accountEntityRepository.save(updatedAccount);
            return updatedAccount;
        }
    }

    @Transactional
    @Override
    public AccountEntity followProfile(Long accountId, Long followId) {
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        ProfileEntity toFollowProfile = profileEntityRepository.findById(followId)
                .orElseThrow(() -> new UnableToFollowProfileException("accountId: " + followId + " does not exist and cannot be followed"));

        if (!profile.getFollowing().contains(followId)) {
            //update account following
            profile.getFollowing().add(followId);
            profileEntityRepository.save(profile);

            //update toFollowProfile followers
            toFollowProfile.getFollowers().add(accountId);
            profileEntityRepository.save(toFollowProfile);

            return profile;
        } else {
            throw new UnableToFollowProfileException("Your account (accountId: "
                    + accountId + ") is unable to follow accountId: " + followId
                    + " because it is already in your following list");
        }
    }
    
    @Transactional
    @Override
    public AccountEntity unfollowProfile(Long accountId, Long unfollowId) {
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));
        
        ProfileEntity toUnfollowProfile = profileEntityRepository.findById(unfollowId)
                .orElseThrow(() -> new UnableToUnfollowProfileException("accountId: " + unfollowId + " does not exist and cannot be unfollowed"));
        
        if (profile.getFollowing().contains(unfollowId)) {
            //can unfollow 
            //remove unfollowId from profile following list
            profile.getFollowing().remove(unfollowId);
            profileEntityRepository.save(profile);
            
            //update toUnfollowProfile followers
            toUnfollowProfile.getFollowers().remove(accountId);
            profileEntityRepository.save(toUnfollowProfile);
            
            return profile;
        } else {
            throw new UnableToUnfollowProfileException("Your account (accountId: "
                    + accountId + ") is unable to unfollow accountId: " + unfollowId
                    + " because it is not in your following list");
        }
    }

//    @Override
//    public UserVO getAccount(Long accountId) {
//        AccountEntity account = accountEntityRepository.findById(accountId)
//                .orElseThrow(() -> new UserNotFoundException(accountId));
//        return UserVO.of(account);
//    }
//
//    @Override
//    public UserVO getAccount(String email) {
//        AccountEntity account = accountEntityRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException(email));
//        return UserVO.of(account);
//    }
    @Override
    public AccountEntity getAccount(Long accountId) {
        AccountEntity account = accountEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));
        return account;
    }

    @Override
    public AccountEntity getAccount(String email) {
        AccountEntity account = accountEntityRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return account;
    }

    @Override
    public AccountEntity getAccount(UUID uuid) {
        AccountEntity account = accountEntityRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
        return account;
    }

    @Override
    public List<AccountEntity> getAllAccounts() {
        return accountEntityRepository.findAll();
    }

    @Override
    public List<AccountEntity> getAllActiveAccounts() {
        return accountEntityRepository.findAllActiveAccounts();
    }

    @Override
    public List<AccountEntity> getAllFollowingAccounts(Long id) {
        AccountEntity account = accountEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        //type cast into individual or organisation
        if (account instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) account;

            //convert set to List
            List<Long> followingIds = new ArrayList<>(i.getFollowing());
            List<AccountEntity> following = new ArrayList<>();

            for (int j = 0; j < followingIds.size(); j++) {
                AccountEntity a = accountEntityRepository.findById(followingIds.get(j)).get();
                following.add(a);
            }
            return following;
        } else { //must be organisation
            OrganisationEntity o = (OrganisationEntity) account;

            //convert set to List
            List<Long> followingIds = new ArrayList<>(o.getFollowing());
            List<AccountEntity> following = new ArrayList<>();

            for (int j = 0; j < followingIds.size(); j++) {
                AccountEntity a = accountEntityRepository.findById(followingIds.get(j)).get();
                following.add(a);
            }
            return following;
        }

    }

    @Override
    public List<AccountEntity> getAllFollowerAccounts(Long id) {
        AccountEntity account = accountEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        //type cast into individual or organisation
        if (account instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) account;

            //convert set to List
            List<Long> followerIds = new ArrayList<>(i.getFollowers());
            List<AccountEntity> followers = new ArrayList<>();

            for (int j = 0; j < followerIds.size(); j++) {
                AccountEntity a = accountEntityRepository.findById(followerIds.get(j)).get();
                followers.add(a);
            }
            return followers;
        } else { //must be organisation
            OrganisationEntity o = (OrganisationEntity) account;

            //convert set to List
            List<Long> followerIds = new ArrayList<>(o.getFollowers());
            List<AccountEntity> followers = new ArrayList<>();

            for (int j = 0; j < followerIds.size(); j++) {
                AccountEntity a = accountEntityRepository.findById(followerIds.get(j)).get();
                followers.add(a);
            }
            return followers;
        }

    }

    @Transactional
    @Override
    public IndividualEntity updateIndividual(IndividualUpdateVO vo) {
        AccountEntity account = accountEntityRepository.findById(vo.getId())
                .orElseThrow(() -> new UserNotFoundException(vo.getId()));
        System.out.println("account is found");

        if (account instanceof IndividualEntity) {
            IndividualEntity individual = (IndividualEntity) account;
            System.out.println("typecasted to individual");

            vo.updateIndividualAccount(individual);

            if (vo.getSdgIds().length != 0) {
                //find the updated SDG and associate with individual 
                individual.getSdgs().clear();
                for (int i = 0; i < vo.getSdgIds().length; i++) {
                    SDGEntity sdg = sdgEntityRepository.findBySdgId(vo.getSdgIds()[i]);
                    individual.getSdgs().add(sdg);
                }
            }

            accountEntityRepository.save(individual);
            return individual;
        } else {
            throw new UpdateProfileException("Unable to update individual");
        }

    }

    @Transactional
    @Override
    public OrganisationEntity updateOrganisation(OrganisationUpdateVO vo) {
        AccountEntity account = accountEntityRepository.findById(vo.getId())
                .orElseThrow(() -> new UserNotFoundException(vo.getId()));

        if (account instanceof OrganisationEntity) {
            OrganisationEntity organisation = (OrganisationEntity) account;
            System.out.println("typecasted to organisation");

            vo.updateOrganisationAccount(organisation);

            if (vo.getSdgIds().length != 0) {
                //find the updated SDG and associate with individual 
                organisation.getSdgs().clear();
                for (int i = 0; i < vo.getSdgIds().length; i++) {
                    SDGEntity sdg = sdgEntityRepository.findBySdgId(vo.getSdgIds()[i]);
                    organisation.getSdgs().add(sdg);
                }
            }

            accountEntityRepository.save(organisation);
            return organisation;
        } else {
            throw new UpdateProfileException("Unable to update organsation");
        }
    }

    @Transactional
    @Override
    public void delete(Long accountId) {
        AccountEntity account = accountEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        Boolean deleteAccount = true;

        if (account instanceof IndividualEntity) {
            IndividualEntity i = (IndividualEntity) account;

            //account cannot be deleted if it has incomplete tasks, reviews given
            if (taskEntityRepository.getIncompleteTasksOfAccount(accountId).size() > 0
                    || reviewEntityRepository.getAllReviewsByAccount(accountId).size() > 0) {
                deleteAccount = false;
            }
        } else {
            OrganisationEntity o = (OrganisationEntity) account;

            //account cannot be deleted if it has incomplete tasks, reviews given
            if (taskEntityRepository.getIncompleteTasksOfAccount(accountId).size() > 0
                    || reviewEntityRepository.getAllReviewsByAccount(accountId).size() > 0) {
                deleteAccount = false;
            }
        }

        if (deleteAccount) {
            accountEntityRepository.delete(account);
        } else { // account cannot be deleted
            account.setDisabled(Boolean.TRUE);
            accountEntityRepository.save(account);
        }
    }

    @Transactional
    @Override
    public void deleteProfilePic(Long accountId) {
        AccountEntity account = accountEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        if (account instanceof ProfileEntity) {
            ProfileEntity p = (ProfileEntity) account;
            p.setProfilePhoto(null);
            accountEntityRepository.save(p);
        } else {
            throw new DeleteProfilePictureException("Unable to delete profile picture of accountId: " + accountId);
        }
    }

    @Transactional
    @Override
    public AccountEntity changePassword(UUID uuid, ChangePasswordVO vo) {
        AccountEntity account = accountEntityRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));

        vo.changePassword(account, passwordEncoder);

        accountEntityRepository.save(account);

        return account;
    }

    @Override
    public Page<UserVO> search(String search, Pageable pageable
    ) {
        Page<AccountEntity> accountPage;
        if (search.isEmpty()) {
            accountPage = accountEntityRepository.findAll(pageable);
        } else {
            accountPage = accountEntityRepository.search(search, pageable);
        }
        return accountPage.map((a) -> UserVO.of(a));
    }
}
