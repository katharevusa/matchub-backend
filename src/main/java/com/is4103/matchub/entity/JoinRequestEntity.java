/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.is4103.matchub.enumeration.JoinRequestStatusEnum;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class JoinRequestEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long joinRequestId;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime requestCreationTime = LocalDateTime.now();
    
    @Column(nullable = false)
    @NotNull
    private JoinRequestStatusEnum status = JoinRequestStatusEnum.ON_HOLD;
    
    @ManyToOne
    @NotNull
    @JsonIgnoreProperties({"likedPosts","joinedChannel", "managedChannel", "tasks", "fundPladges", "badges","reviewsReceived", "joinRequests","projectsOwned","projectsJoined", "meetings", "sdgs", "hostedResources", "notifications"  })
    private ProfileEntity requestor;
    
    @ManyToOne
    @NotNull
    @JsonIgnoreProperties({"joinRequests", "reviews","projectBadge","fundsCampaign", "meetings","listOfRequests",  "sdgs", "kpis", "teamMembers", "channels" ,"projectOwners" })
    private ProjectEntity project;

    public JoinRequestEntity(LocalDateTime requestCreationTime, JoinRequestStatusEnum status) {
        this.requestCreationTime = requestCreationTime;
        this.status = status;
    }
    
    
    
    
    
    
}
