/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.service.UserService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/public/share/")
public class PublicSharingController {

    /* this controller is for sharing PROFILE, PROJECT, RESOURCES */
 /* PUBLIC ACCESS */
    @Autowired
    private UserService userService;

    /* these methods are for sharing profile */
    @RequestMapping(method = RequestMethod.GET, value = "/getAccount/{uuid}")
    AccountEntity getAccount(@PathVariable UUID uuid) {
        return userService.getAccount(uuid);
    }
//    
//    @RequestMapping(method = RequestMethod.GET, value = "/getAccount/{email}")
//    AccountEntity getAccount(@PathVariable String email) {
//        return userService.getAccount(email);
//    }
    //    @RequestMapping(method = RequestMethod.GET, value = "/getAccount/{id}")
//    AccountEntity getAccount(@PathVariable Long id) {
//        return userService.getAccount(id);
//    }
//    

    /* END OF sharing profile */
}
