/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.vo.PostVO;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
public interface PostService {

    PostEntity createPost(PostVO vo);

    PostEntity uploadPhotos(Long postId, MultipartFile[] photos);

    PostEntity getPostById(Long postId);

    Page<PostEntity> getPostsByAccountId(Long id, Pageable pageable);

    void deletePost(Long postId, Long postCreatorId) throws IOException;
}
