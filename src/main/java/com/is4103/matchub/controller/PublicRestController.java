/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.OrganisationCreateVO;
import com.is4103.matchub.vo.UserVO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(method = RequestMethod.POST, value = "/createNewIndividual")
    UserVO createNewIndividual(@Valid @RequestBody IndividualCreateVO createVO) {
        return userService.createIndividual(createVO);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/createNewOrganisation")
    UserVO createNewOrgansation(@Valid @RequestBody OrganisationCreateVO createVO) {
        return userService.createOrganisation(createVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/uploadFile")
    public String uploadFile(@RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "directory", required = false) String directory) {
        return attachmentService.upload(file, directory);
    }
}
