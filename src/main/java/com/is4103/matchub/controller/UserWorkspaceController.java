/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.DeleteCommentException;
import com.is4103.matchub.exception.LikePostException;
import com.is4103.matchub.exception.RepostException;
import com.is4103.matchub.vo.PostVO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.is4103.matchub.service.PostService;
import com.is4103.matchub.vo.CommentVO;
import com.is4103.matchub.vo.DeleteFilesVO;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/authenticated")
public class UserWorkspaceController {

    @Autowired
    PostService postService;

    @RequestMapping(method = RequestMethod.POST, value = "/createPost")
    PostEntity createPost(@Valid @RequestBody PostVO createVO) {
        return postService.createPost(createVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "post/uploadPhotos/{postId}")
    public PostEntity uploadPhotos(@RequestParam(value = "photos") MultipartFile[] photos, @PathVariable("postId") Long postId) {
        return postService.uploadPhotos(postId, photos);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPost/{postId}")
    PostEntity getPost(@PathVariable("postId") Long postId) {
        return postService.getPostById(postId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPostsByAccountId/{accountId}")
    Page<PostEntity> getPostsByAccountId(@PathVariable("accountId") Long accountId, Pageable pageable) {
        return postService.getPostsByAccountId(accountId, pageable);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updatePost/{postId}")
    PostEntity updatePost(@PathVariable("postId") Long postId, @Valid @RequestBody PostVO vo) {
        return postService.updatePost(postId, vo);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "post/deletePhotos/{postId}")
    PostEntity deletePhotos(@PathVariable("postId") Long postId, @Valid @RequestBody DeleteFilesVO vo) throws IOException {
        return postService.deletePhotos(postId, vo.getFileNamesWithExtension());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deletePost/{postId}/{postCreatorId}")
    void deletePost(@PathVariable("postId") Long postId, @PathVariable("postCreatorId") Long postCreatorId) throws IOException {
        postService.deletePost(postId, postCreatorId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteComment")
    public void deleteComment(@RequestParam(value = "commentId", required = true) Long commentId,
            @RequestParam(value = "postId", required = true) Long postId,
            @RequestParam(value = "deletorId", required = true) Long deletorId) throws DeleteCommentException {
        postService.deleteComment(commentId, postId, deletorId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addComment")
    public PostEntity addComment(@RequestBody @Valid CommentVO newCommentVO,
            @RequestParam(value = "postId", required = true) Long postId) {
        return postService.addComment(newCommentVO, postId);

    }

    @RequestMapping(method = RequestMethod.PUT, value = "/likeAPost")
    public PostEntity likeAPost(@RequestParam(value = "postId", required = true)Long postId,
                                @RequestParam(value = "likerId", required = true)Long likerId) throws LikePostException {
        return postService.likeAPost(postId, likerId);

    }

    @RequestMapping(method = RequestMethod.PUT, value = "/unLikeAPost")
    public PostEntity unLikeAPost(@RequestParam(value = "postId", required = true)Long postId,
                                  @RequestParam(value = "likerId", required = true)Long likerId) throws LikePostException {
        return postService.unLikeAPost(postId, likerId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getListOfLikers")
    public List<ProfileEntity> getListOfLikers(@RequestParam(value = "postId", required = true)Long postId) {
        return postService.getListOfLikers(postId);
    }
    
    
    @RequestMapping(method = RequestMethod.GET, value = "/getFollowingProjectAnnouncements")
    public List<AnnouncementEntity> getFollowingProjectAnnouncements(@RequestParam(value = "userId", required = true)Long userId){
        return postService.getFollowingProjectAnnouncements(userId);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getOwnedProjectAnnouncements")
    public List<AnnouncementEntity> getOwnedProjectAnnouncements(@RequestParam(value = "userId", required = true)Long userId){
        return postService.getOwnedProjectAnnouncements(userId);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getJoinedProjectAnnouncements")
    public List<AnnouncementEntity> getJoinedProjectAnnouncements(@RequestParam(value = "userId", required = true)Long userId){
        return postService.getJoinedProjectAnnouncements(userId);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/repost")
    public PostEntity repost(@RequestParam(value = "originalPostId", required = true)Long originalPostId,@Valid @RequestBody PostVO vo)throws RepostException{
        return postService.repost(originalPostId, vo);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getFollowingUserPosts")
    public List<PostEntity> getFollowingUserPosts(@RequestParam(value = "userId", required = true)Long userId){
        return postService.getFollowingUserPosts(userId);
    }
}
