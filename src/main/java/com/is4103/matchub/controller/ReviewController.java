/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/authenticated")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @RequestMapping(method = RequestMethod.GET, value = "profilewall/reviewsReceived/{accountId}")
    Page<ReviewEntity> reviewsReceived(@PathVariable("accountId") Long accountId, Pageable pageable) {
        return reviewService.getReviewsReceivedByAccountId(accountId, pageable);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "profilewall/reviewsGiven/{accountId}")
    Page<ReviewEntity> reviewsGiven(@PathVariable("accountId") Long accountId, Pageable pageable) {
        return reviewService.getReviewsGivenByAccountId(accountId, pageable);
    }

}
