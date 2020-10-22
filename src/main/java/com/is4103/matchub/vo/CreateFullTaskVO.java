/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

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
public class CreateFullTaskVO {
    
    @NotNull(message = "Task title can not be null")
    private String taskTitle;

  
    private String taskDescription;
    
    
    private LocalDateTime expectedDeadline;

    
    private Long taskLeaderId;
    
    @NotNull(message = "Task Column Id can not be null")
    private Long taskColumnId;

    @NotNull(message = "Task creator Id can not be null")
    private Long taskCreatorOrEditorId;
    
    @NotNull(message = "Kanban Board Id can not be null")
    private Long kanbanboardId;
    
     Map<String, String> labelAndColour = new HashMap<>();
     
     List<Long> newTaskDoerList= new ArrayList<>();;
}
