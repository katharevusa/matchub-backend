/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.TaskEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class TaskVO {
    
    
    
    private Long taskId;
    
    @NotNull(message = "Task title can not be null")
    private String taskTitle;


    
    private String taskDescription;
    
    
    private LocalDateTime expectedDeadline;

    
    //Key: label, Value = colour
    private Map<String, String> labelAndColour = new HashMap<>();
    
    private Long taskLeaderId;
    
    private Long taskColumnId;

    // need to further take care
    private List<Long> taskdoers = new ArrayList<>();
    
    @NotNull(message = "Task creator Id can not be null")
    private Long taskCreatorId;
    
    
    public void createTask(TaskEntity newTask){
        newTask.setTaskTitle(this.taskTitle);
        if(taskDescription!=null){
            newTask.setTaskDescription(this.taskDescription);
        }
        newTask.setTaskDescription(this.taskDescription);
        newTask.setCreatedTime(LocalDateTime.now());
        newTask.setTaskCreatorId(this.taskCreatorId);
        if(expectedDeadline!=null){
            newTask.setExpectedDeadline(this.expectedDeadline);          
        }
        if(taskLeaderId!=null){
            newTask.setTaskLeaderId(this.taskLeaderId);
        }
        
    }
    
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
