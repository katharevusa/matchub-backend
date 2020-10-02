/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.NotificationEntity;
import com.is4103.matchub.enumeration.NotificationTypeEnum;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class NotificationVO {

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private LocalDateTime timestamp;

    @NotNull
    private Long notifiedUserId;

    @NotNull
    private NotificationTypeEnum type;
   

    private Long projectId;

    private Long taskId;

    private Long postId;

    public void createNotification(NotificationEntity entity) {
        entity.setTitle(this.title);
        entity.setContent(this.content);
        entity.setTimestamp(this.timestamp);
        entity.setNotifiedUserId(this.notifiedUserId);
        entity.setType(this.type);
        if(projectId != null){
            entity.setProjectId(this.projectId);
        }
        if(taskId != null){
            entity.setTaskId(taskId);
        }
        if(postId != null){
            entity.setPostId(this.postId);
        }

    }

}
