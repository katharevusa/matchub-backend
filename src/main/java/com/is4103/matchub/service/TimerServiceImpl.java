/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.BadgeEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author ngjin
 */
@Service
public class TimerServiceImpl implements TimerService {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private BadgeEntityRepository badgeEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    //leaderboard will be refreshed everyday at 12pm 
//    @Scheduled(cron = "0 0 12 * * ?")
//    @Scheduled(fixedRate = 2000, initialDelay = 15000)
    @Override
    public void refreshLeaderboardBadges() {
        System.out.println("Executed Here: ********************");
//
//        Long accountId = Long.valueOf(5);
//        ProfileEntity profile = profileEntityRepository.findById(accountId)
//                .orElseThrow(() -> new UserNotFoundException(accountId));
//
//        BadgeEntity top10Badge = badgeEntityRepository.findByBadgeTitle("TOP 10 IN LEADERBOARD");
//        top10Badge.getProfiles().add(profile);
//        profile.getBadges().add(top10Badge);

        //find the top 10 & remove/issue badge 
        badgeService.leaderboardTop10();
        //find the top 50 & remove/issue badge
        badgeService.leaderboardTop50();
        //find the top 100 & remove/issue badge
        badgeService.leaderboardTop100();
    }

    //sysdmin badge to be issues
    @Scheduled(cron = "0 0 12 * * ?")
    public void issueSysadminBadge() {

    }
}
