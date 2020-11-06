/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class FundCampaignEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fundsCampaignId;

    @Column(nullable = false)
    @NotNull
    private String campaignTitle;
    
    @Column(nullable = false)
    @NotNull
    private BigDecimal campaignTarget;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime endDate;

    @Column(nullable = false)
    @NotNull
    private String campaignDescription;

    @Column(nullable = false)
    @NotNull
    private BigDecimal currentAmountRaised = BigDecimal.ZERO;

    @OneToMany(mappedBy = "fundCampaign")
    @JsonIgnoreProperties({"fundCampaign"})
    private List<DonationOptionEntity> donationOptions = new ArrayList<>();

    @Column(nullable = false)
    @NotNull
    private Long projectId;

    @Column(nullable = false)
    @NotNull
    private String stripeAccountUid;
    
    @Column(nullable = false)
    @NotNull
    private boolean activated = Boolean.FALSE;
    
    @Column(nullable = false)
    @NotNull
    private Long payeeId;
    
   

}
