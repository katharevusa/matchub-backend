/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
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
 * @author longluqian
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionnaireId;

    @NotNull
    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime createdDate;

    @Column(nullable = true, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime openDate;

    @Column(nullable = true, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime closeDate;
    
    @NotNull
    private boolean expired = false;

    @OneToMany
    @JsonIgnoreProperties({"survey"})
    private List<SurveyResponseEntity> surveyResponses = new ArrayList<>();

    @ManyToMany
    @JsonIgnoreProperties({"surveys", "projectsFollowing", "hostedResources", "sdgs", "meetings", "projectsJoined", "projectsOwned", "joinRequests", "reviewsReceived", "badges", "fundPladges", "tasks", "managedChannel", "joinedChannel", "likedPosts"})
    private List<ProfileEntity> recievers = new ArrayList<>();

    @OneToMany
    @JsonIgnoreProperties({"survey"})
    private List<QuestionEntity> questions = new ArrayList<>();

}
