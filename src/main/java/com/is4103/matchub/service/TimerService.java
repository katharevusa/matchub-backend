/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

/**
 *
 * @author ngjin
 */
public interface TimerService {

    void refreshLeaderboardBadges();

    void issueSysadminBadge();

    void longServiceBadges();

//    void trackDailyCompletedProjects();

    String longServiceAwardDemo(Long accountId, Integer noOfYears);
}