/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.JoinRequestEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author longluqian
 */
public interface JoinRequestEntityRepository extends JpaRepository<JoinRequestEntity, Long> {

    /**
     *
     * @param projectId
     * @param requestorId
     * @return
     */
    @Query(value = " SELECT jr FROM JoinRequestEntity jr WHERE jr.project.projectId = :projectId AND jr.requestor.accountId = :requestorId")
    Optional< JoinRequestEntity> searchJoinRequestProjectByProjectAndRequestor(Long projectId, Long requestorId);

}
