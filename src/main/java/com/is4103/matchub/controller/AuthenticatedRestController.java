package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.IndividualUpdateVO;
import com.is4103.matchub.vo.OrganisationUpdateVO;
import com.is4103.matchub.vo.ChangePasswordVO;
import com.is4103.matchub.vo.DeleteOrganisationDocumentsVO;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/authenticated")
public class AuthenticatedRestController {

    @Autowired
    UserService userService;

    @Autowired
    AttachmentService attachmentService;

    @RequestMapping(method = RequestMethod.GET, value = "/me")
    AccountEntity getMe(Principal principal) {
        return userService.getAccount(principal.getName());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAccount/{id}")
    AccountEntity getAccount(@PathVariable Long id) {
        return userService.getAccount(id);
    }

//    @RequestMapping(method = RequestMethod.GET, value = "/getAccount/{uuid}")
//    AccountEntity getAccount(@PathVariable UUID uuid) {
//        return userService.getAccount(uuid);
//    }
//    
//    @RequestMapping(method = RequestMethod.GET, value = "/getAccount/{email}")
//    AccountEntity getAccount(@PathVariable String email) {
//        return userService.getAccount(email);
//    }
    @RequestMapping(method = RequestMethod.GET, value = "/getFollowing/{id}")
    Page<ProfileEntity> getFollowing(@PathVariable Long id, Pageable pageable) {
        return userService.getFollowing(id, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getFollowers/{id}")
    Page<ProfileEntity> getFollowers(@PathVariable Long id, Pageable pageable) {
        return userService.getFollowers(id, pageable);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteAccount/{id}")
    void deleteAccount(@PathVariable Long id) {
        userService.delete(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteProfilePict/{id}")
    AccountEntity deleteProfilePic(@PathVariable Long id) throws IOException {
        return userService.deleteProfilePic(id);
    }

//    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteOrgVerificationDoc/{id}")
//    AccountEntity deleteOrgVerificationDoc(@PathVariable Long id, @RequestParam(value = "filenamewithextension") String filenamewithextension) throws IOException {
//        return userService.deleteOrgVerificationDoc(id, filenamewithextension);
//    }
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteOrgVerificationDocs/{id}")
    AccountEntity deleteOrgVerificationDocs(@PathVariable Long id, @Valid @RequestBody DeleteOrganisationDocumentsVO fileNamesWithExtension) throws IOException {
        return userService.deleteOrgVerificationDocs(id, fileNamesWithExtension);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateIndividual")
    IndividualEntity updateIndividual(@Valid @RequestBody IndividualUpdateVO updateVO) {
        //does not include update profile pic
        return userService.updateIndividual(updateVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateOrganisation")
    OrganisationEntity updateOrganisation(@Valid @RequestBody OrganisationUpdateVO updateVO) {
        //does not include update profile pic
        return userService.updateOrganisation(updateVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/changePassword/{uuid}")
    AccountEntity changePassword(@PathVariable("uuid") UUID uuid, @Valid @RequestBody ChangePasswordVO vo) {
        return userService.changePassword(uuid, vo);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/followProfile")
    AccountEntity followProfile(@RequestParam(value = "accountId") Long accountId, @RequestParam(value = "followId") Long followId) {
        return userService.followProfile(accountId, followId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/unfollowProfile")
    AccountEntity unfollowProfile(@RequestParam(value = "accountId") Long accountId, @RequestParam(value = "unfollowId") Long unfollowId) {
        return userService.unfollowProfile(accountId, unfollowId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/removeFollower")
    AccountEntity removeFollower(@RequestParam(value = "accountId") Long accountId, @RequestParam(value = "removeFollowerId") Long removeFollowerId) {
        return userService.removeFollower(accountId, removeFollowerId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateIndividual/updateProfilePic/{uuid}")
    public AccountEntity updateIndividualFile(@RequestParam(value = "file") MultipartFile file, @PathVariable("uuid") UUID uuid) {
//        return attachmentService.upload(file, directory);
        String filePath = attachmentService.upload(file);

        System.out.println("uploaded file successfully: relative pathImage is " + filePath);
        return userService.setProfilePic(uuid, filePath);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateOrganisation/updateProfilePic/{uuid}")
    public AccountEntity updateOrganisationFile(@RequestParam(value = "file") MultipartFile file, @PathVariable("uuid") UUID uuid) {
//        return attachmentService.upload(file, directory);
        String filePath = attachmentService.upload(file);

        System.out.println("uploaded file successfully: relative pathImage is " + filePath);
        return userService.setProfilePic(uuid, filePath);
    }
}
