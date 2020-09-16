/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.EmailService;
import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.IndividualSetupVO;
import com.is4103.matchub.vo.OrganisationCreateVO;
import com.is4103.matchub.vo.OrganisationSetupVO;
import com.is4103.matchub.vo.ChangePasswordVO;
import com.is4103.matchub.vo.UserVO;
import java.io.IOException;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

        System.out.println("uploaded file successfully: relative pathImage is " + filePath);
        return userService.setProfilePic(uuid, filePath);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setupOrganisationProfile/uploadProfilePic/{uuid}")
    public AccountEntity uploadOrganisationFile(@RequestParam(value = "file") MultipartFile file, @PathVariable("uuid") UUID uuid) {
//        return attachmentService.upload(file, directory);
        String filePath = attachmentService.upload(file);

        System.out.println("uploaded file successfully: relative pathImage is " + filePath);
        return userService.setProfilePic(uuid, filePath);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/setupOrganisationProfile/uploadDocument/{uuid}")
    public AccountEntity uploadOrganisationDocument(@RequestParam(value = "file") MultipartFile file, @PathVariable("uuid") UUID uuid) {
//        return attachmentService.upload(file, directory);
        String filePath = attachmentService.upload(file);

        System.out.println("uploaded verification doc successfully: relative path is " + filePath);
        return userService.setOrganisationVerificationDoc(uuid, filePath);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/forgotPassword")
    public void sendResetPasswordEmail(@RequestParam(value = "email") String email) throws MessagingException, IOException {
        userService.triggerResetPasswordEmail(email);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/resetPassword/{uuid}")
    public void resetPassword(@PathVariable("uuid") UUID uuid, @Valid @RequestBody ChangePasswordVO vo) {
        userService.changePassword(uuid, vo);
    }

}
