/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.KanbanBoardEntity;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class KanbanBoardVO {
    
   
    @NotNull(message = "KanbanBoard channelUid can not be null")
    private String channelUid;
    

    @NotNull(message = "KanbanBoard projectId can not be null")
    private Long projectId;
    
    public void createNewKanbanBoard(KanbanBoardEntity newKanbanBoard){
        newKanbanBoard.setChannelUid(this.channelUid);
        newKanbanBoard.setProjectId(this.projectId);

    }
}
