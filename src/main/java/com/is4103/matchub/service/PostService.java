/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.DeleteCommentException;
import com.is4103.matchub.exception.LikePostException;
import com.is4103.matchub.exception.PostNotFoundException;
import com.is4103.matchub.exception.RepostException;
import com.is4103.matchub.vo.CommentVO;
import com.is4103.matchub.vo.PostVO;
import java.io.IOException;
import java.util.List;
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

    PostEntity updatePost(Long postId, PostVO vo);

    PostEntity deletePhotos(Long postId, String[] photosToDelete) throws IOException;

    void deletePost(Long postId, Long postCreatorId) throws IOException;

    public PostEntity deleteComment(Long commentId, Long postId, Long deletorId) throws DeleteCommentException;

    public PostEntity addComment(CommentVO newCommentVO, Long postId)throws PostNotFoundException;

    public PostEntity likeAPost(Long postId, Long likerId) throws LikePostException;

    public PostEntity unLikeAPost(Long postId, Long likerId) throws LikePostException;

    public List<ProfileEntity> getListOfLikers(Long postId);
    
    public List<PostEntity> getFollowingUserPosts(Long userId);
    
    public PostEntity repost(Long previousPostId, PostVO vo)throws RepostException;
    
    public void createPostDataInit(PostVO vo)throws LikePostException;

   
}
