/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.NotificationTypeEnum;
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
 * @author ngjin
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    
    @Column(nullable = false)
    @NotNull
    private String title;

    @Column(nullable = false)
    @NotNull
    private String content;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @NotNull
    private Long notifiedUserId;

    @Column(nullable = true)
    private Long projectId;

    @Column(nullable = true)
    private Long taskId;

    @Column(nullable = true)
    private Long postId;

    @Column(nullable = false)
    @NotNull
    private NotificationTypeEnum type;

    public NotificationEntity(String content, LocalDateTime timestamp, Long projectId) {
        this.content = content;
        this.timestamp = timestamp;
        this.projectId = projectId;
    }

}
