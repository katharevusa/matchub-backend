/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class ResourceCategoryEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceCategoryId;

    @Column(nullable = false)
    @NotNull
    private String resourceCategoryName;

    @Column(nullable = false)
    @NotNull
    private String resourceCategoryDescription;

    @OneToMany
    private List<ResourceEntity> resources = new ArrayList<>();
    
    @NotNull
    private Integer communityPointsGuideline;
    
    @NotNull
    private Integer perUnit;
    
    private String unitName;

    public ResourceCategoryEntity(String resourceCategoryName, String resourceCategoryDescription, Integer communityPointsGuideline, Integer perUnit, String unitName) {
        this.resourceCategoryName = resourceCategoryName;
        this.resourceCategoryDescription = resourceCategoryDescription;
        this.communityPointsGuideline = communityPointsGuideline;
        this.perUnit = perUnit;
        this.unitName = unitName;
    }
    

   

}
