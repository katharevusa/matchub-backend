/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.ProfileEntity;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %?1%)",
            countQuery = "SELECT COUNT(pe) FROM ProfileEntity pe WHERE pe.email LIKE %?1% OR "
            + "(pe.organizationName IS NOT NULL AND pe.organizationName LIKE %?1%) OR "
            + "(pe.firstName IS NOT NULL AND pe.firstName LIKE %?1%) OR "
            + "(pe.lastName IS NOT NULL AND pe.lastName LIKE %?1%)")
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

}
