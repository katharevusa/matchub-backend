/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.BadgeEntityRepository;
import com.is4103.matchub.repository.PostEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ReviewEntityRepository;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private PostEntityRepository postEntityRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Autowired
    private ReviewEntityRepository reviewEntityRepository;

    //leaderboard will be refreshed everyday at 12pm 
//    @Scheduled(cron = "0 0 12 * * ?")
    @Scheduled(fixedRate = 2000, initialDelay = 15000)
    @Override
    public void refreshLeaderboardBadges() {
        System.out.println("Executed Here: ********************");

//        test code
//        Long accountId = Long.valueOf(5);
//        ProfileEntity profile = profileEntityRepository.findById(accountId)
//                .orElseThrow(() -> new UserNotFoundException(accountId));
//
//        BadgeEntity top10Badge = badgeEntityRepository.findByBadgeTitle("TOP 10 IN LEADERBOARD");
//        top10Badge.getProfiles().add(profile);
//        profile.getBadges().add(top10Badge);
//        badgeEntityRepository.save(top10Badge);
//        profileEntityRepository.save(profile);
        //find the top 10 & remove/issue badge 
        badgeService.leaderboardTop10();
        //find the top 50 & remove/issue badge
        badgeService.leaderboardTop50();
        //find the top 100 & remove/issue badge
        badgeService.leaderboardTop100();
    }

    //sysdmin badge to be issuesd
    @Scheduled(cron = "0 0 12 * * ?")
    public void issueSysadminBadge() {

    }

    //method will be called monthly to check for activity of user and increment counter accordingly
    //runs 1st of every month, 12pm
    @Scheduled(cron = "0 0 12 1 * ?")
    public void longServiceBadges() {
        Boolean increment = false;
        List<ProfileEntity> profiles = profileEntityRepository.findAllActiveAccounts();

        for (ProfileEntity p : profiles) {
            Pageable pageable = PageRequest.of(0, 1);

            //check for post 
            List<PostEntity> posts = postEntityRepository.getPostsByAccountId(p.getAccountId(), pageable).toList();
            if (!increment && !posts.isEmpty() && isActiveLastMonth(posts.get(0).getTimeCreated()) == true) {
                increment = true;
            }

            //check for review
            List<ReviewEntity> reviews = reviewEntityRepository.getReviewsGivenByAccountId(p.getAccountId(), pageable).toList();
            if (!increment && !reviews.isEmpty() && isActiveLastMonth(reviews.get(0).getTimeCreated()) == true) {
                increment = true;
            }

            if (increment) {
                p.setActiveMonths(p.getActiveMonths() + 1);
                profileEntityRepository.save(p);

                if (p.getActiveMonths() == 12) {
                    // call another method to give the badge
                    badgeService.issueLongServiceAward1YearBadge(p);
                } else if (p.getActiveMonths() == 24) {
                    // call another method to give the badge
                    badgeService.issueLongServiceAward2YearsBadge(p);
                } else if (p.getActiveMonths() == 60) {
                    // call another method to give the badge
                    badgeService.issueLongServiceAward5YearsBadge(p);
                }
            } else {//not active already
                //reset back to 0 months 
                p.setActiveMonths(0);
                profileEntityRepository.save(p);
            }
        }

    }

    private Boolean isActiveLastMonth(LocalDateTime x) {

        LocalDateTime now = LocalDateTime.now();

        if (now.getYear() == x.getYear()) {
            Integer monthInInteger = x.getMonthValue();
            Integer currentMonthInInteger = now.getMonthValue();

            if (currentMonthInInteger - 1 == monthInInteger) {
                return true;
            }
        } else if (now.getYear() - 1 == x.getYear()) {
            Integer monthInInteger = x.getMonthValue();
            Integer currentMonthInInteger = now.getMonthValue();

            if (monthInInteger == 12 && currentMonthInInteger == 1) {
                return true;
            }
        }

        return false;

    }
}
