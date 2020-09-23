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
import org.springframework.context.annotation.ComponentScan;
import com.is4103.matchub.service.PostService;

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

}
