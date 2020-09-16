/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
public class SDGEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sdgId;

    @Column(nullable = false)
    @NotNull
    private String sdgName;

    @Column(nullable = false)
    @NotNull
    private String sdgDescription;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"sdgs", "projectOwners", "joinRequests", "reviews", "projectBadge", "fundsCampaign", "meetings", "listOfRequests", "kpis", "teamMembers", "channels"})
    private List<ProjectEntity> projects = new ArrayList<>();

    public SDGEntity(String sdgName, String sdgDescription) {
        this.sdgName = sdgName;
        this.sdgDescription = sdgDescription;
    }

}
