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

    @Column(nullable = false)
    @NotNull
    private Long reviewerId;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ProjectEntity project;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ProfileEntity reviewReceiver;

    public ReviewEntity(String content, BigDecimal rating) {
        this.content = content;
        this.rating = rating;
    }

}
