/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.entity.TaskEntity;
import com.is4103.matchub.repository.KanbanBoardEntityRepository;
import com.is4103.matchub.repository.TaskColumnEntityRepository;
import com.is4103.matchub.vo.TaskColumnVO;
import java.util.ArrayList;
import java.util.List;
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
    
    @Override 
    public KanbanBoardEntity createNewColumn(TaskColumnVO vo){
        
        // checking only channel admins can create column 
        
        TaskColumnEntity newTaskColumn = new TaskColumnEntity();
        vo.createNewTaskColumn(newTaskColumn);
        newTaskColumn = taskColumnEntityRepository.saveAndFlush(newTaskColumn);
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(newTaskColumn.getKanbanBoardId()).get();
        kanbanBoardEntity.getTaskColumns().add(newTaskColumn);
        kanbanBoardEntity = kanbanBoardEntityRepository.saveAndFlush(kanbanBoardEntity);
        return kanbanBoardEntity;
    }
    
    @Override 
    public KanbanBoardEntity updateColumn(TaskColumnVO vo){
        
        // checking only channel admins can update column 
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(vo.getColumnId()).get();      
        vo.updateTaskColumn(taskColumn);
        taskColumn = taskColumnEntityRepository.saveAndFlush(taskColumn);
        kanbanBoardEntityRepository.flush();
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(taskColumn.getKanbanBoardId()).get();
        return kanbanBoardEntity;
    }
    
    @Override 
    public KanbanBoardEntity deleteColumn(Long columnId){
        
        // checking only channel admins can delete column 
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(columnId).get();
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(taskColumn.getKanbanBoardId()).get();
       
        //delete tasks
       for(TaskEntity t : taskColumn.getListOfTasks()){
           // call delete task method
       }
       
       //remove column from kanban board 
       kanbanBoardEntity.getTaskColumns().remove(taskColumn);
       kanbanBoardEntity = kanbanBoardEntityRepository.saveAndFlush(kanbanBoardEntity);
       // delete task column
       taskColumn.setListOfTasks(new ArrayList<>());
       taskColumnEntityRepository.delete(taskColumn);
       
        return kanbanBoardEntity;
    }
    
    @Override 
    public TaskColumnEntity getColumnByColumnId(Long columnId){
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(columnId).get();
        taskColumn.getListOfTasks();
        return taskColumn;      
    }
    
    @Override 
    public List<TaskColumnEntity> getColumnsByKanbanBoardId(Long kanbanBoardId){
        KanbanBoardEntity kanbanBoard = kanbanBoardEntityRepository.findById(kanbanBoardId).get();   
        return kanbanBoard.getTaskColumns();
    }
    
    
    @Override 
    public KanbanBoardEntity rearrangeColumn(Long kanbanBoardId, List<Long> columnIdSequence){
        KanbanBoardEntity kanbanBoard = kanbanBoardEntityRepository.findById(kanbanBoardId).get();   
        kanbanBoard.setTaskColumns(new ArrayList<>());
        for(Long columnId : columnIdSequence){
            kanbanBoard.getTaskColumns().add(taskColumnEntityRepository.findById(columnId).get());
            
        }
       kanbanBoard = kanbanBoardEntityRepository.saveAndFlush(kanbanBoard);
        
        return kanbanBoard;
        
        
    }
    
    
    
    
    
    
}
