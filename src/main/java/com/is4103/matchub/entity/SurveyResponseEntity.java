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
import javax.persistence.ManyToOne;
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
public class SurveyResponseEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyResponseId;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timestamp;
    
    @NotNull
    @ManyToOne
    @JsonIgnoreProperties({"surveyResponses","donations","posts","announcements","hostedResources","savedResources","sdgs","projectsJoined","projectsOwned","projectsFollowing","joinRequests", "reviewsReceived","badges","tasks","managedChannel","joinedChannel"})
    private ProfileEntity respondent;
    
    @NotNull
    @ManyToOne
    @JsonIgnoreProperties({"surveyResponses"})
    private SurveyEntity survey;
    
    @ManyToMany
    private List<QuestionResponseEntity> questionResponses = new ArrayList<>();
    
    
    
}
