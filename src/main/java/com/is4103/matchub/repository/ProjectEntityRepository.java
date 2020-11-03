package com.is4103.matchub.repository;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.ProjectEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface ProjectEntityRepository extends JpaRepository<ProjectEntity, Long> {

    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projectTitle LIKE %?1%",
            countQuery = "SELECT COUNT(pe) FROM ProjectEntity pe WHERE pe.projectTitle LIKE %?1%")
    Page<ProjectEntity> search(String search, Pageable pageable);

    @Query(value = " SELECT pe FROM ProjectEntity pe WHERE pe.projCreatorId = :profileId")
    List<ProjectEntity> getCreatedProjectByProfileId(Long profileId);

    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projectTitle LIKE %?1% OR pe.projectDescription LIKE %?1% OR pe.country LIKE %?1%",
            countQuery = "SELECT COUNT(pe) FROM ProjectEntity pe WHERE pe.projectTitle LIKE %?1% OR pe.projectDescription LIKE %?1% OR pe.country LIKE %?1%")
    List<ProjectEntity> searchByKeywords(String search);

    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE ",
            countQuery = "SELECT COUNT(pe) FROM ProjectEntity pe WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE")
    Page<ProjectEntity> getLaunchedProjects(Pageable pageable);

    //for issue long service badge usecase
//    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projCreatorId = :id ORDER BY pe.",
//            countQuery = "SELECT COUNT(pe) FROM ProjectEntity pe WHERE pe.projCreatorId = :id")
//    Page<ProjectEntity> getLatestProjectById(Long id, Pageable pageable);
    /* query method for significantprojectcontributor badge usecase*/
//    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.COMPLETED "
//            + "AND ((pe.teamMembers tm WHERE tm.accountId = :accountId) OR (pe.projectOwners po WHERE po.accountId = :accountId))")
//    List<ProjectEntity> getCompletedProjectsByAccountId(Long accountId);
    @Query(value = "SELECT pe FROM ProjectEntity pe JOIN pe.teamMembers tm JOIN pe.projectOwners po WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.COMPLETED "
            + "AND ((tm.accountId = :accountId) OR (po.accountId = :accountId))")
    List<ProjectEntity> getCompletedProjectsByAccountId(Long accountId);

    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE")
    List<ProjectEntity> getAllActiveProjects();

    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE "
            + "AND ("
            + "(pe.startDate BETWEEN ?1 AND ?2) "
            + "OR (pe.endDate BETWEEN ?1 AND ?2) "
            + "OR (?1 BETWEEN pe.startDate AND pe.endDate) "
            + "OR (?2 BETWEEN pe.startDate AND pe.endDate)"
            + ")")
    List<ProjectEntity> getAllActiveProjects(LocalDateTime resourceStartTime, LocalDateTime resourceEndTime);

    @Query(value = "SELECT pe FROM ProjectEntity pe "
            + "WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE "
            + "AND pe.country = ?1")
    List<ProjectEntity> getAllActiveProjectsInCountry(String country);

    @Query(value = "SELECT pe FROM ProjectEntity pe "
            + "WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE "
            + "AND pe.country = ?1 "
            + "AND ("
            + "(pe.startDate BETWEEN ?2 AND ?3) "
            + "OR (pe.endDate BETWEEN ?2 AND ?3) "
            + "OR (?2 BETWEEN pe.startDate AND pe.endDate) "
            + "OR (?3 BETWEEN pe.startDate AND pe.endDate)"
            + ")")
    List<ProjectEntity> getAllActiveProjectsInCountry(String country, LocalDateTime resourceStartTime, LocalDateTime resourceEndTime);

    @Query(value = "SELECT pe FROM ProjectEntity pe "
            + "WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE "
            + "AND pe.country <> ?1")
    List<ProjectEntity> getAllActiveProjectsNotInCountry(String country);

    @Query(value = "SELECT pe FROM ProjectEntity pe "
            + "WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE "
            + "AND pe.country <> ?1 "
            + "AND ("
            + "(pe.startDate BETWEEN ?2 AND ?3) "
            + "OR (pe.endDate BETWEEN ?2 AND ?3) "
            + "OR (?2 BETWEEN pe.startDate AND pe.endDate) "
            + "OR (?3 BETWEEN pe.startDate AND pe.endDate)"
            + ")")
    List<ProjectEntity> getAllActiveProjectsNotInCountry(String country, LocalDateTime resourceStartTime, LocalDateTime resourceEndTime);

    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.spotlight = TRUE ORDER BY pe.spotlightEndTime DESC")
    List<ProjectEntity> getSpotlightedProjects();

    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.spotlight = TRUE ORDER BY pe.spotlightEndTime DESC",
            countQuery = "SELECT COUNT(pe) FROM ProjectEntity pe WHERE pe.spotlight = TRUE ORDER BY pe.spotlightEndTime DESC")
    Page<ProjectEntity> getSpotlightedProjects(Pageable pageable);

}
