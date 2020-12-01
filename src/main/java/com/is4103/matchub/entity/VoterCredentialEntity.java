package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tjle2
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoterCredentialEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voterCredentialId;

    @Column(nullable = false, unique = true)
    @NotNull
    private String voterSecret;

    @Column(nullable = false)
    @NotNull
    private Boolean isUsed = Boolean.FALSE;

    @ManyToOne
    @JsonIgnoreProperties({"posts", "announcements", "hostedResources", "savedResources", "sdgs", "projectsJoined", "projectsOwned", "projectsFollowing", "joinRequests", "reviewsReceived", "badges", "donations", "tasks", "surveyResponses", "surveys"})
    ProfileEntity voter;

    @ManyToOne
    @JsonIgnoreProperties({"projects", "voterCredentials"})
    CompetitionEntity competition;
}
