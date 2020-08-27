/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

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
    private Set<String> keywords = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> uploadedFiles = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    private List<ProjectEntity> projects = new ArrayList<>();

    public ResourceEntity(String resourceName, String resourceDescription) {
        this.resourceName = resourceName;
        this.resourceDescription = resourceDescription;
    }

    

}
