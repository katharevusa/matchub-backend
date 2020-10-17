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
    private String columnDescription;
    
    
    @NotNull
    private Long kanbanBoardId;
    
    private Long columnId;
    
   
    private Long editorId;
    
   
    
    public void createNewTaskColumn(TaskColumnEntity newTaskColumn){
        newTaskColumn.setColumnDescription(this.columnDescription);
        newTaskColumn.setColumnTitle(this.columnTitle);
        newTaskColumn.setKanbanBoardId(kanbanBoardId);
    }
    
    public void updateTaskColumn(TaskColumnEntity taskColumn){
        taskColumn.setColumnDescription(this.columnDescription);
        taskColumn.setColumnTitle(this.columnTitle);
    }
  
   
}
