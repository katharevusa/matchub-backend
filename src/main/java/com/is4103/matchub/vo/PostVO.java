/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.PostEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author ngjin
 */
@Data 
public class PostVO {

    @NotNull(message = "Post Creator Id can not be null.")
//    @NotBlank(message = "Post Creator Id can not be blank.")
    private Long postCreatorId;

    @NotNull(message = "Post Content can not be null.")
    @NotBlank(message = "Post Content can not be blank.")
    private String content;

    public void updatePost(PostEntity post) {
        post.setContent(this.content);
    }
}
