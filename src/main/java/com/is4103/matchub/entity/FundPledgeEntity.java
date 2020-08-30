/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.FundStatusEnum;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class FundPledgeEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fundPledgeId;

    @Column(nullable = false)
    @NotNull
    private BigDecimal donatedAmount;

    @Column(nullable = false)
    @NotNull
    private String wellWishes;

    @Column(nullable = false)
    @NotNull
    private FundStatusEnum fundStatus;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ProfileEntity profile;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FundsCampaignEntity fundCampaign;

    public FundPledgeEntity(BigDecimal donatedAmount, String wellWishes, FundStatusEnum fundStatus) {
        this.donatedAmount = donatedAmount;
        this.wellWishes = wellWishes;
        this.fundStatus = fundStatus;
    }

}
