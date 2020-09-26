/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.is4103.matchub.enumeration.BadgeTypeEnum;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
    @JsonIgnoreProperties({"posts", "hostedResources", "sdgs", "projectsJoined", "projectsOwned", "reviewsReceived", "badges", "likedPosts"})
    private List<ProfileEntity> profiles = new ArrayList<>();

    @OneToOne(optional = true)
    @JsonIgnoreProperties({"projectBadge", "sdgs", "teamMembers"})
    private ProjectEntity project;

    public BadgeEntity(BadgeTypeEnum badgeType, String badgeTitle, String icon) {
        this.badgeType = badgeType;
        this.badgeTitle = badgeTitle;
        this.icon = icon;
    }

}
