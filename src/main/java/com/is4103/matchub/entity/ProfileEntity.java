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
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProfileEntity extends AccountEntity {

    @Column(unique = true)
    @NotNull
    private String phoneNumber;

    @Column(nullable = true)
    private String country;

    @Column(nullable = true)
    private String profilePhoto;

    @Column(nullable = false)
    @NotNull
    private Integer reputationPoints = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> followers = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> following = new HashSet<>();

    @OneToMany(mappedBy = "profile")
    private List<NotificationEntity> notifications = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ResourceEntity> hostedResources = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<SDGEntity> sdgs = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<TaskEntity> tasks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ChatEntity> chats = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<BadgeEntity> badges = new ArrayList<>();

    @OneToMany(mappedBy = "reviewer")
    private List<ReviewEntity> reviews = new ArrayList<>();

    public ProfileEntity(String username, String password, String email) {
        super(username, password, email);
    }

}
