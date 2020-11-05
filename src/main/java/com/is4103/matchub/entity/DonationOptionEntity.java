/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ngjin
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationOptionEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donationOptionId;

    @Column(nullable = false)
    @NotNull
    private BigDecimal amount;

    @Column(nullable = false)
    @NotNull
    private String optionDescription;
    
    @OneToMany
    @JsonIgnoreProperties({"donationOption"})
    private List<DonationEntity> donations = new ArrayList<>();
   
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties({"donationOptions"})
    private FundCampaignEntity fundCampaign;
    

    

}
