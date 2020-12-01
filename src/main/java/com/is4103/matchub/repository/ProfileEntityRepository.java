/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author longluqian
 */
public interface ProfileEntityRepository extends JpaRepository<ProfileEntity, Long> {

    @Query(value = "SELECT pe FROM ProfileEntity pe WHERE pe.accountId IN ?1",
            countQuery = "SELECT COUNT(pe) FROM ProfileEntity pe WHERE pe.accountId IN ?1")
    Page<ProfileEntity> getFollowers(Set<Long> followerIds, Pageable pageable);

    @Query(value = "SELECT pe FROM ProfileEntity pe WHERE pe.accountId IN ?1",
            countQuery = "SELECT COUNT(pe) FROM ProfileEntity pe WHERE pe.accountId IN ?1")
    Page<ProfileEntity> getFollowing(Set<Long> followingIds, Pageable pageable);

    @Query(value = "SELECT pe FROM ProfileEntity pe WHERE pe.email LIKE %?1% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %?1%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %?1%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %?1%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%))",
            countQuery = "SELECT COUNT(pe) FROM ProfileEntity pe WHERE pe.email LIKE %?1% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %?1%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %?1%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %?1%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%))"
    )
    Page<ProfileEntity> searchAllUsers(String search, Pageable pageable);

    @Override
    @Query(value = "SELECT pe FROM ProfileEntity pe WHERE pe.accountLocked = FALSE AND pe.accountExpired = FALSE "
            + "AND pe.disabled = FALSE AND pe.isVerified = TRUE",
            countQuery = "SELECT COUNT(a) FROM AccountEntity a "
            + "WHERE a.accountLocked = FALSE AND a.accountExpired = FALSE AND a.disabled = FALSE "
            + "AND a.isVerified = TRUE")
    Page<ProfileEntity> findAll(Pageable pageable);

    @Query(value = "SELECT pe FROM ProfileEntity pe WHERE pe.accountId IN ?1",
            countQuery = "SELECT COUNT(pe) FROM ProfileEntity pe WHERE pe.accountId IN ?1")
    Page<ProfileEntity> getEmployees(Set<Long> employeesId, Pageable pageable);

    @Query(value = "SELECT pe FROM ProfileEntity pe WHERE pe.accountId IN ?1",
            countQuery = "SELECT COUNT(pe) FROM ProfileEntity pe WHERE pe.accountId IN ?1")
    Page<ProfileEntity> getKAHs(Set<Long> kahIds, Pageable pageable);

    @Query(value = "SELECT pe FROM ProfileEntity pe WHERE pe.accountId IN ?1",
            countQuery = "SELECT COUNT(pe) FROM ProfileEntity pe WHERE pe.accountId IN ?1")
    Page<ProfileEntity> findAllMembers(Set<Long> memberIds, Pageable pageable);

    @Query(value = "SELECT pe FROM ProfileEntity pe WHERE pe.accountId IN ?1 AND (pe.firstName LIKE %?2% OR pe.lastName LIKE %?2% OR pe.email LIKE %?2% OR CONCAT(pe.firstName, ' ', pe.lastName) LIKE %?2% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %?2%)",
            countQuery = "SELECT COUNT(pe) FROM ProfileEntity pe WHERE pe.accountId IN ?1 AND (pe.firstName LIKE %?2% OR pe.lastName LIKE %?2% OR pe.email LIKE %?2% OR CONCAT(pe.firstName, ' ', pe.lastName) LIKE %?2% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %?2%)")
    Page<ProfileEntity> searchMembers(Set<Long> memberIds, String search, Pageable pageable);

    //this query is for system use case (issue badge)
    @Query(value = "SELECT pe FROM ProfileEntity pe WHERE (pe.accountLocked = FALSE AND pe.accountExpired = FALSE "
            + "AND pe.disabled = FALSE AND pe.isVerified = TRUE) "
            + "ORDER BY pe.reputationPoints DESC",
            countQuery = "SELECT COUNT(pe) FROM ProfileEntity pe WHERE (pe.accountLocked = FALSE AND pe.accountExpired = FALSE "
            + "AND pe.disabled = FALSE AND pe.isVerified = TRUE) "
            + "ORDER BY pe.reputationPoints DESC")
    Page<ProfileEntity> leaderboard(Pageable pageable);

    @Query(value = "SELECT p FROM ProfileEntity p WHERE p.accountLocked = FALSE AND p.accountExpired = FALSE "
            + "AND p.disabled = FALSE AND p.isVerified = TRUE",
            countQuery = "SELECT COUNT(p) FROM ProfileEntity p WHERE p.accountLocked = FALSE AND p.accountExpired = FALSE "
            + "AND p.disabled = FALSE AND p.isVerified = TRUE")
    List<ProfileEntity> findAllActiveAccounts();

//    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.sdgs sdg "
//            + "WHERE (pe.email LIKE %:search% OR "
//            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
//            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
//            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
//            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) OR "
//            + "pe.country LIKE %:search%) AND "
//            + "sdg.sdgId IN :sdgIds",
//            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.sdgs sdg "
//            + "WHERE (pe.email LIKE %:search% OR "
//            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
//            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
//            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
//            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) OR "
//            + "pe.country LIKE %:search%) AND "
//            + "sdg.sdgId IN :sdgIds")
//    Page<ProfileEntity> globalSearchAllUsers(@Param("search") String search, @Param("sdgIds") Long[] sdgIds, Pageable pageable);
    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.selectedTargets selectedTarget JOIN selectedTarget.sdg sdg JOIN selectedTarget.sdgTargets sdgTarget "
            + "WHERE (pe.email LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) OR "
            + "pe.country LIKE %:search%) AND "
            + "sdg.sdgId IN :sdgIds AND "
            + "sdgTarget.sdgTargetId IN :sdgTargetIds",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.selectedTargets selectedTarget JOIN selectedTarget.sdg sdg JOIN selectedTarget.sdgTargets sdgTarget "
            + "WHERE (pe.email LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) OR "
            + "pe.country LIKE %:search%) AND "
            + "sdg.sdgId IN :sdgIds AND "
            + "sdgTarget.sdgTargetId IN :sdgTargetIds")
    Page<ProfileEntity> globalSearchAllUsers(@Param("search") String search, @Param("sdgIds") Long[] sdgIds, @Param("sdgTargetIds") long[] sdgTargetIds, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.sdgs sdg "
            + "WHERE (pe.email LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) OR "
            + "pe.country LIKE %:search%) AND "
            + "sdg.sdgId IN :sdgIds",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.sdgs sdg "
            + "WHERE (pe.email LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) OR "
            + "pe.country LIKE %:search%) AND "
            + "sdg.sdgId IN :sdgIds")
    Page<ProfileEntity> globalSearchAllUsers(@Param("search") String search, @Param("sdgIds") Long[] sdgIds, Pageable pageable);
    
    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe "
            + "WHERE pe.email LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) OR "
            + "pe.country LIKE %:search%",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe "
            + "WHERE pe.email LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) OR "
            + "pe.country LIKE %:search%")
    Page<ProfileEntity> globalSearchAllUsers(@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe "
            + "JOIN pe.selectedTargets selectedTarget "
            + "JOIN selectedTarget.sdg sdg "
            + "JOIN selectedTarget.sdgTargets sdgTarget "
            + "WHERE (pe.email LIKE %:search% OR "
            + "pe.country LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%))) AND "
            + "pe.country LIKE %:country% AND "
            + "sdg.sdgId IN :sdgIds AND "
            + "sdgTarget.sdgTargetId IN :sdgTargetIds",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe "
            + "JOIN pe.selectedTargets selectedTarget "
            + "JOIN selectedTarget.sdg sdg "
            + "JOIN selectedTarget.sdgTargets sdgTarget "
            + "WHERE (pe.email LIKE %:search% OR "
            + "pe.country LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%))) AND "
            + "pe.country LIKE %:country% AND "
            + "sdg.sdgId IN :sdgIds AND "
            + "sdgTarget.sdgTargetId IN :sdgTargetIds")
    Page<ProfileEntity> globalSearchAllUsers(@Param("search") String search, @Param("country") String country, @Param("sdgIds") Long[] sdgIds, @Param("sdgTargetIds") long[] sdgTargetIds, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe "
            + "JOIN pe.sdgs sdg "
            + "WHERE (pe.email LIKE %:search% OR "
            + "pe.country LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%))) AND "
            + "pe.country LIKE %:country% AND "
            + "sdg.sdgId IN :sdgIds",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe "
            + "JOIN pe.sdgs sdg "
            + "WHERE (pe.email LIKE %:search% OR "
            + "pe.country LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%))) AND "
            + "pe.country LIKE %:country% AND "
            + "sdg.sdgId IN :sdgIds")
    Page<ProfileEntity> globalSearchAllUsers(@Param("search") String search, @Param("country") String country, @Param("sdgIds") Long[] sdgIds, Pageable pageable);

     @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe "
            + "WHERE pe.email LIKE %:search% OR "
            + "pe.country LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) AND "
            + "pe.country LIKE %:country% ",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe "
            + "WHERE pe.email LIKE %:search% OR "
            + "pe.country LIKE %:search% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %:search%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %:search%) OR "
            + "(pe.firstName IS NOT NULL AND pe.lastName IS NOT NULL AND (CONCAT(pe.firstName, ' ', pe.lastName) LIKE %:search% OR CONCAT(pe.lastName, ' ', pe.firstName) LIKE %:search%)) AND "
            + "pe.country LIKE %:country%")
    Page<ProfileEntity> globalSearchAllUsers(@Param("search") String search, @Param("country") String country, Pageable pageable);
    
    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe "
            + "WHERE pe.accountId <> ?1 AND "
            + "pe.accountId NOT IN ?2 AND "
            + "pe.country = ?3",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe "
            + "WHERE pe.accountId <> ?1 AND "
            + "pe.accountId NOT IN ?2 AND "
            + "pe.country = ?3")
    Page<ProfileEntity> recommendProfiles(Long id, Set<Long> followingIds, String country, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.country = ?2",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.country = ?2")
    Page<ProfileEntity> recommendProfiles(Long id, String country, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND (pe.country = ?2 OR "
            + "project IN ?3)",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND (pe.country = ?2 OR "
            + "project IN ?3)")
    Page<ProfileEntity> recommendProfiles(Long id, String country, List<ProjectEntity> projs, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.accountId NOT IN ?2 "
            + "AND (pe.country = ?3 OR "
            + "project IN ?4)",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.projectsJoined project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.accountId NOT IN ?2 "
            + "AND (pe.country = ?3 OR "
            + "project IN ?4)")
    Page<ProfileEntity> recommendProfiles(Long id, Set<Long> followingIds, String country, List<ProjectEntity> projs, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe "
            + "WHERE pe.accountId <> ?1 AND "
            + "pe.accountId NOT IN ?2 AND "
            + "pe.country = ?3 AND "
            + "pe.firstName IS NOT NULL",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe "
            + "WHERE pe.accountId <> ?1 AND "
            + "pe.accountId NOT IN ?2 AND "
            + "pe.country = ?3 AND "
            + "pe.firstName IS NOT NULL")
    Page<ProfileEntity> recommendIndividualProfiles(Long id, Set<Long> followingIds, String country, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.accountId NOT IN ?2 "
            + "AND (pe.country = ?3 OR "
            + "project IN ?4) AND "
            + "pe.firstName IS NOT NULL",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.projectsJoined project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.accountId NOT IN ?2 "
            + "AND (pe.country = ?3 OR "
            + "project IN ?4) AND "
            + "pe.firstName IS NOT NULL")
    Page<ProfileEntity> recommendIndividualProfiles(Long id, Set<Long> followingIds, String country, List<ProjectEntity> projs, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.country = ?2 "
            + "AND pe.firstName IS NOT NULL",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.country = ?2 "
            + "AND pe.firstName IS NOT NULL")
    Page<ProfileEntity> recommendIndividualProfiles(Long id, String country, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND (pe.country = ?2 OR "
            + "project IN ?3) "
            + "AND pe.firstName IS NOT NULL",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND (pe.country = ?2 OR "
            + "project IN ?3) "
            + "AND pe.firstName IS NOT NULL")
    Page<ProfileEntity> recommendIndividualProfiles(Long id, String country, List<ProjectEntity> projs, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe "
            + "WHERE pe.accountId <> ?1 AND "
            + "pe.accountId NOT IN ?2 AND "
            + "pe.country = ?3 AND "
            + "pe.organizationName IS NOT NULL",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe "
            + "WHERE pe.accountId <> ?1 AND "
            + "pe.accountId NOT IN ?2 AND "
            + "pe.country = ?3 AND "
            + "pe.organizationName IS NOT NULL")
    Page<ProfileEntity> recommendOrganisationProfiles(Long id, Set<Long> followingIds, String country, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.accountId NOT IN ?2 "
            + "AND (pe.country = ?3 OR "
            + "project IN ?4) AND "
            + "pe.organizationName IS NOT NULL",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.projectsJoined project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.accountId NOT IN ?2 "
            + "AND (pe.country = ?3 OR "
            + "project IN ?4) AND "
            + "pe.organizationName IS NOT NULL")
    Page<ProfileEntity> recommendOrganisationProfiles(Long id, Set<Long> followingIds, String country, List<ProjectEntity> projs, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.country = ?2 "
            + "AND pe.organizationName IS NOT NULL",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND pe.country = ?2 "
            + "AND pe.organizationName IS NOT NULL")
    Page<ProfileEntity> recommendOrganisationProfiles(Long id, String country, Pageable pageable);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND (pe.country = ?2 OR "
            + "project IN ?3) "
            + "AND pe.organizationName IS NOT NULL",
            countQuery = "SELECT DISTINCT COUNT(pe) FROM ProfileEntity pe JOIN pe.projectsFollowing project "
            + "WHERE pe.accountId <> ?1 "
            + "AND (pe.country = ?2 OR "
            + "project IN ?3) "
            + "AND pe.organizationName IS NOT NULL")
    Page<ProfileEntity> recommendOrganisationProfiles(Long id, String country, List<ProjectEntity> projs, Pageable pageable);

    Optional<ProfileEntity> findByEmail(String email);

    Optional<ProfileEntity> findByStripeAccountUid(String stripeAccountUid);

    @Query(value = "SELECT DISTINCT pe FROM ProfileEntity pe WHERE pe.joinDate BETWEEN ?1 AND ?2 ")
    List<ProfileEntity> findUsersByJoinDate(LocalDateTime from, LocalDateTime to);


    @Query(value =" SELECT pe FROM ProfileEntity pe JOIN pe.roles per WHERE per = 'SYSADMIN'")
    List<ProfileEntity> findAdminUsers();

}
