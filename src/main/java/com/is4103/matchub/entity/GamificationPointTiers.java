/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class GamificationPointTiers {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gamificationPointTiersId;

    @Column(nullable = false)
    @NotNull
    @Builder.Default
    private Integer pointsToComment = 5;

    @Column(nullable = false)
    @NotNull
    @Builder.Default
    private Integer pointsToDownvote = 50;

    @Column(nullable = false)
    @NotNull
    @Builder.Default
    private Integer pointsToAnonymousReview = 100;

    @Column(nullable = false)
    @NotNull
    @Builder.Default
    private Integer pointsToSpotlight = 200;

}
