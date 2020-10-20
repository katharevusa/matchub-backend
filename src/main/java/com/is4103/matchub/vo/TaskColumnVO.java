/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.TaskColumnEntity;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class TaskColumnVO {
    
    @NotNull
    private String columnTitle;
    
   

    
    
    @NotNull
    private Long kanbanBoardId;
    
    private Long columnId;
    
   
    private Long editorId;
    
   
    
    public void createNewTaskColumn(TaskColumnEntity newTaskColumn){
       
        newTaskColumn.setColumnTitle(this.columnTitle);
        newTaskColumn.setKanbanBoardId(kanbanBoardId);
    }
    
    public void updateTaskColumn(TaskColumnEntity taskColumn){
     
        taskColumn.setColumnTitle(this.columnTitle);
    }
  
   
}
