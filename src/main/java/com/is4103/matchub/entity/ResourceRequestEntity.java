/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.ApproverEnum;
import com.is4103.matchub.enumeration.RequestStatusEnum;
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
public class ResourceRequestEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime requestCreationTime;
    
    @Column(nullable = false)
    @NotNull
    private RequestStatusEnum status;
    
    @Column(nullable = false)
    @NotNull
    private ApproverEnum approver;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ResourceEntity resource;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ProjectEntity project;

    public ResourceRequestEntity(LocalDateTime requestCreationTime, RequestStatusEnum status, ApproverEnum approver, ResourceEntity Resource, ProjectEntity project) {
        this.requestCreationTime = requestCreationTime;
        this.status = status;
        this.approver = approver;
        this.resource = Resource;
        this.project = project;
    }
    
    
    
    
    
    
    

    
    
    
}
