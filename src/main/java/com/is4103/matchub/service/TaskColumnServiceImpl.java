/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.repository.KanbanBoardEntityRepository;
import com.is4103.matchub.repository.TaskColumnEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class TaskColumnServiceImpl implements TaskColumnService{
    //create taskColumn
    
    @Autowired
    TaskColumnEntityRepository taskColumnEntityRepository;
    
    @Autowired
    KanbanBoardEntityRepository kanbanBoardEntityRepository;
    
//    @Override 
//    public KanbanBoardEntity createNewColumn(TaskColumnVO vo){
//        TaskColumnEntity newTaskColumn = new TaskColumnEntity();
//        vo.createNewTaskColumn(newTaskColumn);
//        
//        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(newTaskColumn.getKanbanBoardId()).get();
//        kanbanBoardEntity.getTaskColumns().add(newTaskColumn);
//        
//    }
}
