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
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.BadgeEntityRepository;
import com.is4103.matchub.repository.PostEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ReviewEntityRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    //leaderboard will be refreshed everyday at 12pm 
//    @Scheduled(cron = "0 0 12 * * ?")
//    @Scheduled(fixedRate = 2000, initialDelay = 15000)
    @Override
    public void refreshLeaderboardBadges() {
        System.out.println("Executed Here: Leaderboard Badge ********************");

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
        /*read code starts here*/
        //find the top 10 & remove/issue badge 
        badgeService.leaderboardTop10();
        //find the top 50 & remove/issue badge
        badgeService.leaderboardTop50();
        //find the top 100 & remove/issue badge
        badgeService.leaderboardTop100();
    }

    //sysdmin badge to be issuesd
//    @Scheduled(cron = "0 0 12 * * ?")
    @Override
    public void issueSysadminBadge() {

    }

    //method will be called monthly to check for activity of user and increment counter accordingly
    //runs 1st of every month, 12pm
//    @Scheduled(cron = "0 0 12 1 * ?")
//    @Scheduled(fixedRate = 2000, initialDelay = 15000)
    @Override
    public void longServiceBadges() {
        System.out.println("Executed Here: Long Service Badges ********************");

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

    //helper method for longservicebadge method
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

    //will check for completed projects everyday 12pm and issue badge 
//    @Scheduled(cron = "0 0 12 * * ?")
//    @Scheduled(fixedRate = 2000, initialDelay = 15000)
    @Override
    public void trackDailyCompletedProjects() {
        System.out.println("Executed Here: Track Daily Completed Project ********************");

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -1);
        LocalDateTime yesterday = LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault());

        List<ProjectEntity> completedProjs = projectEntityRepository.getCompletedProjectsByEndDate(yesterday);

        for (ProjectEntity p : completedProjs) {
            badgeService.issueProjectBadge(p);
        }
    }

    @Override
    public String longServiceAwardDemo(Long accountId, Integer noOfYears) {

        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        //manually set his joinDate 
        LocalDateTime newJoinDate = profile.getJoinDate().minusYears(noOfYears);
        profile.setJoinDate(newJoinDate);

        BadgeEntity badge;

        //query for the correct long service badge
        if (noOfYears >= 5) {
            badge = badgeEntityRepository.findByBadgeTitle("5 YEARS WITH MATCHUB");
        } else if (noOfYears >= 2) {
            badge = badgeEntityRepository.findByBadgeTitle("2 YEARS WITH MATCHUB");
        } else if (noOfYears == 1) {
            badge = badgeEntityRepository.findByBadgeTitle("1 YEAR WITH MATCHUB");
        } else {
            return "Please enter a positive integer for noOfYears!";
        }

        badge.getProfiles().add(profile);
        badgeEntityRepository.save(badge);

        profile.getBadges().add(badge);
        profileEntityRepository.save(profile);
        
        return "AccountId " + accountId + " is successfully awarded the longservicebadge.";
    }
}
