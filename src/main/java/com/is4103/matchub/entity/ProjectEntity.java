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
import javax.persistence.ManyToMany;
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

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> keywords = new HashSet<>();

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
    private ProjectStatusEnum status;

    @Column(nullable = false)
    @NotNull
    private Integer votes = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> photos = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<StakeholderEntity> stakeholders = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<IndividualEntity> teamMembers = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ReviewEntity> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<BadgeEntity> badges = new ArrayList<>();

    @OneToMany
//    (mappedBy = "project")
    private List<ScheduleEntity> meetings = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ResourceEntity> resources = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<SDGEntity> sdgs = new ArrayList<>();

   @OneToMany
//    (mappedBy = "project")
    private List<KPIEntity> kpis = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private KanbanBoardEntity kanbanBoard;

    public ProjectEntity(String projectTitle, String projectDescription, String country, LocalDateTime startDate, LocalDateTime endDate, ProjectStatusEnum status, KanbanBoardEntity kanbanBoard) {
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.kanbanBoard = kanbanBoard;
    }

}
