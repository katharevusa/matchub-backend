/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.CommentEntity;
import com.is4103.matchub.enumeration.CommentTypeEnum;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;

/**
 *
 * @author longluqian
 */
public class CommentVO {
 
    @NotNull(message = "Comment content can not be null")
    private String content;
    
    @NotNull
    private Long accountId;
    
    public void createTaskComment(CommentEntity newComment){
        newComment.setContent(this.content);
        newComment.setAccountId(this.accountId);
        newComment.setTimeCreated(LocalDateTime.now());
        newComment.setCommentType(CommentTypeEnum.Task_Comment);
    }
    
    public void createPostComment(CommentEntity newComment){
        newComment.setContent(this.content);
        newComment.setAccountId(this.accountId);
        newComment.setTimeCreated(LocalDateTime.now());
        newComment.setCommentType(CommentTypeEnum.Post_Comment);
    }

}
