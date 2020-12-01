/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UnableToCreateReviewException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.service.ReviewServiceImpl;
import com.is4103.matchub.vo.ReviewCreateVO;
import java.math.BigDecimal;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.domain.Page;

/**
 *
 * @author kaikai
 */
@RunWith(SpringRunner.class)

@SpringBootTest
@Transactional
public class ReviewServiceTest {

    public ReviewServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

 
    @Autowired
    private ReviewServiceImpl reviewServiceImpl;
    ReviewEntity testReview;


    @Test
    
    public void testCreateReview() {
        ReviewCreateVO reviewVO = new ReviewCreateVO();
        
        reviewVO.setContent("testing review content");
        reviewVO.setProjectId(1l);
        reviewVO.setRating(BigDecimal.valueOf(3));
        reviewVO.setReviewReceiverId(5l);
        reviewVO.setReviewerId(9l);
        try {
            testReview = reviewServiceImpl.createReview(reviewVO);
            Assert.assertTrue(testReview.getReviewId() != null);
        } catch (ProjectNotFoundException ex) {
            ex.printStackTrace();
        }
        
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testCreateReviewProjectNotFound() throws ProjectNotFoundException{
        ReviewCreateVO reviewVO = new ReviewCreateVO();
        reviewVO.setContent("testing review content");
        reviewVO.setProjectId(20l);
        reviewVO.setRating(BigDecimal.valueOf(3));
        reviewVO.setReviewReceiverId(5l);
        reviewVO.setReviewerId(7l);
      try {
            testReview = reviewServiceImpl.createReview(reviewVO);
        } catch (UnableToCreateReviewException ex ) {
             ex.printStackTrace();
        }
    
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateReviewUserNotFound() {
        ReviewCreateVO reviewVO = new ReviewCreateVO();
        reviewVO.setContent("testing review content");
        reviewVO.setProjectId(2l);
        reviewVO.setRating(BigDecimal.valueOf(3));
        reviewVO.setReviewReceiverId(89l);
        reviewVO.setReviewerId(99l);
        try {
            testReview = reviewServiceImpl.createReview(reviewVO);
        } catch (ProjectNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Test(expected = UnableToCreateReviewException.class)
    public void testCreateReviewUnableToCreateReview() {
        ReviewCreateVO reviewVO = new ReviewCreateVO();
        reviewVO.setContent("testing review content");
        reviewVO.setProjectId(2l);
        reviewVO.setRating(BigDecimal.valueOf(3));
        reviewVO.setReviewReceiverId(5l);
        reviewVO.setReviewerId(5l);
        try {
            testReview = reviewServiceImpl.createReview(reviewVO);
        } catch (ProjectNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testGetReivewsReceivedByAccountId() {
        Page<ReviewEntity> reviews = reviewServiceImpl.getReviewsGivenByAccountId(5l, PageRequest.of(0, 5));
        Assert.assertTrue(reviews.getTotalElements() != 0l);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetReivewsReceivedByAccountIdUserNotFound() {
        Page<ReviewEntity> reviews = reviewServiceImpl.getReviewsGivenByAccountId(30l, PageRequest.of(0, 5));

    }

    @Test
    public void testGetReviewsGivenByAccountId() {
        Page<ReviewEntity> reviews = reviewServiceImpl.getReviewsGivenByAccountId(5l, PageRequest.of(0, 5));
        Assert.assertTrue(reviews.getTotalElements() != 0l);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetReviewsGivenByAccountIdUserNotFound() {
        Page<ReviewEntity> reviews = reviewServiceImpl.getReviewsGivenByAccountId(30l, PageRequest.of(0, 5));

    }

}
