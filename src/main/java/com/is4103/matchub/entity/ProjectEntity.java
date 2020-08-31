/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.ProjectStatusEnum;
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
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 *
 * @author ngjin
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false)
    @NotNull
    private String projectTitle;

    @Column(nullable = false)
    @NotNull
    private String projectDescription;

    @Column(nullable = false)
    @NotNull
    private String country;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime startDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime endDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> userFollowers = new HashSet<>();

    @Column(nullable = false)
    @NotNull
    private ProjectStatusEnum projStatus;

    @Column(nullable = false)
    @NotNull
    private Integer upvotes = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> photos = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> relatedResources = new ArrayList<>();

    @Column(nullable = false)
    @NotNull
    private Long projCreatorId;


    @OneToMany
    private List<JoinRequestEntity> joinRequests = new ArrayList<>();
    @OneToMany(mappedBy = "project")
    private List<ReviewEntity> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<BadgeEntity> badges = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<FundsCampaignEntity> fundsCampaign = new ArrayList<>();

    @OneToMany
    private List<ScheduleEntity> meetings = new ArrayList<>();

   
    @OneToMany
    private List<ResourceRequestEntity> listOfRequests = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    private List<SDGEntity> sdgs = new ArrayList<>();

    @OneToMany
    private List<KPIEntity> kpis = new ArrayList<>();

    @ManyToMany
    private List<ProfileEntity> projAdmins = new ArrayList<>();

    @ManyToMany
    private List<ProfileEntity> teamMembers = new ArrayList<>();
    
    @OneToMany(mappedBy = "project")
    private List<ChannelEntity> channels = new ArrayList<>();
    
    @ManyToOne
    private ProfileEntity projectOwner;

    public ProjectEntity(String projectTitle, String projectDescription, String country, LocalDateTime startDate, LocalDateTime endDate, ProjectStatusEnum projStatus) {
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projStatus = projStatus;
    }

}
