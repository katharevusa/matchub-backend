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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ngjin
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SDGTargetEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sdgTargetId;

    @Column(nullable = false, unique = true)
    @NotNull
    private String sdgTargetNumbering;

    @Column(nullable = false, length = 2000)
    @NotNull
    private String sdgTargetDescription;

    public SDGTargetEntity(String sdgTargetNumbering, String sdgTargetDescription) {
        this.sdgTargetNumbering = sdgTargetNumbering;
        this.sdgTargetDescription = sdgTargetDescription;
    }

}
