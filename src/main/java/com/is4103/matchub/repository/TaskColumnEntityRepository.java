/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.TaskColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author longluqian
 */
public interface TaskColumnEntityRepository extends JpaRepository<TaskColumnEntity, Long> {

    @Query(value = "SELECT t FROM TaskColumnEntity t WHERE t.isDone = TRUE AND t.kanbanBoardId = ?1")
    public TaskColumnEntity getCompletedTaskColumnByKanbanboardId(Long kanbanBoardId);

}
