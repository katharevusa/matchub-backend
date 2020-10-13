/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author ngjin
 */
public interface IndividualEntityRepository extends JpaRepository<IndividualEntity, Long> {
    
    @Query(value = "SELECT DISTINCT i FROM IndividualEntity i",
            countQuery = "SELECT COUNT(i) FROM IndividualEntity i")
    Page<IndividualEntity> findAll(Pageable pageable);

    @Query(value = "SELECT i FROM IndividualEntity i WHERE i.firstName LIKE %?1% OR i.lastName LIKE %?1% OR i.email LIKE %?1% OR CONCAT(i.firstName, ' ', i.lastName) LIKE %?1% OR CONCAT(i.lastName, ' ', i.firstName) LIKE %?1%",
            countQuery = "SELECT COUNT(i) FROM IndividualEntity i WHERE i.firstName LIKE %?1% OR i.lastName LIKE %?1% OR i.email LIKE %?1% OR CONCAT(i.firstName, ' ', i.lastName) LIKE %?1% OR CONCAT(i.lastName, ' ', i.firstName) LIKE %?1%")
    Page<IndividualEntity> searchIndividuals(String search, Pageable pageable);
}
