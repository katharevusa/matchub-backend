/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.OrganisationEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author ngjin
 */
public interface OrganisationEntityRepository extends JpaRepository<OrganisationEntity, Long> {

    @Query(value = "SELECT DISTINCT o FROM OrganisationEntity o",
            countQuery = "SELECT COUNT(o) FROM OrganisationEntity o")
    Page<OrganisationEntity> findAll(Pageable pageable);

    @Query(value = "SELECT o FROM OrganisationEntity o WHERE o.organizationName LIKE %?1% OR o.email LIKE %?1%",
            countQuery = "SELECT COUNT(o) FROM OrganisationEntity o WHERE o.organizationName LIKE %?1% OR o.email LIKE %?1%")
    Page<OrganisationEntity> searchOrganisations(String search, Pageable pageable);

    @Query(value = "SELECT o FROM OrganisationEntity o ORDER BY o.reputationPoints DESC",
            countQuery = "SELECT COUNT(o) FROM OrganisationEntity o ORDER BY o.reputationPoints DESC")
    Page<OrganisationEntity> organisationalLeaderboard(Pageable pageable);

//    @Query(value = "SELECT o FROM OrganisationEntity o WHERE :accountId IN o.employees")
//    Optional<OrganisationEntity> findOrgansationOfProfile(Long accountId);
}
