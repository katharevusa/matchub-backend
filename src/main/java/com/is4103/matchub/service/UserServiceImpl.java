/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.entity.SDGTargetEntity;
import com.is4103.matchub.entity.SelectedTargetEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import com.is4103.matchub.exception.DeleteOrganisationVerificationDocumentException;
import com.is4103.matchub.exception.DeleteProfilePictureException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.exception.EmailExistException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.UnableToFollowProfileException;
import com.is4103.matchub.exception.UnableToRemoveFollowerException;
import com.is4103.matchub.exception.UnableToSaveResourceException;
import com.is4103.matchub.exception.UnableToUnfollowProfileException;
import com.is4103.matchub.exception.UnableToUnsaveResourceException;
import com.is4103.matchub.exception.UpdateProfileException;
import com.is4103.matchub.exception.UploadOrganisationVerificationDocException;
import com.is4103.matchub.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.IndividualEntityRepository;
import com.is4103.matchub.repository.OrganisationEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import com.is4103.matchub.repository.TaskEntityRepository;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.IndividualSetupVO;
import com.is4103.matchub.vo.OrganisationCreateVO;
import com.is4103.matchub.vo.OrganisationSetupVO;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ReviewEntityRepository;
import com.is4103.matchub.repository.SDGTargetEntityRepository;
import com.is4103.matchub.repository.SelectedTargetEntityRepository;
import com.is4103.matchub.vo.IndividualUpdateVO;
import com.is4103.matchub.vo.OrganisationUpdateVO;
import com.is4103.matchub.vo.ChangePasswordVO;
import com.is4103.matchub.vo.DeleteFilesVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.multipart.MultipartFile;

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
    private AttachmentService attachmentService;

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

    @Autowired
    private IndividualEntityRepository individualEntityRepository;

    @Autowired
    private OrganisationEntityRepository organisationEntityRepository;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementEntityRepository announcementEntityRepository;

    @Autowired
    private SDGTargetEntityRepository sDGTargetEntityRepository;

    @Autowired
    private SelectedTargetEntityRepository selectedTargetEntityRepository;

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
        emailService.sendVerificationEmail(newAccount);

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
        emailService.sendVerificationEmail(newAccount);

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
//        for (int i = 0; i < vo.getSdgIds().length; i++) {
//            SDGEntity sdg = sdgEntityRepository.findBySdgId(vo.getSdgIds()[i]);
//            individual.getSdgs().add(sdg);
//        }
        //****************refactored implementation
        for (int i = 1; i <= 17; i++) {
            if (vo.getHashmapSDG().containsKey(Long.valueOf(i))) {
                SDGEntity sdg = sdgEntityRepository.findBySdgId(Long.valueOf(i));
                individual.getSdgs().add(sdg);

                SelectedTargetEntity selectedTargets = new SelectedTargetEntity();
                List<Long> targetIds = vo.getHashmapSDG().get(Long.valueOf(i));

                for (int j = 0; j < targetIds.size(); j++) {
                    //find the actual instance of the sdgTarget
                    SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetId(targetIds.get(j));
                    selectedTargets.getSdgTargets().add(sdgTarget);
                }

                selectedTargets.setSdg(sdg);
                selectedTargets.setProfile(individual);
                selectedTargetEntityRepository.saveAndFlush(selectedTargets);

                individual.getSelectedTargets().add(selectedTargets);
            }
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
//        for (int i = 0; i < vo.getSdgIds().length; i++) {
//            SDGEntity sdg = sdgEntityRepository.findBySdgId(vo.getSdgIds()[i]);
//            organisation.getSdgs().add(sdg);
//        }

        //****************refactored implementation
        for (int i = 1; i <= 17; i++) {
            if (vo.getHashmapSDG().containsKey(Long.valueOf(i))) {
                SDGEntity sdg = sdgEntityRepository.findBySdgId(Long.valueOf(i));
                organisation.getSdgs().add(sdg);

                SelectedTargetEntity selectedTargets = new SelectedTargetEntity();
                List<Long> targetIds = vo.getHashmapSDG().get(Long.valueOf(i));

                for (int j = 0; j < targetIds.size(); j++) {
                    //find the actual instance of the sdgTarget
                    SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetId(targetIds.get(j));
                    selectedTargets.getSdgTargets().add(sdgTarget);
                }

                selectedTargets.setSdg(sdg);
                selectedTargets.setProfile(organisation);
                selectedTargetEntityRepository.saveAndFlush(selectedTargets);

                organisation.getSelectedTargets().add(selectedTargets);
            }
        }

        organisation.setDisabled(Boolean.FALSE);
        organisation.setIsVerified(Boolean.TRUE);

        AccountEntity updatedAccount = (AccountEntity) organisation;
        updatedAccount = accountEntityRepository.save(updatedAccount);

        return UserVO.of(updatedAccount);
    }

    @Transactional
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
    public AccountEntity uploadOrganisationDocuments(UUID uuid, MultipartFile[] files) {
        Optional<AccountEntity> currentAccount = accountEntityRepository.findByUuid(uuid);

        if (!currentAccount.isPresent()) {
            throw new UserNotFoundException(uuid);
        }

        if (currentAccount.get() instanceof OrganisationEntity) {
            OrganisationEntity organisation = (OrganisationEntity) currentAccount.get();

            for (MultipartFile file : files) {
                String filename = file.getOriginalFilename();
                String savedPath = attachmentService.upload(file);

                //Key: filename, Value: directory
                organisation.getVerificationDocHashMap().put(filename, savedPath);
            }

            AccountEntity updatedAccount = (AccountEntity) organisation;
            updatedAccount = accountEntityRepository.save(updatedAccount);
            return updatedAccount;
        } else {// throw exception 
            throw new UploadOrganisationVerificationDocException("Unable to upload verification documents.");
        }
    }

//    @Transactional
//    @Override
//    public AccountEntity setOrganisationVerificationDoc(UUID uuid, String directory, String filename) {
//        Optional<AccountEntity> currentAccount = accountEntityRepository.findByUuid(uuid);
//
//        if (!currentAccount.isPresent()) {
//            throw new UserNotFoundException(uuid);
//        }
//        
//        if (currentAccount.get() instanceof OrganisationEntity) {
//            OrganisationEntity organisation = (OrganisationEntity) currentAccount.get();
//            //Key: filename, Value: directory
//            organisation.getVerificationDocHashMap().put(filename, directory);
//
//            AccountEntity updatedAccount = (AccountEntity) organisation;
//            updatedAccount = accountEntityRepository.save(updatedAccount);
//            return updatedAccount;
//        } else {// throw exception 
//            throw new UploadOrganisationVerificationDocException("Unable to upload verification document.");
//        }
//    }
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
            toFollowProfile = profileEntityRepository.saveAndFlush(toFollowProfile);

            // create announcement (notify toFollowProfile)
            String profileName = "";
            if (profile instanceof IndividualEntity) {
                profileName = ((IndividualEntity) profile).getFirstName() + " " + ((IndividualEntity) profile).getLastName();
            } else if (profile instanceof OrganisationEntity) {
                profileName = ((OrganisationEntity) profile).getOrganizationName();
            }

            AnnouncementEntity announcementEntity = new AnnouncementEntity();
            announcementEntity.setTitle("New Follower");
            announcementEntity.setContent(profileName + " just followed you.");
            announcementEntity.setTimestamp(LocalDateTime.now());
            announcementEntity.setType(AnnouncementTypeEnum.NEW_PROFILE_FOLLOWER);
            announcementEntity.setNewFollowerAndNewPosterProfileId(accountId);
            announcementEntity.setNewFollowerAndNewPosterUUID(profile.getUuid());

            // association
            if (toFollowProfile.getAnnouncementsSetting().get(AnnouncementTypeEnum.NEW_PROFILE_FOLLOWER)) {
                announcementEntity.getNotifiedUsers().add(toFollowProfile);
                toFollowProfile.getAnnouncements().add(announcementEntity);
            }

            announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

            // create notification   
            if (toFollowProfile.getAnnouncementsSetting().get(AnnouncementTypeEnum.NEW_PROFILE_FOLLOWER)) {
                announcementService.createNormalNotification(announcementEntity);
            }

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

    @Transactional
    @Override
    public AccountEntity removeFollower(Long accountId, Long removeFollowerId) {
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        ProfileEntity toRemoveFollowerProfile = profileEntityRepository.findById(removeFollowerId)
                .orElseThrow(() -> new UnableToRemoveFollowerException("accountId: " + removeFollowerId + " does not exist and cannot be removed from your followers list"));

        if (profile.getFollowers().contains(removeFollowerId)) {
            //can remove follower
            //remove follower from profile followers
            profile.getFollowers().remove(removeFollowerId);
            profileEntityRepository.save(profile);

            //remove profile from toRemoveFollowerProfile following
            toRemoveFollowerProfile.getFollowing().remove(accountId);
            profileEntityRepository.save(toRemoveFollowerProfile);

            return profile;
        } else {
            throw new UnableToRemoveFollowerException("Your account (accountId: "
                    + accountId + ") is unable to remove follower with accountId: " + removeFollowerId
                    + " because it is not in your followers list");
        }
    }

    @Override
    public ProfileEntity saveResource(Long accountId, Long resourceId) throws ResourceNotFoundException {
        //find the profile 
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        //find the resource 
        ResourceEntity resourceToBeSaved = resourceEntityRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource Id " + resourceId + " is not found"));

        if (profile.getSavedResources().contains(resourceToBeSaved)) {
            throw new UnableToSaveResourceException("Unable to save resource: You have previously saved this resource");
        }

        //associate unidirectionally
        profile.getSavedResources().add(resourceToBeSaved);
        profile = profileEntityRepository.saveAndFlush(profile);

        return profile;

    }

    @Override
    public ProfileEntity unsaveResource(Long accountId, Long resourceId) throws ResourceNotFoundException {
        //find the profile 
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        //find the resource 
        ResourceEntity resourceToUnsave = resourceEntityRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource Id " + resourceId + " is not found"));

        if (!profile.getSavedResources().contains(resourceToUnsave)) {
            throw new UnableToUnsaveResourceException("Unable to unsave resource: This resource is not in your saved Resources liat");
        }

        profile.getSavedResources().remove(resourceToUnsave);
        profile = profileEntityRepository.saveAndFlush(profile);

        return profile;
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
    public Page<AccountEntity> getAccountsByIds(Long[] ids, Pageable pageable) {
        return accountEntityRepository.getAccountsByIds(ids, pageable);
    }

    @Override
    public Page<AccountEntity> getAccountsByUuid(UUID[] uuid, Pageable pageable) {
        return accountEntityRepository.getAccountsByUuid(uuid, pageable);
    }

//    @Override
//    public List<AccountEntity> getAllAccounts() {
//        return accountEntityRepository.findAll();
//    }
    @Override
    public Page<AccountEntity> getAllAccounts(Pageable pageable) {
        return accountEntityRepository.findAll(pageable);
    }

//    @Override
//    public List<AccountEntity> getAllActiveAccounts() {
//        return accountEntityRepository.findAllActiveAccounts();
//    }
    @Override
    public Page<AccountEntity> getAllActiveAccounts(Pageable pageable) {
        return accountEntityRepository.findAllActiveAccounts(pageable);
    }

    @Override
    public Page<ProfileEntity> getFollowers(Long accountId, Pageable pageable) {
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        Set<Long> followerIds = profile.getFollowers();
        return profileEntityRepository.getFollowers(followerIds, pageable);
    }

    @Override
    public Page<ProfileEntity> getFollowing(Long accountId, Pageable pageable) {
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        Set<Long> followingIds = profile.getFollowing();
        return profileEntityRepository.getFollowing(followingIds, pageable);
    }

//    @Override
//    public List<AccountEntity> getAllFollowingAccounts(Long id) {
//        AccountEntity account = accountEntityRepository.findById(id)
//                .orElseThrow(() -> new UserNotFoundException(id));
//
//        //type cast into individual or organisation
//        if (account instanceof IndividualEntity) {
//            IndividualEntity i = (IndividualEntity) account;
//
//            //convert set to List
//            List<Long> followingIds = new ArrayList<>(i.getFollowing());
//            List<AccountEntity> following = new ArrayList<>();
//
//            for (int j = 0; j < followingIds.size(); j++) {
//                AccountEntity a = accountEntityRepository.findById(followingIds.get(j)).get();
//                following.add(a);
//            }
//            return following;
//        } else { //must be organisation
//            OrganisationEntity o = (OrganisationEntity) account;
//
//            //convert set to List
//            List<Long> followingIds = new ArrayList<>(o.getFollowing());
//            List<AccountEntity> following = new ArrayList<>();
//
//            for (int j = 0; j < followingIds.size(); j++) {
//                AccountEntity a = accountEntityRepository.findById(followingIds.get(j)).get();
//                following.add(a);
//            }
//            return following;
//        }
//
//    }
//    @Override
//    public List<AccountEntity> getAllFollowerAccounts(Long id) {
//        AccountEntity account = accountEntityRepository.findById(id)
//                .orElseThrow(() -> new UserNotFoundException(id));
//
//        //type cast into individual or organisation
//        if (account instanceof IndividualEntity) {
//            IndividualEntity i = (IndividualEntity) account;
//
//            //convert set to List
//            List<Long> followerIds = new ArrayList<>(i.getFollowers());
//            List<AccountEntity> followers = new ArrayList<>();
//
//            for (int j = 0; j < followerIds.size(); j++) {
//                AccountEntity a = accountEntityRepository.findById(followerIds.get(j)).get();
//                followers.add(a);
//            }
//            return followers;
//        } else { //must be organisation
//            OrganisationEntity o = (OrganisationEntity) account;
//
//            //convert set to List
//            List<Long> followerIds = new ArrayList<>(o.getFollowers());
//            List<AccountEntity> followers = new ArrayList<>();
//
//            for (int j = 0; j < followerIds.size(); j++) {
//                AccountEntity a = accountEntityRepository.findById(followerIds.get(j)).get();
//                followers.add(a);
//            }
//            return followers;
//        }
//
//    }
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

//            if (vo.getSdgIds().length != 0) {
//                //find the updated SDG and associate with individual 
//            individual.getSdgs().clear();
//                for (int i = 0; i < vo.getSdgIds().length; i++) {
//                    SDGEntity sdg = sdgEntityRepository.findBySdgId(vo.getSdgIds()[i]);
//                    individual.getSdgs().add(sdg);
//                }
//            }
            if (!vo.getHashmapSDG().isEmpty()) {

                //clear the old associations first 
                individual.getSdgs().clear();

                List<SelectedTargetEntity> oldSelections = individual.getSelectedTargets();

                for (SelectedTargetEntity s : oldSelections) {
                    s.setProfile(null);
                    s.getSdgTargets().clear();
                    selectedTargetEntityRepository.delete(s);
                }

                individual.getSelectedTargets().clear();

                for (int i = 1; i <= 17; i++) {
                    if (vo.getHashmapSDG().containsKey(Long.valueOf(i))) {
                        SDGEntity sdg = sdgEntityRepository.findBySdgId(Long.valueOf(i));
                        individual.getSdgs().add(sdg);

                        SelectedTargetEntity selectedTargets = new SelectedTargetEntity();
                        List<Long> targetIds = vo.getHashmapSDG().get(Long.valueOf(i));

                        for (int j = 0; j < targetIds.size(); j++) {
                            //find the actual instance of the sdgTarget
                            SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetId(targetIds.get(j));
                            selectedTargets.getSdgTargets().add(sdgTarget);
                        }

                        selectedTargets.setSdg(sdg);
                        selectedTargets.setProfile(individual);
                        selectedTargetEntityRepository.saveAndFlush(selectedTargets);

                        individual.getSelectedTargets().add(selectedTargets);
                    }
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

//            if (vo.getSdgIds().length != 0) {
//                //find the updated SDG and associate with individual 
//                organisation.getSdgs().clear();
//                for (int i = 0; i < vo.getSdgIds().length; i++) {
//                    SDGEntity sdg = sdgEntityRepository.findBySdgId(vo.getSdgIds()[i]);
//                    organisation.getSdgs().add(sdg);
//                }
//            }
            if (!vo.getHashmapSDG().isEmpty()) {

                //clear the old associations first 
                organisation.getSdgs().clear();

                List<SelectedTargetEntity> oldSelections = organisation.getSelectedTargets();

                for (SelectedTargetEntity s : oldSelections) {
                    s.setProfile(null);
                    s.getSdgTargets().clear();
                    selectedTargetEntityRepository.delete(s);
                }

                organisation.getSelectedTargets().clear();

                for (int i = 1; i <= 17; i++) {
                    if (vo.getHashmapSDG().containsKey(Long.valueOf(i))) {
                        SDGEntity sdg = sdgEntityRepository.findBySdgId(Long.valueOf(i));
                        organisation.getSdgs().add(sdg);

                        SelectedTargetEntity selectedTargets = new SelectedTargetEntity();
                        List<Long> targetIds = vo.getHashmapSDG().get(Long.valueOf(i));

                        for (int j = 0; j < targetIds.size(); j++) {
                            //find the actual instance of the sdgTarget
                            SDGTargetEntity sdgTarget = sDGTargetEntityRepository.findBySdgTargetId(targetIds.get(j));
                            selectedTargets.getSdgTargets().add(sdgTarget);
                        }

                        selectedTargets.setSdg(sdg);
                        selectedTargets.setProfile(organisation);
                        selectedTargetEntityRepository.saveAndFlush(selectedTargets);

                        organisation.getSelectedTargets().add(selectedTargets);
                    }
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

            //account cannot be deleted if it has task assigned, reviews given
            if (i.getTasks().size() > 0
                    || reviewEntityRepository.getReviewsGivenByAccountId(accountId).size() > 0) {
                deleteAccount = false;
            }
        } else {
            OrganisationEntity o = (OrganisationEntity) account;

            //account cannot be deleted if it has task assigned, reviews given
            if (o.getTasks().size() > 0
                    || reviewEntityRepository.getReviewsGivenByAccountId(accountId).size() > 0) {
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
    public AccountEntity deleteProfilePic(Long accountId) throws IOException {
        AccountEntity account = accountEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        if (account instanceof ProfileEntity) {
            ProfileEntity p = (ProfileEntity) account;

            //call attachmentService to delete the actual file from /build folder
            attachmentService.deleteFile(p.getProfilePhoto());

            p.setProfilePhoto(null);
            accountEntityRepository.save(p);

            return p;
        } else {
            throw new DeleteProfilePictureException("Unable to delete profile picture of accountId: " + accountId);
        }
    }

//    @Transactional
//    @Override
//    public AccountEntity deleteOrgVerificationDoc(Long accountId, String filenamewithextension) throws IOException {
//        AccountEntity account = accountEntityRepository.findById(accountId)
//                .orElseThrow(() -> new UserNotFoundException(accountId));
//
//        if (account instanceof OrganisationEntity) {
//            OrganisationEntity o = (OrganisationEntity) account;
//
//            Map<String, String> hashmap = o.getVerificationDocHashMap();
//
//            //get the path of the document to delete
//            String selectedDocumentPath = hashmap.get(filenamewithextension);
//            if (selectedDocumentPath == null) {
//                throw new DeleteOrganisationVerificationDocumentException("Unable to delete organisation document (Document not found): " + filenamewithextension);
//            }
//
//            //if file is present, call attachmentService to delete the actual file from /build folder
//            attachmentService.deleteFile(selectedDocumentPath);
//
//            //successfully removed the actual file from /build folder, update organisation hashmap
//            hashmap.remove(filenamewithextension);
//
//            accountEntityRepository.save(o);
//
//            return o;
//        } else {
//            throw new DeleteOrganisationVerificationDocumentException("Unable to delete organisation document: " + filenamewithextension);
//        }
//    }
//    does not reflect the most updated instance 
//    @Transactional
//    @Override
//    public AccountEntity deleteOrgVerificationDocs(Long accountId, DeleteFilesVO docsToDelete) throws IOException {
//        AccountEntity account = accountEntityRepository.findById(accountId)
//                .orElseThrow(() -> new UserNotFoundException(accountId));
//
//        if (account instanceof OrganisationEntity) {
//            OrganisationEntity o = (OrganisationEntity) account;
//
//            for (String key : docsToDelete.getFileNamesWithExtension()) {
//                Map<String, String> hashmap = o.getVerificationDocHashMap();
//
//                //get the path of the document to delete
//                String selectedDocumentPath = hashmap.get(key);
//                if (selectedDocumentPath == null) {
//                    throw new DeleteOrganisationVerificationDocumentException("Unable to delete organisation document (Document not found): " + key);
//                }
//
//                //if file is present, call attachmentService to delete the actual file from /build folder
//                attachmentService.deleteFile(selectedDocumentPath);
//
//                //successfully removed the actual file from /build folder, update organisation hashmap
//                hashmap.remove(key);
//
//            }
//
//            //save once all documents are removed successfully
//            accountEntityRepository.save(o);
//            return o;
//        } else {
//            throw new DeleteOrganisationVerificationDocumentException("Unable to delete organisation documents for organisation with accountId: " + accountId);
//        }
//    }
    @Transactional
    @Override
    public AccountEntity deleteOrgVerificationDocs(Long accountId, DeleteFilesVO docsToDelete) throws IOException {
        AccountEntity account = accountEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        if (account instanceof OrganisationEntity) {
            OrganisationEntity o = (OrganisationEntity) account;
            Map<String, String> hashmap = o.getVerificationDocHashMap();

            //loop 1: check if all the documents are present
            for (String key : docsToDelete.getFileNamesWithExtension()) {
                //get the path of the document to delete
                String selectedDocumentPath = hashmap.get(key);
                if (selectedDocumentPath == null) {
                    throw new DeleteOrganisationVerificationDocumentException("Unable to delete organisation document (Document not found): " + key);
                }
            }

            //loop2: delete the actual file when all files are present
            for (String key : docsToDelete.getFileNamesWithExtension()) {
                //get the path of the document to delete
                String selectedDocumentPath = hashmap.get(key);
                //if file is present, call attachmentService to delete the actual file from /build folder
                attachmentService.deleteFile(selectedDocumentPath);
                //successfully removed the actual file from /build folder, update organisation hashmap
                hashmap.remove(key);
            }
            //save once all documents are removed successfully
            o.setVerificationDocHashMap(hashmap);
            accountEntityRepository.saveAndFlush(o);

            return o;
        } else {
            throw new DeleteOrganisationVerificationDocumentException("Unable to delete organisation documents for organisation with accountId: " + accountId);
        }
    }

    @Transactional
    @Override
    public void triggerResetPasswordEmail(String email) throws MessagingException, IOException {
        AccountEntity account = accountEntityRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        emailService.sendResetPasswordEmail(account);
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
    public Page<UserVO> search(String search, Pageable pageable) {
        Page<AccountEntity> accountPage;
        if (search.isEmpty()) {
            accountPage = accountEntityRepository.findAll(pageable);
        } else {
            accountPage = accountEntityRepository.search(search, pageable);
        }
        return accountPage.map((a) -> UserVO.of(a));
    }

    @Override
    public Page<IndividualEntity> searchIndividuals(String search, Pageable pageable) {
        Page<IndividualEntity> page;
        if (search.isEmpty()) {
            page = individualEntityRepository.findAll(pageable);
        } else {
            page = individualEntityRepository.searchIndividuals(search, pageable);
        }
        return page;
    }

    @Override
    public Page<OrganisationEntity> searchOrganisations(String search, Pageable pageable) {
        Page<OrganisationEntity> page;
        if (search.isEmpty()) {
            page = organisationEntityRepository.findAll(pageable);
        } else {
            page = organisationEntityRepository.searchOrganisations(search, pageable);
        }
        return page;
    }

    @Override
    public Page<ProfileEntity> searchAllUsers(String search, Pageable pageable) {
        Page<ProfileEntity> page;
        if (search.isEmpty()) {
            page = profileEntityRepository.findAll(pageable);
        } else {
            page = profileEntityRepository.searchAllUsers(search, pageable);
        }
        return page;
    }

    @Override
    public Page<ProfileEntity> globalSearchAllUsers(String search, String country, Long[] sdgIds, long[] sdgTargetIds, Pageable pageable) {

        Page<ProfileEntity> page;

        if (search.isEmpty() && country.isEmpty() && sdgIds.length == 0) {
            page = profileEntityRepository.findAll(pageable);
        } else if (country.isEmpty()) {
            page = profileEntityRepository.globalSearchAllUsers(search, sdgIds, sdgTargetIds, pageable);

            if (page.isEmpty()) {
                //spilt the keywords
                search = search.trim();
                String[] split = search.split(" ");

                Set<ProfileEntity> temp = new HashSet<>();

                for (String s : split) {
                    temp.addAll(profileEntityRepository.globalSearchAllUsers(s, sdgIds, sdgTargetIds, pageable).toList());
                }

                //convert set into List
                List<ProfileEntity> resultsList = new ArrayList<>(temp);

                Long start = pageable.getOffset();
                Long end = (start + pageable.getPageSize()) > resultsList.size() ? resultsList.size() : (start + pageable.getPageSize());
                page = new PageImpl<ProfileEntity>(resultsList.subList(start.intValue(), end.intValue()), pageable, resultsList.size());

            }
        } else {
            page = profileEntityRepository.globalSearchAllUsers(search, country, sdgIds, pageable);

            if (page.isEmpty()) {
                //spilt the keywords
                search = search.trim();
                String[] split = search.split(" ");

                Set<ProfileEntity> temp = new HashSet<>();

                for (String s : split) {
                    temp.addAll(profileEntityRepository.globalSearchAllUsers(s, country, sdgIds, pageable).toList());
                }

                //convert set into List
                List<ProfileEntity> resultsList = new ArrayList<>(temp);

                Long start = pageable.getOffset();
                Long end = (start + pageable.getPageSize()) > resultsList.size() ? resultsList.size() : (start + pageable.getPageSize());
                page = new PageImpl<ProfileEntity>(resultsList.subList(start.intValue(), end.intValue()), pageable, resultsList.size());

            }
        }

        return page;
    }

    @Override
    public void setUserStripeAccountUid(String email, String stripeAccountUid) {

        Optional<ProfileEntity> accountOptional = profileEntityRepository.findByEmail(email);

        if (!accountOptional.isPresent()) {
            throw new UserNotFoundException("No user found with the provided email.");
        }

        ProfileEntity profile = (ProfileEntity) accountOptional.get();
        profile.setStripeAccountUid(stripeAccountUid);

        profileEntityRepository.saveAndFlush(profile);
    }

    @Override
    public void setUserStripeAccountChargesEnabled(String stripeAccountUid) {

        Optional<ProfileEntity> accountOptional = profileEntityRepository.findByStripeAccountUid(stripeAccountUid);

        if (!accountOptional.isPresent()) {
            throw new UserNotFoundException("No user found with the provided Stripe account ID.");
        }

        ProfileEntity profile = (ProfileEntity) accountOptional.get();
        profile.setStripeAccountChargesEnabled(true);

        profileEntityRepository.saveAndFlush(profile);
    }
}
