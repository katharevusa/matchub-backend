/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.service.MatchingService;
import com.is4103.matchub.service.MatchingServiceImpl;
import com.is4103.matchub.service.TimerService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/public")
public class TimerController {

    @Autowired
    TimerService timerService;

    //manually trigger the long service award 
    @RequestMapping(method = RequestMethod.POST, value = "/executeLongServiceAwardDemo/{accountId}/{noOfYears}")
    public String executeLongServiceAwardDemo(@PathVariable("accountId") Long accountId, @PathVariable("noOfYears") Integer noOfYears) {
        return timerService.longServiceAwardDemo(accountId, noOfYears);
    }

}
