/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.PostNotFoundException;
import com.is4103.matchub.exception.UnableToDeletePostException;
import com.is4103.matchub.exception.UpdatePostException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.PostEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.vo.PostVO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<PostEntity> getPostsByAccountId(Long id, Pageable pageable) {
        /* use case: viewing profile page posts */
        ProfileEntity profile = profileEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return postEntityRepository.getPostsByAccountId(id, pageable);
    }

    @Transactional
    @Override
    public PostEntity updatePost(Long postId, PostVO vo) {

        ProfileEntity profile = profileEntityRepository.findById(vo.getPostCreatorId())
                .orElseThrow(() -> new UserNotFoundException(vo.getPostCreatorId()));

        PostEntity postToEdit = postEntityRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostId: " + postId + " cannot be found"));

        //check if the postToEdit belongs to the postCreator
        if (postToEdit.getPostCreator().getAccountId().equals(profile.getAccountId())) {
            vo.updatePost(postToEdit);

            postToEdit = postEntityRepository.saveAndFlush(postToEdit);
            return postToEdit;
        } else {
            throw new UpdatePostException("Unable to update post because account: " + vo.getPostCreatorId()
                    + " is not the owner of the post to be updated.");
        }
    }

    @Transactional
    @Override
    public PostEntity deletePhotos(Long postId, String[] photosToDelete) throws IOException {

        PostEntity post = postEntityRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostId: " + postId + " cannot be found"));

        for (String photo : photosToDelete) {
            if (!post.getPhotos().contains(photo)) {
                throw new UpdatePostException("Unable to delete photo from post: " + photo + " cannot be found.");
            }
        }

        for (String photo : photosToDelete) {
            attachmentService.deleteFile(photo);
            post.getPhotos().remove(photo);
        }

        post = postEntityRepository.saveAndFlush(post);
        return post;
    }

    @Transactional
    @Override
    public void deletePost(Long postId, Long postCreatorId) throws IOException {
        PostEntity postToDelete = postEntityRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("PostId: " + postId + " cannot be found"));

        if (postToDelete.getPostCreator().getAccountId().equals(postCreatorId)) {

            if (postToDelete.getOriginalPostId() != null || postToDelete.getPreviousPostId() != null) {
                //Post cannot be deleted if it is being reshared
                throw new UnableToDeletePostException("Unable to delete post because post is reshared.");
            }

            //delete the all photos from build/ folder first
            for (String photo : postToDelete.getPhotos()) {
                attachmentService.deleteFile(photo);
            }
            //delete the post entity
            postEntityRepository.delete(postToDelete);
        } else {
            throw new UnableToDeletePostException("Unable to delete post because account: " + postCreatorId
                    + " is not the owner of the post to be deleted.");
        }
    }
}
