/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

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

    public Map<String, Integer> getLastFiveUserNumberData();
}
