/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.PostNotFoundException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.PostEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.vo.PostVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostEntityRepository postEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Transactional
    @Override
    public PostEntity createPost(PostVO vo) {

        ProfileEntity profile = profileEntityRepository.findById(vo.getPostCreatorId())
                .orElseThrow(() -> new UserNotFoundException(vo.getPostCreatorId()));

        PostEntity newPost = new PostEntity();
        newPost.setTimeCreated(LocalDateTime.now());
        newPost.setPostCreator(profile);

        vo.updatePost(newPost);

        newPost = postEntityRepository.saveAndFlush(newPost);
        return newPost;
    }

    @Transactional
    @Override
    public PostEntity uploadPhotos(Long postId, MultipartFile[] photos) {
        PostEntity post = postEntityRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostId: " + postId + " cannot be found"));

        for (MultipartFile photo : photos) {
            String path = attachmentService.upload(photo);
            post.getPhotos().add(path);
        }

        post = postEntityRepository.saveAndFlush(post);
        return post;
    }

    @Override
    public PostEntity getPostById(Long postId) {
        PostEntity post = postEntityRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostId: " + postId + " cannot be found"));

        return post;
    }

    @Override
    public List<PostEntity> getPostsByAccountId(Long id) {
        /* use case: viewing profile page posts */
        ProfileEntity profile = profileEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        List<PostEntity> posts = profile.getPosts();
        return posts;
    }
    
//    public void deletePost() {
//        
//    }
}
