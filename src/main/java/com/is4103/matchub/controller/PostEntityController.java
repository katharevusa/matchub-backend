/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.PostEntity;
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
import com.is4103.matchub.vo.DeleteFilesVO;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/authenticated")
public class PostEntityController {

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
    Page<PostEntity> getPostsByAccountId(@PathVariable("accountId") Long postId, Pageable pageable) {
        return postService.getPostsByAccountId(postId, pageable);
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
}
