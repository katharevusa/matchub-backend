/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.CommentTypeEnum;
import java.time.LocalDateTime;
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
 * @author longluqian
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    @NotNull
    private String content;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timeCreated;

    @Column(nullable = false)
    @NotNull
    private Long accountId;
    
    @Column(nullable = false)
    @NotNull
    private CommentTypeEnum commentType;

    public CommentEntity(String content, LocalDateTime timeCreated, Long accountId) {
        this.content = content;
        this.timeCreated = timeCreated;
        this.accountId = accountId;
    }

}
