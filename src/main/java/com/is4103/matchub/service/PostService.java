/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.vo.PostVO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
public interface PostService {

    PostEntity createPost(PostVO vo);

    PostEntity uploadPhotos(Long postId, MultipartFile[] photos);

    PostEntity getPostById(Long postId);

    List<PostEntity> getPostsByAccountId(Long id);
}
