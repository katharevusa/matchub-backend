/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.ResourceRequestEntity;
import com.is4103.matchub.enumeration.RequestStatusEnum;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author longluqian
 */
public interface ResourceRequestEntityRepository extends JpaRepository<ResourceRequestEntity, Long> {

    @Query(value = "SELECT rr FROM ResourceRequestEntity rr WHERE rr.requestorId = :id",
            countQuery = "SELECT COUNT(rr) FROM ResourceRequestEntity rr WHERE rr.requestorId = :id")
    Page<ResourceRequestEntity> getResourceRequestByRequestorId(Long id, Pageable pageable);

    @Query(value = "SELECT rr FROM ResourceRequestEntity rr WHERE rr.resourceId = :id",
            countQuery = "SELECT COUNT(rr) FROM ResourceRequestEntity rr WHERE rr.resourceId = :id")
    Page<ResourceRequestEntity> getResourceRequestByResourceId(Long id, Pageable pageable);

    @Query(value = "SELECT rr FROM ResourceRequestEntity rr WHERE rr.projectId = :id",
            countQuery = "SELECT COUNT(rr) FROM ResourceRequestEntity rr WHERE rr.projectId = :id")
    Page<ResourceRequestEntity> getResourceRequestByProjectId(Long id, Pageable pageable);

    @Query(value = " SELECT rr FROM ResourceRequestEntity rr WHERE rr.projectId = :projectId AND rr.resourceId = :resourceId AND rr.status = :status")
    Optional< ResourceRequestEntity> searchResourceRequestProjectByProjectAndResourceOnHold(Long projectId, Long resourceId,RequestStatusEnum status);

}
