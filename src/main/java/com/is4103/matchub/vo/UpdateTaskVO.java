/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.TaskEntity;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class UpdateTaskVO {
    private Long taskId;
    
    @NotNull(message = "Task title can not be null")
    private String taskTitle;


    
    private String taskDescription;
    
    
    private LocalDateTime expectedDeadline;
    
    private Long taskLeaderId;
    
    @NotNull(message = "Task creator Id can not be null")
    private Long taskCreatorOrEditorId;
    
    @NotNull(message = "Kanban Board Id can not be null")
    private Long kanbanboardId;
    
    public void updateTask(TaskEntity newTask){
        newTask.setTaskTitle(this.taskTitle);
        if(taskDescription!=null){
            newTask.setTaskDescription(this.taskDescription);
        }
        if(expectedDeadline!=null){
            newTask.setExpectedDeadline(this.expectedDeadline);          
        }
        if(taskLeaderId!=null){
            newTask.setTaskLeaderId(this.taskLeaderId);
        }
        
    }
}
