/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.helper.StatisticsWrapper;
import com.is4103.matchub.service.SystemAdminService;
import com.is4103.matchub.service.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    SystemAdminService systemAdminService;

    @RequestMapping(method = RequestMethod.GET, value = "/getAllAccounts")
    Page<AccountEntity> getAllAccounts(Pageable pageable) {
        return userService.getAllAccounts(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllActiveAccounts")
    Page<AccountEntity> getAllActiveAccounts(Pageable pageable) {
        return userService.getAllActiveAccounts(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getTotalNumberOfUser")
    public int getTotalNumberOfUser() {
        return systemAdminService.getTotalNumberOfUser();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getTotalNumberOfResource")
    public int getTotalNumberOfResource() {
        return systemAdminService.getTotalNumberOfResource();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getTotalNumberOfProject")
    public int getTotalNumberOfProject() {
        return systemAdminService.getTotalNumberOfProject();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getTotalNumberOfFundCampaign")
    public int getTotalNumberOfFundCampaign() {
        return systemAdminService.getTotalNumberOfFundCampaign();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getLastFiveUserNumberData")
    public StatisticsWrapper getLastFiveUserNumberData() {
        return systemAdminService.getLastFiveUserNumberData();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getLastFiveTransactionNumberData")
    public StatisticsWrapper getLastFiveTransactionNumberData() {
        return systemAdminService.getLastFiveTransactionNumberData();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getProjectsNumberWithDifferentStatus")
    public Map<String, Integer> getProjectsNumberWithDifferentStatus() {
        return systemAdminService.getProjectsNumberWithDifferentStatus();
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "/updatePlatformAdmins")
    public List<ProfileEntity> updatePlatformAdmins(@RequestParam(value = "newAdminNumber", required = true) List<Long> newAdminNumber){
        return systemAdminService.updatePlatformAdmins(newAdminNumber);
    }
}
