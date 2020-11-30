/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.repository.DonationEntityRepository;
import com.is4103.matchub.repository.FundCampaignEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ResourceTransactionEntityRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class SystemAdminServiceImpl implements SystemAdminService {

    @Autowired
    ProfileEntityRepository profileEntityRepository;

    @Autowired
    ResourceEntityRepository resourceEntityRepository;

    @Autowired
    ProjectEntityRepository projectEntityRepository;

    @Autowired
    FundCampaignEntityRepository fundCampaignEntityRepository;

    @Autowired
    ResourceTransactionEntityRepository resourceTransactionEntityRepository;

    @Autowired
    DonationEntityRepository donationEntityRepository;

    @Override
    public int getTotalNumberOfUser() {
        return profileEntityRepository.findAll().size();
    }

    @Override
    public int getTotalNumberOfResource() {
        return resourceEntityRepository.findAll().size();
    }

    @Override
    public int getTotalNumberOfProject() {
        return projectEntityRepository.findAll().size();
    }

    @Override
    public int getTotalNumberOfFundCampaign() {
        return fundCampaignEntityRepository.findAll().size();
    }

    @Override
    public Map<String, Integer> getLastFiveUserNumberData() {

        Map<String, Integer> map = new HashMap<>();
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("currentTime" + currentTime);
        LocalDateTime lastOneMonthTime = currentTime.minus(1, ChronoUnit.MONTHS);
        LocalDateTime lastTwoMonthTime = currentTime.minus(2, ChronoUnit.MONTHS);
        LocalDateTime lastThreeMonthTime = currentTime.minus(3, ChronoUnit.MONTHS);
        LocalDateTime lastFourMonthTime = currentTime.minus(4, ChronoUnit.MONTHS);
        LocalDateTime lastFiveMonthTime = currentTime.minus(5, ChronoUnit.MONTHS);

        int lastMonth = profileEntityRepository.findUsersByJoinDate(lastOneMonthTime, currentTime).size();
        int secondLastMonth = profileEntityRepository.findUsersByJoinDate(lastTwoMonthTime, lastOneMonthTime).size();
        int ThirdLastMonth = profileEntityRepository.findUsersByJoinDate(lastThreeMonthTime, lastTwoMonthTime).size();
        int FourthLastMonth = profileEntityRepository.findUsersByJoinDate(lastFourMonthTime, lastThreeMonthTime).size();
        int FifthLastMonth = profileEntityRepository.findUsersByJoinDate(lastFiveMonthTime, lastFourMonthTime).size();

    
        map.put("last month data", lastMonth);
        map.put("last second month data", secondLastMonth);
        map.put("last third month data", ThirdLastMonth);
        map.put("last fourth month data", FourthLastMonth);
        map.put("last fifth month data", FifthLastMonth);
        System.out.println(map.toString());

        return map;
    }

//Monthly transactions meaning last 5 month monthly total transactions is it

    @Override
    public Map<String, Integer> getLastFiveTransactionNumberData() {
        Map<String, Integer> map = new HashMap<>();
        LocalDateTime currentTime = LocalDateTime.now();
   
        LocalDateTime lastOneMonthTime = currentTime.minus(1, ChronoUnit.MONTHS);
        LocalDateTime lastTwoMonthTime = currentTime.minus(2, ChronoUnit.MONTHS);
        LocalDateTime lastThreeMonthTime = currentTime.minus(3, ChronoUnit.MONTHS);
        LocalDateTime lastFourMonthTime = currentTime.minus(4, ChronoUnit.MONTHS);
        LocalDateTime lastFiveMonthTime = currentTime.minus(5, ChronoUnit.MONTHS);

        int lastMonth = resourceTransactionEntityRepository.findResourceTransactionEntityByTransactionTime(lastOneMonthTime, currentTime).size()+
                donationEntityRepository.findDonationsByTransactionTime(lastOneMonthTime, currentTime).size();
        int secondLastMonth = resourceTransactionEntityRepository.findResourceTransactionEntityByTransactionTime(lastTwoMonthTime, lastOneMonthTime).size()+
                donationEntityRepository.findDonationsByTransactionTime(lastTwoMonthTime, lastOneMonthTime).size();
        int ThirdLastMonth = resourceTransactionEntityRepository.findResourceTransactionEntityByTransactionTime(lastThreeMonthTime, lastTwoMonthTime).size()+
                donationEntityRepository.findDonationsByTransactionTime(lastThreeMonthTime, lastTwoMonthTime).size();
        int FourthLastMonth = resourceTransactionEntityRepository.findResourceTransactionEntityByTransactionTime(lastFourMonthTime, lastThreeMonthTime).size()+
                donationEntityRepository.findDonationsByTransactionTime(lastFourMonthTime, lastThreeMonthTime).size();
        int FifthLastMonth = resourceTransactionEntityRepository.findResourceTransactionEntityByTransactionTime(lastFiveMonthTime, lastFourMonthTime).size()+
                donationEntityRepository.findDonationsByTransactionTime(lastFiveMonthTime, lastFourMonthTime).size();

        map.put("last month data", lastMonth);
        map.put("last second month data", secondLastMonth);
        map.put("last third month data", ThirdLastMonth);
        map.put("last fourth month data", FourthLastMonth);
        map.put("last fifth month data", FifthLastMonth);
        
        
        return map;
    }


    
   @Override
    public Map<String, Integer> getProjectsNumberWithDifferentStatus() {
        Map<String, Integer> map = new HashMap<>();
        map.put(String.valueOf(ProjectStatusEnum.ACTIVE), projectEntityRepository.getProjectsByStatus(ProjectStatusEnum.ACTIVE).size());
        map.put(String.valueOf(ProjectStatusEnum.COMPLETED), projectEntityRepository.getProjectsByStatus(ProjectStatusEnum.COMPLETED).size());
        map.put(String.valueOf(ProjectStatusEnum.ON_HOLD), projectEntityRepository.getProjectsByStatus(ProjectStatusEnum.ON_HOLD).size());
        map.put(String.valueOf(ProjectStatusEnum.TERMINATED), projectEntityRepository.getProjectsByStatus(ProjectStatusEnum.TERMINATED).size());

        return map;

    }

}
