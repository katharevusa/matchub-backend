package com.is4103.matchub.repository;

import com.is4103.matchub.entity.CompetitionEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.SDGEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author tjle2
 */
public interface CompetitionEntityRepository extends JpaRepository<CompetitionEntity, Long> {

    @Query(value = "SELECT ce FROM CompetitionEntity ce WHERE ce.competitionStatus = com.is4103.matchub.enumeration.CompetitionStatusEnum.ACTIVE",
            countQuery = "SELECT COUNT(ce) FROM CompetitionEntity ce WHERE ce.competitionStatus = com.is4103.matchub.enumeration.CompetitionStatusEnum.ACTIVE")
    List<CompetitionEntity> findActiveCompetitions();

    @Query(value = "SELECT DISTINCT p FROM CompetitionEntity ce, IN (ce.projects) p, IN (p.selectedTargets) st, IN (st.sdgTargets) t WHERE ce = ?1 AND ?2 MEMBER OF p.sdgs AND t.sdgTargetId IN ?3",
            countQuery = "SELECT DISTINCT COUNT(p) FROM CompetitionEntity ce, IN (ce.projects) p, IN (p.selectedTargets) st, IN (st.sdgTargets) t WHERE ce = ?1 AND ?2 MEMBER OF p.sdgs AND t.sdgTargetId IN ?3")
    List<ProjectEntity> findActiveCompetitionProjectsBySdgAndSdgTargets(CompetitionEntity competition, SDGEntity sdg, long[] sdgTargetIds);
}