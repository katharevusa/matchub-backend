/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> uploadedFiles = new HashSet<>();
    
    @NotNull
    private boolean available;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime startTime;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime endTime;
    
    @OneToMany(mappedBy = "resource")
    private List<ResourceRequestEntity> listOfRequests;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ResourceCategoryEntity resourceCategory;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ProfileEntity resourceOwner;
    
    

    public ResourceEntity(String resourceName, String resourceDescription, boolean available, LocalDateTime startTime, LocalDateTime endTime, ResourceCategoryEntity resourceCategory, ProfileEntity ResourceOwner) {
        this.resourceName = resourceName;
        this.resourceDescription = resourceDescription;
        this.available = available;
        this.startTime = startTime;
        this.endTime = endTime;
        this.resourceCategory = resourceCategory;
        this.resourceOwner = ResourceOwner;
    }
    

  

    

}
