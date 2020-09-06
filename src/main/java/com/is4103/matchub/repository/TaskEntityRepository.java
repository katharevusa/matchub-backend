/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.TaskEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author ngjin
 */
public interface TaskEntityRepository extends JpaRepository<TaskEntity, Long> {

    @Query("SELECT distinct t FROM TaskEntity t "
            + "JOIN t.profiles p "
            + "WHERE t.statusEnum <> com.is4103.matchub.enumeration.TaskStatusEnum.COMPLETED "
            + "AND p.accountId = :id")
    List<TaskEntity> getIncompleteTasksOfAccount(Long id);
}
