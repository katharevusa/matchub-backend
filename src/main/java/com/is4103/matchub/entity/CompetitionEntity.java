package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.is4103.matchub.enumeration.CompetitionStatusEnum;
import java.math.BigDecimal;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author tjle2
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long competitionId;

    @Column(nullable = false)
    @NotNull
    private String competitionTitle;

    @Column(nullable = false, length = 1000)
    @NotNull
    private String competitionDescription;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;

    @Column(nullable = false)
    @NotNull
    private BigDecimal prizeMoney;

    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn()
    private List<String> photos = new ArrayList<>();

    //Key: filename, Value = docPath
    @ElementCollection
    private Map<String, String> documents = new HashMap<>();

    @Column(nullable = false)
    @NotNull
    private CompetitionStatusEnum competitionStatus = CompetitionStatusEnum.INACTIVE;

    @OneToMany
    @JsonIgnoreProperties({"joinRequests", "reviews", "projectBadge", "fundCampaigns", "listOfRequests", "listOfResourceTransactions", "teamMembers", "projectFollowers", "projectOwners", "selectedTargets", "competition"})
    List<ProjectEntity> projects = new ArrayList<>();

    @OneToMany
    @JsonIgnoreProperties({"voter", "competition"})
    List<VoterCredentialEntity> voterCredentials = new ArrayList<>();
}
