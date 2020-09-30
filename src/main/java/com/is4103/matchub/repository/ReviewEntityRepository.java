/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.ReviewEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author ngjin
 */
public interface ReviewEntityRepository extends JpaRepository<ReviewEntity, Long> {

    @Query(value = "SELECT r FROM ReviewEntity r WHERE r.reviewReceiver.accountId = :id ORDER BY r.timeCreated",
            countQuery = "SELECT COUNT(r) FROM ReviewEntity r WHERE r.reviewReceiver.accountId = :id ORDER BY r.timeCreated")
    Page<ReviewEntity> getReviewsReceivedByAccountId(Long id, Pageable pageable);

    @Query(value = "SELECT r FROM ReviewEntity r WHERE r.reviewer.accountId = :id ORDER BY r.timeCreated",
            countQuery = "SELECT COUNT(r) FROM ReviewEntity r WHERE r.reviewer.accountId = :id ORDER BY r.timeCreated")
    Page<ReviewEntity> getReviewsGivenByAccountId(Long id, Pageable pageable);

    //this query is to check for reviews given before deleting an account (used in UserServiceImpl)
    @Query(value = "SELECT r FROM ReviewEntity r WHERE r.reviewer.accountId = :id")
    List<ReviewEntity> getReviewsGivenByAccountId(Long id);
}
