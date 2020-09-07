package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.IndividualUpdateVO;
import com.is4103.matchub.vo.OrganisationUpdateVO;
import com.is4103.matchub.vo.UserVO;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
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

    @RequestMapping(method = RequestMethod.GET, value = "/getAllFollowingAccounts/{id}")
    List<AccountEntity> getAllFollowingAccounts(@PathVariable Long id) {
        return userService.getAllFollowingAccounts(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllFollowerAccounts/{id}")
    List<AccountEntity> getAllFollowerAccounts(@PathVariable Long id) {
        return userService.getAllFollowingAccounts(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteAccount/{id}")
    void deleteAccount(@PathVariable Long id) {
        userService.delete(id);
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
