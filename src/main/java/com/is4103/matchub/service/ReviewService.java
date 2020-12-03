/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UnableToCreateReviewException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.vo.ReviewCreateVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author ngjin
 */
public interface ReviewService {

    Page<ReviewEntity> getReviewsReceivedByAccountId(Long id, Pageable pageable) throws UserNotFoundException;

    Page<ReviewEntity> getReviewsGivenByAccountId(Long id, Pageable pageable) throws UserNotFoundException;

    ReviewEntity createReview(ReviewCreateVO vo) throws UserNotFoundException,UnableToCreateReviewException,ProjectNotFoundException;
}
