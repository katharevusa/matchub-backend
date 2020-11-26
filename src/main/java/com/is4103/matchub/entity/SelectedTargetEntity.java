/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ngjin
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectedTargetEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selectedTargetId;

    //***********ASSOCIATIONS************
    @OneToOne
    @JsonIgnoreProperties({"projects", "targets"})
    private SDGEntity sdg;

    @ManyToMany
    private List<SDGTargetEntity> sdgTargets = new ArrayList<>();

    @ManyToOne(optional = true)
    @JsonIgnore
    private ProfileEntity profile;

    @ManyToOne(optional = true)
    @JsonIgnore
    private ProjectEntity project;

    public SelectedTargetEntity(SDGEntity sdg, ProfileEntity profile, ProjectEntity project) {
        this.sdg = sdg;
        this.profile = profile;
        this.project = project;
    }

}
