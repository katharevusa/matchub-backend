/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
public class AnnouncementEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long announcementId;
    
    @Column(nullable = false, length = 1000)
    @NotNull
    private String title;

    @Column(nullable = false)
    @NotNull
    private String content;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timestamp;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"announcements"})
    private List<ProfileEntity> notifiedUsers = new ArrayList<>();

    @Column(nullable = true)
    private Long projectId;

    @Column(nullable = true)
    private Long taskId;

    
    @Column(nullable = true)
    private Long postId;

    @Column(nullable = false)
    @NotNull
    private AnnouncementTypeEnum type;
    
    @ElementCollection
    private List<Long> viewedUserIds = new ArrayList<>();
   
    @Column(nullable = true)
    private Long creatorId;
    
    @Column(nullable = true)
    private Long resourceId;
    
    @Column(nullable = true)
    private Long resourceRequestId;
    
    @Column(nullable = true)
    private Long joinRequestId;
    
    @Column(nullable = true)
    private UUID newFollowerAndNewPosterUUID;
    
    @Column(nullable = true)
    private Long newFollowerAndNewPosterProfileId;
    
    @Column(nullable = true)
    private Long reviewId;

    public AnnouncementEntity(String title, String content, LocalDateTime timestamp, AnnouncementTypeEnum type) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
    }

}
