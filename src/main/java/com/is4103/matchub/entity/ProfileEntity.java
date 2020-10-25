package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProfileEntity extends AccountEntity {

    @Column(nullable = true)
    @Nullable
    private String countryCode;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String country;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String profilePhoto;

    @Column(nullable = false)
    @NotNull
    @PositiveOrZero
    private Integer reputationPoints = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> followers = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> following = new HashSet<>();

//    @ElementCollection(fetch = FetchType.EAGER)
//    private Set<Long> savedResourceIds = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> upvotedProjectIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> downvotedProjectIds = new HashSet<>();

    @Column(nullable = false)
    @NotNull
    @PositiveOrZero
    private Integer spotlightChances = 0;

    //************************** ASSOCIATIONS HERE **************************
    @OneToMany(mappedBy = "postCreator")
    @JsonIgnoreProperties({"postCreator", "listOfComments"})
    private List<PostEntity> posts = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"notifiedUsers"})
    private List<AnnouncementEntity> announcements = new ArrayList<>();

    @OneToMany
    @JsonIgnoreProperties({"listOfRequests", "listOfComments"})
    private List<ResourceEntity> hostedResources = new ArrayList<>();

    //new association added
    @OneToMany
    @JsonIgnoreProperties({"listOfRequests", "listOfComments"})
    private List<ResourceEntity> savedResources = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"projects"})
    private List<SDGEntity> sdgs = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ScheduleEntity> meetings = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"joinRequests", "reviews", "projectBadge", "fundsCampaign", "listOfRequests", "sdgs", "kpis", "teamMembers", "channels", "projectOwners"})
    private List<ProjectEntity> projectsJoined = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"joinRequests", "reviews", "projectBadge", "fundsCampaign", "listOfRequests", "sdgs", "kpis", "teamMembers", "channels", "projectOwners"})
    private List<ProjectEntity> projectsOwned = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"projectFollowers", "joinRequests", "reviews", "projectBadge", "fundsCampaign", "listOfRequests", "sdgs", "kpis", "teamMembers", "channels", "projectOwners"})
    private List<ProjectEntity> projectsFollowing = new ArrayList<>();

    @OneToMany(mappedBy = "requestor")
    private List<JoinRequestEntity> joinRequests = new ArrayList<>();

    @OneToMany(mappedBy = "reviewReceiver")
    private List<ReviewEntity> reviewsReceived = new ArrayList<>();

    @JsonIgnoreProperties({"profiles", "project"})
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BadgeEntity> badges = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    private List<FundPledgeEntity> fundPladges = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<TaskEntity> tasks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ChannelEntity> managedChannel = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ChannelEntity> joinedChannel = new ArrayList<>();


    public ProfileEntity(String email, String password) {
        super(email, password);
    }

}
