/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.KanbanBoardEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author longluqian
 */
public interface KanbanBoardEntityRepository extends JpaRepository<KanbanBoardEntity, Long>{
    
    @Query("Select kb from KanbanBoardEntity kb WHERE kb.channelUid = :id")
    public Optional<KanbanBoardEntity> findByChannelUId(String id);
    
    
    @Query("Select kb from KanbanBoardEntity kb WHERE kb.projectId = :projectId")
    public List<KanbanBoardEntity> findKanbanBoardsByProjectId(Long projectId);
    
    
}
