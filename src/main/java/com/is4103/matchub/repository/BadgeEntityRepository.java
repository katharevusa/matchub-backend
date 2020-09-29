/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.BadgeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author ngjin
 */
public interface BadgeEntityRepository extends JpaRepository<BadgeEntity, Long> {
    
    BadgeEntity findByBadgeTitle(String badgeTitle);

    @Query(value = "SELECT b, p FROM BadgeEntity b JOIN b.profiles p WHERE p.accountId = :accountId",
            countQuery = "SELECT COUNT(b), p FROM BadgeEntity b JOIN b.profiles p WHERE p.accountId = :accountId")
    Page<BadgeEntity> getBadgesByAccountId(Long accountId, Pageable pageable);
}
