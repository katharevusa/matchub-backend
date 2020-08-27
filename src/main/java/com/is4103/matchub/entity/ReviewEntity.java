/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

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
public class ReviewEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    @NotNull
    private String content;

    @Column(nullable = false)
    @NotNull
    private BigDecimal rating;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ProjectEntity project;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ProfileEntity reviewer;

    public ReviewEntity(String content, BigDecimal rating, ProjectEntity project, ProfileEntity reviewer) {
        this.content = content;
        this.rating = rating;
        this.project = project;
        this.reviewer = reviewer;
    }

}
