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
    List<AccountEntity> getAllAccounts() {
        return userService.getAllAccounts();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllActiveAccounts")
    List<AccountEntity> getAllActiveAccounts() {
        return userService.getAllActiveAccounts();
    }

}
