/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ClaimRequestEntity;
import com.is4103.matchub.service.UserService;
import com.is4103.matchub.vo.ClaimRequestVO;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
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
 * @author tjle2
 */
@RestController
@RequestMapping("/authenticated")
public class ClaimRequestController {

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.POST, value = "/createClaimRequest")
    public ClaimRequestEntity createClaimRequest(@Valid @RequestBody ClaimRequestVO claimRequestVO) {
        return userService.createClaimRequest(claimRequestVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateClaimRequest/uploadPhotos")
    public ClaimRequestEntity uploadPhotos(@RequestParam(value = "photos") MultipartFile[] photos, @RequestParam("claimRequestId") Long claimRequestId) {
        return userService.uploadPhotos(claimRequestId, photos);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateClaimRequest/uploadDocuments")
    public ClaimRequestEntity uploadDocuments(@RequestParam(value = "documents") MultipartFile[] documents, @RequestParam("claimRequestId") Long claimRequestId) {
        return userService.uploadDocuments(claimRequestId, documents);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllClaimRequests")
    public List<ClaimRequestEntity> getAllClaimRequests() {
        return userService.getAllClaimRequests();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/approveClaimRequest")
    public void approveClaimRequest(@RequestParam(name = "claimRequestId", required = true) Long claimRequestId) throws IOException, MessagingException {
        userService.approveClaimRequest(claimRequestId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/rejectClaimRequest")
    public void rejectClaimRequest(@RequestParam(name = "claimRequestId", required = true) Long claimRequestId) throws IOException, MessagingException {
        userService.rejectClaimRequest(claimRequestId);
    }
}
