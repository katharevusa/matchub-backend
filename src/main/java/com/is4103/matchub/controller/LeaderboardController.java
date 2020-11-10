/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.service.LeaderboardService;
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
@RequestMapping("/authenticated")
public class LeaderboardController {

    @Autowired
    LeaderboardService leaderboardService;

    @RequestMapping(method = RequestMethod.GET, value = "/individualLeaderboard")
    Page<IndividualEntity> individualLeaderboard(Pageable pageable) {
        return leaderboardService.individualLeaderboard(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/organisationalLeaderboard")
    Page<OrganisationEntity> organisationalLeaderboard(Pageable pageable) {
        return leaderboardService.organisationalLeaderboard(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rank/{accountId}")
    Integer rank(@PathVariable("accountId") Long accountId) {
        return leaderboardService.rank(accountId);
    }
}
