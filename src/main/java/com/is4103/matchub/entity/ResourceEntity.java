/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
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
public class ResourceEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    @Column(nullable = false)
    @NotNull
    private String resourceName;

    @Column(nullable = false)
    @NotNull
    private String resourceDescription;

    @OrderColumn
    @Column(nullable = true)
    @ElementCollection(fetch = FetchType.LAZY, targetClass = String.class)
    private List<String> uploadedFiles = new ArrayList<>();
    
    @NotNull
    private boolean available = Boolean.TRUE;
    
    @Column(nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime startTime;
    
    @Column(nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime endTime;
    
    @OneToMany(mappedBy = "resource")
    private List<ResourceRequestEntity> listOfRequests;
    
    
    @Column(nullable = false)
    @NotNull
    private Long resourceCategoryId;
    
    @Column(nullable = false)
    @NotNull
    private Long resourceOwnerId;
    
    @NotNull
    @Column(nullable = false)
    private Integer units;
      
    private String resourceProfilePic;
    
    @OrderColumn
    @Column(nullable = true)
    @ElementCollection(fetch = FetchType.LAZY, targetClass = String.class)
    private List<String> photos = new ArrayList<>();
    
    //Key: filename, Value = docPath
    @ElementCollection
    private Map<String, String> documents = new HashMap<>();
    
    
    @Column(nullable = false)
    private Boolean spotlight = Boolean.FALSE;
    
    @Column(nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime spotlightEndTime;
    
    @Column(nullable = true)
    private Long matchedProjectId;
    

    public ResourceEntity(String resourceName, String resourceDescription, Long resourceCategoryId, Long resourceOwnerId, Integer units) {
        this.resourceName = resourceName;
        this.resourceDescription = resourceDescription;
        this.resourceCategoryId = resourceCategoryId;
        this.resourceOwnerId = resourceOwnerId;
        this.units = units;
    }
    
    public ResourceEntity(String resourceName, String resourceDescription, Integer units) {
        this.resourceName = resourceName;
        this.resourceDescription = resourceDescription;
        this.units = units;
    }

    public ResourceEntity(String resourceName, String resourceDescription, LocalDateTime startTime, LocalDateTime endTime, Integer units) {
        this.resourceName = resourceName;
        this.resourceDescription = resourceDescription;
        this.startTime = startTime;
        this.endTime = endTime;
        this.units = units;
    }
    
    
    

    

    
    

  

    

}
