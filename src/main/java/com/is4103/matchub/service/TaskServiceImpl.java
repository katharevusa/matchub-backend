/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.repository.KanbanBoardEntityRepository;
import com.is4103.matchub.repository.TaskColumnEntityRepository;
import com.is4103.matchub.repository.TaskEntityRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author longluqian
 */
@Data
public class TaskServiceImpl implements TaskService{
    
    
    @Autowired
   private TaskEntityRepository taskEntityRepository;
    
    @Autowired
    private TaskColumnEntityRepository taskColumnEntityRepository;
    
    @Autowired KanbanBoardEntityRepository kanbanBoardEntityRepository;
    
//    Create Channel Tasks
//View list of Tasks
//View a Particular Tasks
//Update Tasks
//Delete Task
//Filter Tasks by Assignee
//Add comments to task
//Delete task comment
//Upload documents
//remove documents
}
