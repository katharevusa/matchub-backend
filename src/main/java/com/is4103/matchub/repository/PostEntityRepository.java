/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.PostEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author ngjin
 */
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {

    @Query(value = "SELECT p FROM PostEntity p WHERE p.postCreator.accountId = :id ORDER BY p.timeCreated DESC",
            countQuery = "SELECT COUNT(p) FROM PostEntity p WHERE p.postCreator.accountId = :id ORDER BY p.timeCreated DESC")
    Page<PostEntity> getPostsByAccountId(Long id, Pageable pageable);
    
    @Query(value = "SELECT p FROM PostEntity p WHERE p.originalPostId = :postId")
    List<PostEntity> getAllSharedPostByOriginalPostId(Long postId);
}
