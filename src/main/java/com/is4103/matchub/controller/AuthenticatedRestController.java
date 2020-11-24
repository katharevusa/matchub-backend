package com.is4103.matchub.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.FirebaseService;
import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.IndividualUpdateVO;
import com.is4103.matchub.vo.OrganisationUpdateVO;
import com.is4103.matchub.vo.ChangePasswordVO;
import com.is4103.matchub.vo.DeleteFilesVO;
import com.is4103.matchub.vo.GetAccountsByUuidVO;
import java.io.IOException;
import java.security.Principal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.LongStream;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/authenticated")
public class AuthenticatedRestController {

    @Autowired
    UserService userService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    FirebaseService firebaseService;

    @RequestMapping(method = RequestMethod.GET, value = "/me")
    AccountEntity getMe(Principal principal) {
        return userService.getAccount(principal.getName());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/firebaseToken/{uuid}")
    String getFirebaseToken(@PathVariable("uuid") UUID uuid) throws FirebaseAuthException {
        return firebaseService.issueFirebaseCustomToken(uuid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAccount/{id}")
    AccountEntity getAccount(@PathVariable("id") Long id) {
        return userService.getAccount(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAccountsByIds")
    Page<AccountEntity> getAccountsByIds(@RequestParam(value = "ids") Long[] ids, Pageable pageable) {
        return userService.getAccountsByIds(ids, pageable);
    }

    @PostMapping(value = "/getAccountsByUuid")
    @ResponseBody
    Page<AccountEntity> getAccountsByUuid(@Valid @RequestBody GetAccountsByUuidVO vo, Pageable pageable) {
        return userService.getAccountsByUuid(vo.getUuid(), pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAccountByUUID/{uuid}")
    AccountEntity getAccount(@PathVariable UUID uuid) {
        return userService.getAccount(uuid);
    }

//    
//    @RequestMapping(method = RequestMethod.GET, value = "/getAccount/{email}")
//    AccountEntity getAccount(@PathVariable String email) {
//        return userService.getAccount(email);
//    }
    @RequestMapping(method = RequestMethod.GET, value = "/getFollowing/{id}")
    Page<ProfileEntity> getFollowing(@PathVariable("id") Long id, Pageable pageable) {
        return userService.getFollowing(id, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getFollowers/{id}")
    Page<ProfileEntity> getFollowers(@PathVariable("id") Long id, Pageable pageable) {
        return userService.getFollowers(id, pageable);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteAccount/{id}")
    void deleteAccount(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteProfilePict/{id}")
    AccountEntity deleteProfilePic(@PathVariable("id") Long id) throws IOException {
        return userService.deleteProfilePic(id);
    }

//    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteOrgVerificationDoc/{id}")
//    AccountEntity deleteOrgVerificationDoc(@PathVariable Long id, @RequestParam(value = "filenamewithextension") String filenamewithextension) throws IOException {
//        return userService.deleteOrgVerificationDoc(id, filenamewithextension);
//    }
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteOrgVerificationDocs/{id}")
    AccountEntity deleteOrgVerificationDocs(@PathVariable("id") Long id, @Valid @RequestBody DeleteFilesVO fileNamesWithExtension) throws IOException {
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

    @RequestMapping(method = RequestMethod.POST, value = "/saveResource")
    AccountEntity saveResource(@RequestParam(value = "accountId") Long accountId, @RequestParam(value = "resourceId") Long resourceId) throws ResourceNotFoundException {
        return userService.saveResource(accountId, resourceId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/unsaveResource")
    AccountEntity unsaveResource(@RequestParam(value = "accountId") Long accountId, @RequestParam(value = "resourceId") Long resourceId) throws ResourceNotFoundException {
        return userService.unsaveResource(accountId, resourceId);
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

    /*
     * SEARCH METHOD CALLS HERE
     */
    @RequestMapping(method = RequestMethod.GET, value = "/searchIndividuals")
    Page<IndividualEntity> searchIndividuals(@RequestParam(value = "search") String search, Pageable pageable) {
        return userService.searchIndividuals(search, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/searchOrganisations")
    Page<OrganisationEntity> searchOrganisations(@RequestParam(value = "search") String search, Pageable pageable) {
        return userService.searchOrganisations(search, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/searchAllUsers")
    Page<ProfileEntity> searchAllUsers(@RequestParam(value = "search") String search, Pageable pageable) {
        return userService.searchAllUsers(search, pageable);
    }

    // ************** GLOBAL SEARCH METHOD FOR PROFILE EXPLORATION ************** //
    @RequestMapping(method = RequestMethod.GET, value = "/globalSearchAllUsers")
    Page<ProfileEntity> globalSearchAllUsers(@RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "country", defaultValue = "") String country,
            @RequestParam(value = "sdgIds", defaultValue = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17") Long[] sdgIds,
            @RequestParam(value = "sdgTargetIds", defaultValue = "") long[] sdgTargetIds,
            Pageable pageable) {
        
        if (sdgTargetIds.length == 0) {
            sdgTargetIds = LongStream.rangeClosed(1, 169).toArray();
        }
        return userService.globalSearchAllUsers(search, country, sdgIds, sdgTargetIds, pageable);
    }
}
