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
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author longluqian
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceTransactionEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceTransactionId;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime transactionTime;

    @Column(nullable = false)
    @NotNull
    private BigDecimal amountPaid;

    @NotNull
    private Long payerId;

    @NotNull
    @OneToOne
    @JsonIgnoreProperties({"resourceTransaction", "listOfRequests"})
    private ResourceEntity resource;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties(value = {"listOfResourceTransactions", "joinRequests", "reviews", "projectBadge", "fundCampaigns", "listOfRequests", "sdgs", "teamMembers", "projectFollowers", "projectOwners", "selectedTargets", "competition"})
    private ProjectEntity project;

}
