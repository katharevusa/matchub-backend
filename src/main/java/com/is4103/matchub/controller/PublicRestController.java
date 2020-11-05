/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.exception.DonationOptionNotFoundException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.BadgeService;
import com.is4103.matchub.service.EmailService;
import com.is4103.matchub.service.OrganisationService;
import com.is4103.matchub.service.PostService;
import com.is4103.matchub.service.ProjectService;
import com.is4103.matchub.service.ResourceCategoryService;
import com.is4103.matchub.service.StripeService;
import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.IndividualSetupVO;
import com.is4103.matchub.vo.OrganisationCreateVO;
import com.is4103.matchub.vo.OrganisationSetupVO;
import com.is4103.matchub.vo.ChangePasswordVO;
import com.is4103.matchub.vo.UserVO;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
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

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/public")
public class PublicRestController {

    @Autowired
    UserService userService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    EmailService emailService;

    @Autowired
    BadgeService badgeService;

    @Autowired
    PostService postService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ResourceCategoryService resourceCategoryService;

    @Autowired
    OrganisationService organisationService;

    @Autowired
    StripeService stripeService;

    @RequestMapping(method = RequestMethod.POST, value = "/createNewIndividual")
    UserVO createNewIndividual(@Valid @RequestBody IndividualCreateVO createVO) throws MessagingException, IOException {
        return userService.createIndividual(createVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createNewOrganisation")
    UserVO createNewOrgansation(@Valid @RequestBody OrganisationCreateVO createVO) throws MessagingException, IOException {
        return userService.createOrganisation(createVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setupIndividualProfile/{uuid}")
    public UserVO setupIndividualProfile(@PathVariable("uuid") UUID uuid, @Valid @RequestBody IndividualSetupVO setupVO) {
        //this does not include setting profile pic
        return userService.setupIndividualProfile(uuid, setupVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setupOrganisationProfile/{uuid}")
    public UserVO setupOrganisationProfile(@PathVariable("uuid") UUID uuid, @Valid @RequestBody OrganisationSetupVO setupVO) {
        //this does not include setting profile pic
        return userService.setupOrganisationProfile(uuid, setupVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setupIndividualProfile/uploadProfilePic/{uuid}")
    public AccountEntity uploadIndividualFile(@RequestParam(value = "file") MultipartFile file, @PathVariable("uuid") UUID uuid) {
//        return attachmentService.upload(file, directory);
        String filePath = attachmentService.upload(file);

        System.out.println("uploaded file successfully: saved pathImage is " + filePath);
        return userService.setProfilePic(uuid, filePath);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setupOrganisationProfile/uploadProfilePic/{uuid}")
    public AccountEntity uploadOrganisationFile(@RequestParam(value = "file") MultipartFile file, @PathVariable("uuid") UUID uuid) {
//        return attachmentService.upload(file, directory);
        String filePath = attachmentService.upload(file);

        System.out.println("uploaded file successfully: saved pathImage is " + filePath);
        return userService.setProfilePic(uuid, filePath);
    }

//    @RequestMapping(method = RequestMethod.POST, value = "/setupOrganisationProfile/uploadDocument/{uuid}")
//    public AccountEntity uploadOrganisationDocument(@RequestParam(value = "file") MultipartFile file,
//            @RequestParam(value = "filename") String filename, @PathVariable("uuid") UUID uuid) {
//
//        String filePath = attachmentService.upload(file);
//
//        System.out.println("uploaded verification doc successfully: saved path is " + filePath);
//        return userService.setOrganisationVerificationDoc(uuid, filePath, filename);
//    }
//    
    @RequestMapping(method = RequestMethod.POST, value = "/setupOrganisationProfile/uploadDocuments/{uuid}")
    public AccountEntity uploadOrganisationDocuments(@RequestParam(value = "files") MultipartFile[] files, @PathVariable("uuid") UUID uuid) {
        return userService.uploadOrganisationDocuments(uuid, files);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/forgotPassword")
    public void sendResetPasswordEmail(@RequestParam(value = "email") String email) throws MessagingException, IOException {
        userService.triggerResetPasswordEmail(email);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/resetPassword/{uuid}")
    public void resetPassword(@PathVariable("uuid") UUID uuid, @Valid @RequestBody ChangePasswordVO vo) {
        userService.changePassword(uuid, vo);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getBadgesByAccountId/{accountId}")
    Page<BadgeEntity> getBadgesByAccountId(@PathVariable("accountId") Long postId, Pageable pageable) {
        return badgeService.getBadgesByAccountId(postId, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getCreatedProjects")
    List<ProjectEntity> getCreatedProjects(@RequestParam(value = "profileId", required = true) Long profileId) throws UserNotFoundException {
        return projectService.getCreatedProjects(profileId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPostsByAccountId/{accountId}")
    Page<PostEntity> getPostsByAccountId(@PathVariable("accountId") Long accountId, Pageable pageable) {
        return postService.getPostsByAccountId(accountId, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllResourceCategories")
    Page<ResourceCategoryEntity> getAllResourceCategories(Pageable pageable) {
        return resourceCategoryService.getAllResourceCategories(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "organisation/viewMembers/{organisationId}")
    Page<ProfileEntity> viewOrganisationMembers(@PathVariable("organisationId") Long organisationId, Pageable pageable) {
        return organisationService.viewOrganisationMembers(organisationId, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "organisation/viewKAHs/{organisationId}")
    Page<ProfileEntity> viewOrganisationKAHs(@PathVariable("organisationId") Long organisationId, Pageable pageable) {
        return organisationService.viewOrganisationKAHs(organisationId, pageable);
    }

    // placed in public so that webhook can work without bearer token
    @RequestMapping(method = RequestMethod.POST, value = "/webhook")
    public String stripeWebhookListener(@RequestBody String json, HttpServletRequest request)throws DonationOptionNotFoundException{
        return stripeService.handleWebhookEvent(json, request);
    }
}
