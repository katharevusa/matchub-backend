package com.is4103.matchub.entity;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProfileEntity extends AccountEntity {

    @Column(unique = true)
//    @NotNull
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

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> savedResourceIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> upvotedProjectIds = new HashSet<>();

    @Column(nullable = false)
    @NotNull
    @PositiveOrZero
    private Integer spotlightChances = 0;

    @OneToMany(mappedBy = "postCreator")
    private List<PostEntity> posts = new ArrayList<>();

    @OneToMany(mappedBy = "notifiedUser")
    private List<NotificationEntity> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "resourceOwner")
    private List<ResourceEntity> hostedResources = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<SDGEntity> sdgs = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ScheduleEntity> meetings = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ProjectEntity> projectsJoined = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ProjectEntity> projectsOwned = new ArrayList<>();

    @OneToMany(mappedBy = "requestor")
    private List<JoinRequestEntity> joinRequests = new ArrayList<>();

    @OneToMany(mappedBy = "reviewReceiver")
    private List<ReviewEntity> reviewsReceived = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<BadgeEntity> badges = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    private List<FundPledgeEntity> fundPladges = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<TaskEntity> tasks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ChannelEntity> managedChannel = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ChannelEntity> joinedChannel = new ArrayList<>();
    
    @OneToMany
    private List<PostEntity> likedPosts = new ArrayList<>();

    public ProfileEntity(String email, String password) {
        super(email, password);
    }

}
