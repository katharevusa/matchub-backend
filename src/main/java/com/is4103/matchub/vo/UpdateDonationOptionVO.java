/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.DonationOptionEntity;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class UpdateDonationOptionVO {
    
    
    @NotNull
    private Long donationOptionId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String optionDescription;
    
    public void updateDonationOption(DonationOptionEntity donationOptionEntity){
        donationOptionEntity.setAmount(amount);
        donationOptionEntity.setOptionDescription(optionDescription);
        
        
        
    }
    
}
