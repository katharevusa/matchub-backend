/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.DonationOptionEntity;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class CreateDonationOptionVO {
    
    
    private BigDecimal amount;


    @NotNull
    private String optionDescription;
    
    @NotNull
    private Long fundCampaignId;
    
    public void createDonationOption(DonationOptionEntity donationOptionEntity){
        if(amount != null){
            donationOptionEntity.setAmount(amount);
        }
        donationOptionEntity.setOptionDescription(optionDescription);
    }
    
}
