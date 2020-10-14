/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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

    @Column(nullable = false, length = 1000)
    @NotNull
    private String projectDescription;

    @Column(nullable = false)
    @NotNull
    private String country;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> userFollowers = new HashSet<>();

    @Column(nullable = false)
    @NotNull
    private ProjectStatusEnum projStatus = ProjectStatusEnum.ON_HOLD;

    @Column(nullable = false)
    @NotNull
    private Integer upvotes = 0;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> relatedResources = new ArrayList<>();

    @Column(nullable = false)
    @NotNull
    private Long projCreatorId;

    @Column(nullable = false)
    @NotNull
    private Boolean spotlight = false;

    @Column(nullable = true, columnDefinition = "TIMESTAMP")
    @Nullable
    private LocalDateTime spotlightEndTime;

    private String projectProfilePic;

    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn()
    private List<String> photos = new ArrayList<>();

    //Key: filename, Value = docPath
    @ElementCollection
    private Map<String, String> documents = new HashMap<>();

    //*********************Associations Below******************
    @OneToMany
    private List<JoinRequestEntity> joinRequests = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ReviewEntity> reviews = new ArrayList<>();

    @JsonIgnoreProperties({"profiles", "project"})
//    @OneToOne(optional = true, fetch = FetchType.LAZY)//need to change to false later cos every project needs to have one badge
    @OneToOne(optional = true)//need to change to false later cos every project needs to have one badge
    private BadgeEntity projectBadge;

    @OneToMany
    private List<FundsCampaignEntity> fundsCampaign = new ArrayList<>();

    @OneToMany
    private List<ScheduleEntity> meetings = new ArrayList<>();

    @OneToMany
    private List<ResourceRequestEntity> listOfRequests = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"projects"})
    private List<SDGEntity> sdgs = new ArrayList<>();

    @OneToMany
    private List<KPIEntity> kpis = new ArrayList<>();

    @ManyToMany
    @JsonIgnoreProperties({"hostedResources", "sdgs", "meetings", "projectsJoined", "projectsOwned", "joinRequests", "reviewsReceived", "badges", "fundPladges", "tasks", "managedChannel", "joinedChannel", "likedPosts"})
    private List<ProfileEntity> teamMembers = new ArrayList<>();
    
    @ManyToMany
    @JsonIgnoreProperties({"projectsFollowing","hostedResources", "sdgs", "meetings", "projectsJoined", "projectsOwned", "joinRequests", "reviewsReceived", "badges", "fundPladges", "tasks", "managedChannel", "joinedChannel", "likedPosts"})
    private List<ProfileEntity> projectFollowers = new ArrayList<>();
    

    @OneToMany(mappedBy = "project")
    private List<ChannelEntity> channels = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"projectsOwned", "sdgs", "meetings", "projectsJoined", "joinRequests", "reviewsReceived", "badges", "fundPladges", "tasks", "managedChannel", "joinedChannel", "likedPosts"})
    private List<ProfileEntity> projectOwners = new ArrayList<>();

    public ProjectEntity(String projectTitle, String projectDescription, String country, LocalDateTime startDate, LocalDateTime endDate) {
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;

    }

}
