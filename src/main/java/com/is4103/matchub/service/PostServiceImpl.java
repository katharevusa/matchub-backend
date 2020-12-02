/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.CommentEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.PostEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
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
import com.is4103.matchub.vo.CommentVO;
import com.is4103.matchub.vo.PostVO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementEntityRepository announcementEntityRepository;

    @Autowired
    private CommentEntityRepository commentEntityRepository;

    @Transactional
    @Override
    public PostEntity createPost(PostVO vo) throws UserNotFoundException {

        ProfileEntity profile = profileEntityRepository.findById(vo.getPostCreatorId())
                .orElseThrow(() -> new UserNotFoundException(vo.getPostCreatorId()));

        PostEntity newPost = new PostEntity();
        newPost.setTimeCreated(LocalDateTime.now());
        newPost.setPostCreator(profile);

        vo.updatePost(newPost);

        newPost = postEntityRepository.saveAndFlush(newPost);

        //set associations
        profile.getPosts().add(newPost);

        // create announcement (notify profile follower)
        List<ProfileEntity> follower = new ArrayList<>();

        for (Long p : profile.getFollowers()) {
            follower.add(profileEntityRepository.findById(p).get());
        }

        String profileName = "";
        if (profile instanceof IndividualEntity) {
            profileName = ((IndividualEntity) profile).getFirstName() + " " + ((IndividualEntity) profile).getLastName();
        } else if (profile instanceof OrganisationEntity) {
            profileName = ((OrganisationEntity) profile).getOrganizationName();
        }

        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTitle("New Post From Your Following User");
        announcementEntity.setContent("Hurry! Check out " + profileName + "'s new post!");
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setType(AnnouncementTypeEnum.NEW_POST);
        announcementEntity.setPostId(newPost.getPostId());
        announcementEntity.setNewFollowerAndNewPosterProfileId(profile.getAccountId());
        announcementEntity.setNewFollowerAndNewPosterUUID(profile.getUuid());
        // association
        announcementEntity.getNotifiedUsers().addAll(follower);
        for (ProfileEntity p : follower) {
            p.getAnnouncements().add(announcementEntity);
        }
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification         
        announcementService.createNormalNotification(announcementEntity);

        return newPost;
    }

    @Override
    public void createPostDataInit(PostVO vo) throws LikePostException {
        ProfileEntity profile = profileEntityRepository.findById(vo.getPostCreatorId())
                .orElseThrow(() -> new UserNotFoundException(vo.getPostCreatorId()));

        PostEntity newPost = new PostEntity();
        newPost.setTimeCreated(LocalDateTime.now());
        newPost.setPostCreator(profile);

        vo.updatePost(newPost);
        newPost.setLikes(20L);

        newPost = postEntityRepository.saveAndFlush(newPost);
        if (newPost.getPostId() == 1L) {
            CommentVO commentVO = new CommentVO();
            commentVO.setAccountId(3L);
            commentVO.setContent("Nice post!");

            newPost = addComment(commentVO, 1L);
            likeAPost(newPost.getPostId(), 4L);
        }

        //set associations
        profile.getPosts().add(newPost);

        profileEntityRepository.flush();

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
        post.getPhotos();
        return post;
    }

    @Override
    public Page<PostEntity> getPostsByAccountId(Long id, Pageable pageable) throws UserNotFoundException {
        /* use case: viewing profile page posts */
        System.err.println("Reach here");
        ProfileEntity profile = profileEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        System.err.println("Reach here 2");
        return postEntityRepository.getPostsByAccountId(id, pageable);
    }

    @Transactional
    @Override
    public PostEntity updatePost(Long postId, PostVO vo) throws PostNotFoundException, UpdatePostException {

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

            //delete the all photos from build/ folder first
            for (String photo : postToDelete.getPhotos()) {
                attachmentService.deleteFile(photo);
            }

            //remove from association
            ProfileEntity postCreator = postToDelete.getPostCreator();
            postCreator.getPosts().remove(postToDelete);

            //delete the post entity
            postEntityRepository.delete(postToDelete);

            List<PostEntity> sharedPosts = postEntityRepository.getAllSharedPostByOriginalPostId(postId);
            for (PostEntity p : sharedPosts) {
                p.setOriginalPostId(0L);
            }
        } else {
            throw new UnableToDeletePostException("Unable to delete post because account: " + postCreatorId
                    + " is not the owner of the post to be deleted.");
        }
    }

    @Override
    public PostEntity addComment(CommentVO newCommentVO, Long postId) throws PostNotFoundException {

        CommentEntity newComment = new CommentEntity();
        newCommentVO.createPostComment(newComment);
        PostEntity post = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Unable to find post"));

        newComment = commentEntityRepository.saveAndFlush(newComment);
        post.getListOfComments().add(newComment);
        post = postEntityRepository.saveAndFlush(post);

        ProfileEntity postOwner = post.getPostCreator();

        // notify user if n
        if (postOwner.getAnnouncementsSetting().get(AnnouncementTypeEnum.NEW_POST_COMMENT)) {
            ProfileEntity liker = profileEntityRepository.findById(newComment.getAccountId()).get();
            String commentor = "";
            if (liker instanceof IndividualEntity) {
                commentor = ((IndividualEntity) liker).getFirstName() + " " + ((IndividualEntity) liker).getLastName();
            } else if (liker instanceof OrganisationEntity) {
                commentor = ((OrganisationEntity) liker).getOrganizationName();
            }

            AnnouncementEntity announcementEntity = new AnnouncementEntity();
            announcementEntity.setTitle("A new comment from " + commentor + " for your post!");
            announcementEntity.setContent(post.getContent());
            announcementEntity.setTimestamp(LocalDateTime.now());
            announcementEntity.setType(AnnouncementTypeEnum.NEW_POST_COMMENT);
            announcementEntity.setPostId(postId);
            // association
            announcementEntity.getNotifiedUsers().add(post.getPostCreator());
            post.getPostCreator().getAnnouncements().add(announcementEntity);
            announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

            // create notification
            announcementService.createNormalNotification(announcementEntity);
        }
        return post;

    }

    @Override
    public PostEntity deleteComment(Long commentId, Long postId, Long deletorId) throws DeleteCommentException {

        PostEntity post = postEntityRepository.findById(postId).get();
        CommentEntity comment = commentEntityRepository.findById(commentId).get();
        if (!comment.getAccountId().equals(deletorId) && !post.getPostCreator().getAccountId().equals(deletorId)) {
            throw new DeleteCommentException("Only post owner and comment creator can delete comment for this post");
        }

        post.getListOfComments().remove(comment);
        commentEntityRepository.delete(comment);
        return postEntityRepository.saveAndFlush(post);
    }

    @Override
    public PostEntity likeAPost(Long postId, Long likerId) throws LikePostException {
        PostEntity post = postEntityRepository.findById(postId).get();
        if (post.getLikedUsersId().contains(likerId)) {
            throw new LikePostException("You have already liked this post");
        }
        Long oldLikes = post.getLikes();
        post.setLikes(oldLikes + 1);
        post.getLikedUsersId().add(likerId);
        post = postEntityRepository.saveAndFlush(post);

        // announcements
        ProfileEntity postOwner = post.getPostCreator();

        // notify user if n
        if (postOwner.getAnnouncementsSetting().get(AnnouncementTypeEnum.NEW_POST_LIKE)) {

            ProfileEntity liker = profileEntityRepository.findById(likerId).get();
            String likerName = "";
            if (liker instanceof IndividualEntity) {
                likerName = ((IndividualEntity) liker).getFirstName() + " " + ((IndividualEntity) liker).getLastName();
            } else if (liker instanceof OrganisationEntity) {
                likerName = ((OrganisationEntity) liker).getOrganizationName();
            }

            AnnouncementEntity announcementEntity = new AnnouncementEntity();
            announcementEntity.setTitle("A new like from " + likerName + " for your post!");
            announcementEntity.setContent(post.getContent());
            announcementEntity.setTimestamp(LocalDateTime.now());
            announcementEntity.setType(AnnouncementTypeEnum.NEW_POST_LIKE);
            announcementEntity.setPostId(postId);
            // association
            announcementEntity.getNotifiedUsers().add(post.getPostCreator());
            post.getPostCreator().getAnnouncements().add(announcementEntity);
            announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

            // create notification
            announcementService.createNormalNotification(announcementEntity);
        }
        return post;

    }

    @Override
    public PostEntity unLikeAPost(Long postId, Long likerId) throws LikePostException {
        PostEntity post = postEntityRepository.findById(postId).get();
        if (!post.getLikedUsersId().contains(likerId)) {
            throw new LikePostException("You have never liked this post");
        }
        Long oldLikes = post.getLikes();
        post.setLikes(oldLikes - 1);
        post.getLikedUsersId().remove(likerId);
        return postEntityRepository.saveAndFlush(post);

    }

    @Override
    public List<ProfileEntity> getListOfLikers(Long postId) {
        PostEntity post = postEntityRepository.findById(postId).get();
        List<ProfileEntity> listOfLikers = new ArrayList<>();
        for (Long id : post.getLikedUsersId()) {
            listOfLikers.add(profileEntityRepository.findById(id).get());
        }

        return listOfLikers;
    }

    @Override
    public List<PostEntity> getFollowingUserPosts(Long userId) {
        ProfileEntity user = profileEntityRepository.findById(userId).get();
        List<ProfileEntity> listOfFollowingUsers = new ArrayList<>();
        for (Long id : user.getFollowing()) {
            listOfFollowingUsers.add(profileEntityRepository.findById(id).get());
        }

        listOfFollowingUsers.add(user);
        List<PostEntity> posts = new ArrayList<>();

        for (ProfileEntity p : listOfFollowingUsers) {
            posts.addAll(p.getPosts());
        }

        Collections.sort(posts, (PostEntity o1, PostEntity o2) -> o1.getTimeCreated().compareTo(o2.getTimeCreated()));

        return posts;
    }

    @Override
    public PostEntity repost(Long originalPostId, PostVO vo) throws RepostException, UserNotFoundException {
        ProfileEntity profile = profileEntityRepository.findById(vo.getPostCreatorId())
                .orElseThrow(() -> new UserNotFoundException(vo.getPostCreatorId()));

        Optional<PostEntity> originalPostOpt = postEntityRepository.findById(originalPostId);
        // if previouus post is deleted, then cannot repost
        if (!originalPostOpt.isPresent()) {
            throw new RepostException("Sorry the original post is deleted, cannot repost");
        }

        PostEntity originalPost = originalPostOpt.get();

        PostEntity newPost = new PostEntity();
        newPost.setTimeCreated(LocalDateTime.now());
        newPost.setPostCreator(profile);
        newPost.setOriginalPostId(originalPostId);

        vo.updatePost(newPost);
        newPost = postEntityRepository.saveAndFlush(newPost);
        //set associations
        profile.getPosts().add(newPost);
        profileEntityRepository.flush();

        // notify previous post creator
        ProfileEntity postOwner = originalPost.getPostCreator();

        // notify user if n
        if (postOwner.getAnnouncementsSetting().get(AnnouncementTypeEnum.SHARE_POST)) {

            String reposterName = "";
            if (profile instanceof IndividualEntity) {
                reposterName = ((IndividualEntity) profile).getFirstName() + " " + ((IndividualEntity) profile).getLastName();
            } else if (profile instanceof OrganisationEntity) {
                reposterName = ((OrganisationEntity) profile).getOrganizationName();
            }

            AnnouncementEntity announcementEntity = new AnnouncementEntity();
            announcementEntity.setTitle(reposterName + " reposted your post : " + originalPost.getContent());
            announcementEntity.setContent(newPost.getContent());
            announcementEntity.setTimestamp(LocalDateTime.now());
            announcementEntity.setType(AnnouncementTypeEnum.SHARE_POST);
            announcementEntity.setPostId(newPost.getPostId());
            // association
            announcementEntity.getNotifiedUsers().add(originalPost.getPostCreator());
            originalPost.getPostCreator().getAnnouncements().add(announcementEntity);
            announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

            // create notification
            announcementService.createNormalNotification(announcementEntity);
        }
        return newPost;
    }
}
