package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.IndividualSetupVO;
import com.is4103.matchub.vo.IndividualUpdateVO;
import com.is4103.matchub.vo.OrganisationCreateVO;
import com.is4103.matchub.vo.OrganisationSetupVO;
import com.is4103.matchub.vo.OrganisationUpdateVO;
import com.is4103.matchub.vo.UserVO;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserVO createIndividual(IndividualCreateVO vo) throws MessagingException, IOException;

    UserVO createOrganisation(OrganisationCreateVO vo) throws MessagingException, IOException;

    UserVO setupIndividualProfile(UUID uuid, IndividualSetupVO vo);

    UserVO setupOrganisationProfile(UUID uuid, OrganisationSetupVO vo);

    AccountEntity setProfilePic(UUID uuid, String directory);

//    UserVO getAccount(Long id);
//    UserVO getAccount(String username);
    AccountEntity getAccount(Long id);

    AccountEntity getAccount(String email);

    AccountEntity getAccount(UUID uuid);

    List<AccountEntity> getAllAccounts();

    List<AccountEntity> getAllActiveAccounts();

    List<AccountEntity> getAllFollowingAccounts(Long id);

    List<AccountEntity> getAllFollowerAccounts(Long id);

    IndividualEntity updateIndividual(IndividualUpdateVO vo);

    OrganisationEntity updateOrganisation(OrganisationUpdateVO vo);

    void delete(Long id);

    Page<UserVO> search(String search, Pageable pageable);

}
