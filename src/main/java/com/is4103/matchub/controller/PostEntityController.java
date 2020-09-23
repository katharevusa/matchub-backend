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
    PostService postEntityService;

    @RequestMapping(method = RequestMethod.POST, value = "/createPost")
    PostEntity createPost(@Valid @RequestBody PostVO createVO) {
        return postEntityService.createPost(createVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "post/uploadPhotos")
    public PostEntity uploadPhotos(@RequestParam(value = "photos") MultipartFile[] photos, @RequestParam("postId") Long postId) {
        return postEntityService.uploadPhotos(postId, photos);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPost/{id}")
    PostEntity getPost(@PathVariable Long id) {
        return postEntityService.getPostById(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPostsByAccountId/{id}")
    Page<PostEntity> getPostsByAccountId(@PathVariable Long id, Pageable pageable) {
        return postEntityService.getPostsByAccountId(id, pageable);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deletePost/{postId}/{postCreatorId}")
    void deletePost(@PathVariable("postId") Long postId, @PathVariable("postCreatorId") Long postCreatorId) throws IOException {
        postEntityService.deletePost(postId, postCreatorId);
    }
}
