/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.helper.StatisticsWrapper;
import com.is4103.matchub.repository.DonationEntityRepository;
import com.is4103.matchub.repository.FundCampaignEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ResourceTransactionEntityRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public StatisticsWrapper getLastFiveUserNumberData() {

        String[] months = new String[6];
        Object[] values = new Object[6];

        for (int i = 0; i < 6; i++) {
            LocalDateTime startMonth = YearMonth.now().atDay(1).minus(i, ChronoUnit.MONTHS).atStartOfDay();
            LocalDateTime startOfNextMonth = startMonth.plus(1, ChronoUnit.MONTHS);

            // make into capital case
            months[5 - i] = startMonth.getMonth().toString().substring(0, 1) + startMonth.getMonth().toString().substring(1).toLowerCase();
            values[5 - i] = profileEntityRepository.findUsersByJoinDate(startMonth, startOfNextMonth).size();
        }

        return new StatisticsWrapper(months, values);
    }

    @Override
    public StatisticsWrapper getLastFiveTransactionNumberData() {

        String[] months = new String[6];
        Object[] values = new Object[6];

        for (int i = 0; i < 6; i++) {
            LocalDateTime startMonth = YearMonth.now().atDay(1).minus(i, ChronoUnit.MONTHS).atStartOfDay();
            LocalDateTime startOfNextMonth = startMonth.plus(1, ChronoUnit.MONTHS);

            // make into capital case
            months[5 - i] = startMonth.getMonth().toString().substring(0, 1) + startMonth.getMonth().toString().substring(1).toLowerCase();

            // getting resource transaction total sum
            BigDecimal amount = resourceTransactionEntityRepository.findResourceTransactionEntityByTransactionTime(startMonth, startOfNextMonth)
                    .stream()
                    .map(x -> x.getAmountPaid())
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    // convert to thousands so chart scale won't be too big
                    .divide(BigDecimal.valueOf(1000), 2, RoundingMode.CEILING);

            // getting donations total sum
            amount = donationEntityRepository.findDonationsByTransactionTime(startMonth, startOfNextMonth)
                    .stream()
                    .map(x -> x.getDonatedAmount())
                    .reduce(amount, BigDecimal::add)
                    // convert to thousands so chart scale won't be too big
                    .divide(BigDecimal.valueOf(1000), 2, RoundingMode.CEILING);

            values[5 - i] = amount;
        }

        return new StatisticsWrapper(months, values);
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

    @Override
    public List<ProfileEntity> updatePlatformAdmins(List<Long> newAdminNumber) {
        // remove all users' admin right
        List<ProfileEntity> oldAdmins = profileEntityRepository.findAdminUsers();
        List<ProfileEntity> newAdmins = new ArrayList<>();
        for(ProfileEntity p: oldAdmins){
            p.getRoles().remove(AccountEntity.ROLE_SYSADMIN);
        }
        
        // add new admin right 
        for(Long id : newAdminNumber){
            ProfileEntity user = profileEntityRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id));
            user.getRoles().add(AccountEntity.ROLE_SYSADMIN);
            newAdmins.add(user);
        }
        
        profileEntityRepository.saveAll(newAdmins);
        return profileEntityRepository.findAdminUsers();
    }
    
     @Override
    public List<ProfileEntity> getCurrentPlatformAdmin() {
        return profileEntityRepository.findAdminUsers();
    }

}
