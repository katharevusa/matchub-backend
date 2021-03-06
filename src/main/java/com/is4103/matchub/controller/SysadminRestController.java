/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/sysadministrator")
public class SysadminRestController {

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/getAllAccounts")
    Page<AccountEntity> getAllAccounts(Pageable pageable) {
        return userService.getAllAccounts(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllActiveAccounts")
    Page<AccountEntity> getAllActiveAccounts(Pageable pageable) {
        return userService.getAllActiveAccounts(pageable);
    }

    //Resource Category Management
}
