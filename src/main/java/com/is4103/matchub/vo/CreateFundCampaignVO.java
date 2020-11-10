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
public class CreateFundCampaignVO {
      
    @NotNull(message = "Campaign target can not be null")
    private BigDecimal campaignTarget;

   @NotNull(message = "Fund Campaign title can not be null")
    private String campaignTitle;
  
    @NotNull(message =  "End date can not be null")
    private LocalDateTime endDate;

    @NotNull(message = "Fund Campaign description can not be null")
    private String campaignDescription;

    @NotNull(message = "project id can not be null")
    private Long projectId;

    @NotNull(message = "Payee Id can not be null")
    private Long payeeId;
    
    public void createFundCampaign(FundCampaignEntity fundCampaignEntity){
        fundCampaignEntity.setCampaignTitle(campaignTitle);
        fundCampaignEntity.setCampaignTarget(campaignTarget);
        fundCampaignEntity.setEndDate(endDate);
        fundCampaignEntity.setCampaignDescription(campaignDescription);
        fundCampaignEntity.setProjectId(projectId);
        fundCampaignEntity.setPayeeId(payeeId);
        
                
    }
}
