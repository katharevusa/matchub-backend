/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.ResourceCategoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author longluqian
 */
public interface ResourceCategoryEntityRepository extends JpaRepository<ResourceCategoryEntity, Long> {

    Optional<ResourceCategoryEntity> findById(Long id);

}
