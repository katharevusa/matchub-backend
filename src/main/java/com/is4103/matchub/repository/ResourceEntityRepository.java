/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ResourceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author ngjin
 */
public interface ResourceEntityRepository extends JpaRepository<ResourceEntity, Long> {

    Optional<ResourceEntity> findById(Long id);
    
//    @Query("SELECT r FROM ResourceEntity r WHERE r.available = TRUE AND r.resourceOwner = :profile")
//    List<ResourceEntity> getAvailableResourcesOfAccount(ProfileEntity profile);
}
