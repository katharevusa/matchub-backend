/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author longluqian
 */
public interface AnnouncementEntityRepository extends JpaRepository<AnnouncementEntity, Long> {
    @Query(value = "SELECT a FROM AnnouncementEntity a WHERE a.projectId = :projectId AND a.type = :type")      
    List<AnnouncementEntity> searchProjectAnnouncementProjectIdAndType(Long projectId, AnnouncementTypeEnum type);

}
