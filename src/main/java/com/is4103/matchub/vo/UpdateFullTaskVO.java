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
public class UpdateFullTaskVO {

    @NotNull(message = "Task Id can not be null")
    private Long taskId;

    private String taskTitle;

    private String taskDescription;

    private LocalDateTime expectedDeadline;

    private Long taskLeaderId;

    @NotNull(message = "Task creator Id can not be null")
    private Long taskCreatorOrEditorId;

    @NotNull(message = "Kanban Board Id can not be null")
    private Long kanbanboardId;

    List<Long> newTaskDoerList = new ArrayList<>();

    Map<String, String> labelAndColour = new HashMap<>();
    
    
    public void updateTask(UpdateTaskVO newVO){
        newVO.setTaskId(taskId);
        newVO.setTaskTitle(taskTitle);
        newVO.setTaskDescription(taskDescription);
        newVO.setTaskLeaderId(taskLeaderId);
        newVO.setExpectedDeadline(expectedDeadline);
        newVO.setTaskCreatorOrEditorId(taskCreatorOrEditorId);
        newVO.setKanbanboardId(kanbanboardId);
                
    }
}
