/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.entity.CommentEntity;
import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.exception.DeleteCommentException;
import com.is4103.matchub.exception.LikePostException;
import com.is4103.matchub.exception.PostNotFoundException;
import com.is4103.matchub.exception.RepostException;
import com.is4103.matchub.exception.UnableToDeletePostException;
import com.is4103.matchub.exception.UpdatePostException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.CommentEntityRepository;
import com.is4103.matchub.repository.PostEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.service.AnnouncementService;
import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.PostService;
import com.is4103.matchub.vo.CommentVO;
import com.is4103.matchub.vo.PostVO;
import java.io.IOException;
import javax.transaction.Transactional;
import org.hibernate.Hibernate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author longluqian
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostEntityRepository postEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementEntityRepository announcementEntityRepository;

    @Autowired
    private CommentEntityRepository commentEntityRepository;

    @Autowired
    private PostService postService;

    public PostServiceTest() {
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

    @Test
    public void testCreatePost() {
        PostVO postvo = new PostVO();
        postvo.setPostCreatorId(5L);
        postvo.setContent("Hello everyone!");

        PostEntity post = postService.createPost(postvo);
        PostEntity newPost = postService.getPostById(post.getPostId());
        Assert.assertTrue(newPost.getContent().equals("Hello everyone!"));

    }

    @Test(expected = UserNotFoundException.class)
    public void testCreatePostWithUserNotFoundException() {
        PostVO postvo = new PostVO();
        postvo.setPostCreatorId(20L);
        postvo.setContent("Hello everyone!");

        PostEntity post = postService.createPost(postvo);
    }

    @Test
    public void testGetPostById() {
        PostEntity post = postService.getPostById(1L);
        Assert.assertNotNull(post);
    }

    @Test(expected = PostNotFoundException.class)
    public void testGetPostByIdException() {
        PostEntity post = postService.getPostById(20L);

    }

    @Test
    public void testGetPostsByAccountId() {
        /* use case: viewing profile page posts */
        Page<PostEntity> postList = postService.getPostsByAccountId(5L, PageRequest.of(0, 5));
        Assert.assertTrue(postList.getTotalElements() != 0);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetPostsByAccountIdException() {
        Page<PostEntity> postList = postService.getPostsByAccountId(30L, PageRequest.of(0, 5));
    }

    @Test
    public void testUpdatePost() {
        PostVO postVO = new PostVO();
        postVO.setContent("Updated content");
        postVO.setPostCreatorId(5L);
        PostEntity post = postService.updatePost(7L, postVO);
        Assert.assertTrue(post.getContent().equals("Updated content"));
    }

    @Test(expected = PostNotFoundException.class)
    public void testUpdatePostSuccessfulPostNotFoundException() {
        PostVO postVO = new PostVO();
        postVO.setContent("Updated content");
        postVO.setPostCreatorId(5L);
        PostEntity post = postService.updatePost(20L, postVO);
    }

    @Test(expected = UpdatePostException.class)
    public void testUpdatePostSuccessfulUpdatePostException() {
        PostVO postVO = new PostVO();
        postVO.setContent("Updated content");
        postVO.setPostCreatorId(5L);
        PostEntity post = postService.updatePost(2L, postVO);
    }
// 

    @Test( )
    public void testDeletePost() {
        try {
            postService.deletePost(1L, 2L);
           Assert.assertTrue(!postEntityRepository.findById(1L).isPresent());
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }

    @Test(expected = PostNotFoundException.class)
    public void testDeletePostPostNotFoundException() {
        try {
            postService.deletePost(30L, 5L);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Test(expected = UnableToDeletePostException.class)
    public void testDeletePostUnableToDeletePostException() {
        try {
            postService.deletePost(1L, 3L);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testAddCommentTest() {
        CommentVO vo = new CommentVO();
        vo.setAccountId(3L);
        vo.setContent("Nice post!");
        int oldPostSize = postEntityRepository.findById(1L).get().getListOfComments().size();
        PostEntity post = postService.addComment(vo, 1L);
        Assert.assertTrue(post.getListOfComments().size() == (oldPostSize + 1));
    }

    @Test(expected = PostNotFoundException.class)
    public void testAddCommentTestPostNotFoundException() {
        CommentVO vo = new CommentVO();
        vo.setAccountId(3L);
        vo.setContent("Nice post!");
        PostEntity post = postService.addComment(vo, 100L);
    }

    @Test
    public void testDeleteComment() {
        try {
            int oldPostCommentsSize = postEntityRepository.findById(1L).get().getListOfComments().size();
            CommentEntity comment = commentEntityRepository.findById(1L).get();
            postService.deleteComment(comment.getCommentId(), 1L, 3L);
            int newPostCommentsSize = postEntityRepository.findById(1L).get().getListOfComments().size();
            Assert.assertTrue((oldPostCommentsSize - 1) == newPostCommentsSize);
        } catch (DeleteCommentException ex) {
            fail("Should not throw exception");
        }
    }

    @Test(expected = DeleteCommentException.class)
    public void testDeleteCommentDeleteCommentException() throws DeleteCommentException {

        postService.deleteComment(1L, 1L, 7L);

    }

    @Test
    public void testLikeAPost() throws LikePostException {
        PostEntity post = postEntityRepository.findById(1L).get();
        Long oldLikeNumber = post.getLikes();
        post = postService.likeAPost(1L, 5L);
        Assert.assertTrue(post.getLikes() == (oldLikeNumber + 1));
    }

    @Test(expected = LikePostException.class)
    public void testLikeAPostLikePostException() throws LikePostException {
        PostEntity post = postEntityRepository.findById(1L).get();
        post = postService.likeAPost(1L, 4L);
    }

    @Test
    public void testUnlikeAPost() throws LikePostException {
        PostEntity post = postEntityRepository.findById(1L).get();
        Long oldLikeNumber = post.getLikes();
        post = postService.unLikeAPost(1L, 4L);
        Assert.assertTrue(post.getLikes() == (oldLikeNumber - 1));
    }

    @Test(expected = LikePostException.class)
    public void testUnlikeAPostLikePostException() throws LikePostException {
        PostEntity post = postEntityRepository.findById(1L).get();
        Long oldLikeNumber = post.getLikes();
        post = postService.unLikeAPost(1L, 3L);
    }

    @Test
    public void testGetListOfLikers() {
        PostEntity post = postEntityRepository.findById(1L).get();
        Assert.assertTrue(post.getLikedUsersId().size() == postService.getListOfLikers(1L).size());
    }

    @Test
    public void testRepost() throws RepostException {
        PostVO postVO = new PostVO();
        postVO.setContent("repost");
        postVO.setPostCreatorId(4L);
        PostEntity post = postService.repost(1L, postVO);
        post = postEntityRepository.findById(post.getPostId()).get();
        Assert.assertTrue(post.getContent().equals("repost"));
    }

    @Test(expected = RepostException.class)
    public void testRepostRepostException() throws RepostException {
        PostVO postVO = new PostVO();
        postVO.setContent("repost");
        postVO.setPostCreatorId(4L);
        PostEntity post = postService.repost(30L, postVO);
    }

}
