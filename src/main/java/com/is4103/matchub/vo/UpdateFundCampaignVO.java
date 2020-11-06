/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.FundCampaignEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class UpdateFundCampaignVO {

    @NotNull
    public Long campaignId;
    
    @NotNull
    public String campaignTitle;
    
    @NotNull
    public String campaignDescription;

    @NotNull
    public LocalDateTime endDate;
    
    @NotNull
    public BigDecimal targetAmount;
    
    public void updateFundCampaign(FundCampaignEntity fundCampaignEntity){
        fundCampaignEntity.setCampaignDescription(campaignDescription);
        fundCampaignEntity.setCampaignTitle(campaignTitle);
        fundCampaignEntity.setEndDate(endDate);
        fundCampaignEntity.setCampaignTarget(targetAmount);
    }
    
}
