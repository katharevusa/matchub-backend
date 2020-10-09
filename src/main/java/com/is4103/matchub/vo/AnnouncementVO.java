/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class AnnouncementVO {

    @NotNull
    private String title;

    @NotNull
    private String content;
  
    private Long projectId;

    private Long taskId;

    private Long postId;
    
    private Long creatorId;
    
    
    //******************** Need to take care *****************
    private List<Long> notifiedUserId = new ArrayList<>();

    public void createAnnouncement(AnnouncementEntity entity, AnnouncementTypeEnum type) {
        entity.setTitle(this.title);
        entity.setContent(this.content);
        entity.setTimestamp(LocalDateTime.now());

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
    
    public void createProjectPublicAnnouncement(AnnouncementEntity entity)throws NullPointerException{   
        entity.setTitle(this.title);
        entity.setContent(this.content);
        entity.setTimestamp(LocalDateTime.now());
        entity.setType(AnnouncementTypeEnum.PROJECT_PUBLIC_ANNOUNCEMENT);
        entity.setProjectId(this.projectId);
        entity.setCreatorId(this.creatorId);   
    }
    
    public void createProjectInternalAnnouncement(AnnouncementEntity entity)throws NullPointerException{   
        entity.setTitle(this.title);
        entity.setContent(this.content);
        entity.setTimestamp(LocalDateTime.now());
        entity.setType(AnnouncementTypeEnum.PROJECT_INTERNAL_ANNOUNCEMENT);
        entity.setProjectId(this.projectId);
        entity.setCreatorId(this.creatorId);   
    }
   

}
