package com.is4103.matchub.vo;

import com.is4103.matchub.entity.CompetitionEntity;
import com.is4103.matchub.enumeration.CompetitionStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author tjle2
 */
@Data
public class CompetitionVO {

    @NotNull(message = "Competition title can not be null")
    @NotBlank(message = "Competition title can not be empty")
    private String competitionTitle;

    @NotNull(message = "Competition description can not be null")
    @NotBlank(message = "Competition description can not be empty")
    private String competitionDescription;

    @NotNull(message = "Start date can not be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date can not be null")
    private LocalDateTime endDate;

    @NotNull(message = "Prize money can not be null")
    private BigDecimal prizeMoney;

    private CompetitionStatusEnum competitionStatus;

    public void updateCompetition(CompetitionEntity newCompetition) {
        newCompetition.setCompetitionTitle(this.competitionTitle);
        newCompetition.setCompetitionDescription(this.competitionDescription);
        newCompetition.setStartDate(this.startDate);
        newCompetition.setEndDate(this.endDate);
        newCompetition.setPrizeMoney(this.prizeMoney);

        if (this.competitionStatus != null) {
            newCompetition.setCompetitionStatus(this.competitionStatus);
        }
    }
}
