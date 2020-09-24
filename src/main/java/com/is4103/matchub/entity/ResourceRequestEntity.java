/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.RequestorEnum;
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
    private LocalDateTime requestCreationTime = LocalDateTime.now();
    
    @Column(nullable = false)
    @NotNull
    private RequestStatusEnum status = RequestStatusEnum.ON_HOLD;
    
    @Column(nullable = false)
    @NotNull
    private Long requestorId;
    
    @Column(nullable = false)
    @NotNull
    private RequestorEnum requestorEnum;
    
    @Column(nullable = false)
    @NotNull
    private Long resourceId;
     
    @Column(nullable = false)
    @NotNull
    private Long projectId;
    
    @Column(nullable = false)
    @NotNull
    private Integer unitsRequired;
    
    @Column(nullable = true)
    private String message;

    public ResourceRequestEntity(Long requestorId, Long resourceId, Long projectId, Integer unitsRequired, String message) {
        this.requestorId = requestorId;
        this.resourceId = resourceId;
        this.projectId = projectId;
        this.unitsRequired = unitsRequired;
        this.message = message;
    }
    

   
     
     
       

    
    
    
    
    
    
    
    

    
    
    
}
