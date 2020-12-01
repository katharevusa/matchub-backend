/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.helper.StatisticsWrapper;
import java.util.List;
import java.util.Map;

/**
 *
 * @author longluqian
 */
public interface SystemAdminService {

    public int getTotalNumberOfUser();

    public int getTotalNumberOfResource();

    public int getTotalNumberOfProject();

    public int getTotalNumberOfFundCampaign();

    public StatisticsWrapper getLastFiveUserNumberData();

    public StatisticsWrapper getLastFiveTransactionNumberData();

    public Map<String, Integer> getProjectsNumberWithDifferentStatus();
    
    public List<ProfileEntity> updatePlatformAdmins(List<Long> newAdminNumber);
    
    public List<ProfileEntity> getCurrentPlatformAdmin();
}
