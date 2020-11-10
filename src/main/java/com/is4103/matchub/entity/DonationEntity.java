/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
 * @author longluqian
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donationOptionId;

    @Column(nullable = false)
    @NotNull
    private BigDecimal donatedAmount;

    
    @JsonIgnoreProperties({"donations","posts","announcements","hostedResources","savedResources","sdgs","projectsJoined","projectsOwned","projectsFollowing","joinRequests", "reviewsReceived","badges","tasks","managedChannel","joinedChannel"})
    @NotNull
    @ManyToOne
    private ProfileEntity donator;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime donationTime; 
   
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties({"donations"})
    private DonationOptionEntity donationOption;
    

    

}
