/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.is4103.matchub.enumeration.BadgeTypeEnum;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 *
 * @author ngjin
 */
@Entity
//@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "project")
public class BadgeEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long badgeId;

    @Column(nullable = false)
    @NotNull
    private BadgeTypeEnum badgeType;

    @Column(nullable = false)
    @NotNull
    private String badgeTitle;

    @Column(nullable = false)
    @NotNull
    private String icon;

    @ManyToMany(fetch = FetchType.LAZY)
//    @JsonIgnsore
//    @JsonIdentityInfo(generator = what thing)
    @JsonIgnoreProperties({"posts", "hostedResources", "sdgs", "projectsJoined", "projectsOwned", "reviewsReceived", "badges", "likedPosts"})
    private List<ProfileEntity> profiles = new ArrayList<>();

    @OneToOne(optional = true, fetch = FetchType.LAZY)
//    @JsonIgnore
    @JsonIgnoreProperties({"joinRequests", "reviews", "projectBadge", "fundsCampaign", "listOfRequests", "sdgs", "kpis", "teamMembers", "channels", "projectOwners"})
    private ProjectEntity project;

    public BadgeEntity(BadgeTypeEnum badgeType, String badgeTitle, String icon) {
        this.badgeType = badgeType;
        this.badgeTitle = badgeTitle;
        this.icon = icon;
    }

    public Long getBadgeId() {
        return badgeId;
    }

    public BadgeTypeEnum getBadgeType() {
        return badgeType;
    }

    public String getBadgeTitle() {
        return badgeTitle;
    }

    public String getIcon() {
        return icon;
    }

    public List<ProfileEntity> getProfiles() {
        return profiles;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
    }

    public void setBadgeType(BadgeTypeEnum badgeType) {
        this.badgeType = badgeType;
    }

    public void setBadgeTitle(String badgeTitle) {
        this.badgeTitle = badgeTitle;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setProfiles(List<ProfileEntity> profiles) {
        this.profiles = profiles;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }
    
    
}
