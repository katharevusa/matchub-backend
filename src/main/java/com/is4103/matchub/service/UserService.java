package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.IndividualSetupVO;
import com.is4103.matchub.vo.IndividualUpdateVO;
import com.is4103.matchub.vo.OrganisationCreateVO;
import com.is4103.matchub.vo.OrganisationSetupVO;
import com.is4103.matchub.vo.OrganisationUpdateVO;
import com.is4103.matchub.vo.ChangePasswordVO;
import com.is4103.matchub.vo.DeleteOrganisationDocumentsVO;
import com.is4103.matchub.vo.UserVO;
import java.io.IOException;
import java.util.UUID;
import javax.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserVO createIndividual(IndividualCreateVO vo) throws MessagingException, IOException;

    UserVO createOrganisation(OrganisationCreateVO vo) throws MessagingException, IOException;

    UserVO setupIndividualProfile(UUID uuid, IndividualSetupVO vo);

    UserVO setupOrganisationProfile(UUID uuid, OrganisationSetupVO vo);

    AccountEntity setProfilePic(UUID uuid, String directory);

//    AccountEntity setOrganisationVerificationDoc(UUID uuid, String directory, String filename);
    AccountEntity uploadOrganisationDocuments(UUID uuid, MultipartFile[] files);

    AccountEntity followProfile(Long accountId, Long followId);

    AccountEntity unfollowProfile(Long accountId, Long unfollowId);

    AccountEntity removeFollower(Long accountId, Long removeFollowerId);

//    UserVO getAccount(Long id);
//    UserVO getAccount(String username);
    AccountEntity getAccount(Long id);

    AccountEntity getAccount(String email);

    AccountEntity getAccount(UUID uuid);

    Page<AccountEntity> getAccountsByIds(Long[] ids, Pageable pageable);

//    List<AccountEntity> getAllAccounts();
//    List<AccountEntity> getAllActiveAccounts();
    Page<AccountEntity> getAllAccounts(Pageable pageable);

    Page<AccountEntity> getAllActiveAccounts(Pageable pageable);

//    List<AccountEntity> getAllFollowingAccounts(Long id);
    Page<ProfileEntity> getFollowers(Long accountId, Pageable pageable);

//    List<AccountEntity> getAllFollowerAccounts(Long id);
    Page<ProfileEntity> getFollowing(Long accountId, Pageable pageable);

    IndividualEntity updateIndividual(IndividualUpdateVO vo);

    OrganisationEntity updateOrganisation(OrganisationUpdateVO vo);

    void delete(Long id);

    AccountEntity deleteProfilePic(Long accountId) throws IOException;

//    AccountEntity deleteOrgVerificationDoc(Long accountId, String filenamewithextension) throws IOException;
    AccountEntity deleteOrgVerificationDocs(Long accountId, DeleteOrganisationDocumentsVO filenameswithextension) throws IOException;

    void triggerResetPasswordEmail(String email) throws MessagingException, IOException;

    AccountEntity changePassword(UUID uuid, ChangePasswordVO vo);

    Page<UserVO> search(String search, Pageable pageable);

    Page<IndividualEntity> searchIndividuals(String search, Pageable pageable);

}
