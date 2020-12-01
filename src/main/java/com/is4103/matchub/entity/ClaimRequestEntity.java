package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.is4103.matchub.enumeration.ClaimRequestStatusEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
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
public class ClaimRequestEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimRequestId;

    @Column(nullable = false, unique = true)
    @NotNull
    private String email;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime requestCreationTime = LocalDateTime.now();

    @Column(nullable = false)
    @NotNull
    private ClaimRequestStatusEnum claimRequestStatus = ClaimRequestStatusEnum.PENDING;

    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn()
    private List<String> photos = new ArrayList<>();

    //Key: filename, Value = docPath
    @ElementCollection
    private Map<String, String> verificationDoc = new HashMap<>();

    @ManyToOne
    @JsonIgnoreProperties({"posts", "announcements", "hostedResources", "savedResources", "sdgs", "projectsJoined", "projectsOwned", "projectsFollowing", "joinRequests", "reviewsReceived", "badges", "donations", "tasks", "surveyResponses", "surveys", "selectedTargets"})
    private ProfileEntity profile;

}
