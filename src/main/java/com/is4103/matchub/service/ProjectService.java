/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.JoinRequestEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.exception.CompleteProjectException;
import com.is4103.matchub.exception.DeleteProjectException;
import com.is4103.matchub.exception.DownvoteProjectException;
import com.is4103.matchub.exception.FollowProjectException;
import com.is4103.matchub.exception.JoinProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.RevokeDownvoteException;
import com.is4103.matchub.exception.RevokeUpvoteException;
import com.is4103.matchub.exception.TerminateProjectException;
import com.is4103.matchub.exception.UnableToAddProjectOwnerException;
import com.is4103.matchub.exception.UnableToRemoveProjectOwnerException;
import com.is4103.matchub.exception.UpdateProjectException;
import com.is4103.matchub.exception.UpvoteProjectException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.vo.ProjectCreateVO;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author longluqian
 */
public interface ProjectService {

    public ProjectEntity createProject(ProjectCreateVO vo);

    public ProjectEntity retrieveProjectById(Long id) throws ProjectNotFoundException;

    public ProjectEntity updateProject(ProjectCreateVO vo, Long accountId, Long projectId) throws ProjectNotFoundException, UpdateProjectException;

    public void deleteProject(Long projectId, Long accountId) throws DeleteProjectException;

    public ProjectEntity createProject(ProjectEntity newProject, Long creatorId);

    public List<ProjectEntity> getJoinedProjects(Long profileId) throws UserNotFoundException;

    public List<ProjectEntity> getCreatedProjects(Long profileId) throws UserNotFoundException;

    public void terminateProject(Long projectId, Long profileId) throws TerminateProjectException;

    public Page<ProjectEntity> searchProjectByKeywords(String keyword, Pageable pageable);

    public Page<ProjectEntity> getLaunchedProjects(Pageable pageble);

    public Page<ProjectEntity> getAllProjects(Pageable pageble);

    public ProjectEntity setProjectProfilePic(Long projectId, String path) throws ProjectNotFoundException;

    public ProjectEntity uploadPhotos(Long projectId, MultipartFile[] photos) throws ProjectNotFoundException;

    public ProjectEntity uploadDocuments(Long projectId, MultipartFile[] documents) throws ProjectNotFoundException;

    public ProjectEntity upvoteProject(Long projectId, Long profileId) throws ProjectNotFoundException, UpvoteProjectException, UserNotFoundException;

    public ProjectEntity downvoteProject(Long projectId, Long profileId) throws ProjectNotFoundException, DownvoteProjectException;

    public ProjectEntity revokeUpvote(Long projectId, Long profileId) throws ProjectNotFoundException, UserNotFoundException, RevokeUpvoteException;

    public ProjectEntity revokeDownvote(Long projectId, Long profileId) throws ProjectNotFoundException, UserNotFoundException, RevokeDownvoteException;

    public ProjectEntity deleteDocuments(Long projectId, String[] docsToDelete) throws IOException, ProjectNotFoundException, UpdateProjectException;

    public ProjectEntity deletePhotos(Long projectId, String[] photoToDelete) throws ProjectNotFoundException, IOException, UpdateProjectException;

    public ProjectEntity deleteProjectProfilePic(Long projectId) throws ProjectNotFoundException, UpdateProjectException, IOException;

    public JoinRequestEntity createJoinRequest(Long projectId, Long profileId) throws ProjectNotFoundException, JoinProjectException;

    public Page<ProjectEntity> retrieveProjectBySDGIds(List<Long> sdgId, Pageable pageable) throws ProjectNotFoundException;

    public List<ProjectEntity> getOwnedProjects(Long userId);

    public List<ProjectEntity> getProjectsByListOfIds(List<Long> ids) throws ProjectNotFoundException;

    public void completeProject(Long projectId, Long profileId) throws CompleteProjectException, ProjectNotFoundException;

    public Page<ProjectEntity> projectGlobalSearch(String keyword, List<Long> sdgIds, List<Long> sdgTargetIds, String country, ProjectStatusEnum status, Pageable pageable);

    public void UnfollowProject(Long followerId, Long projectId) throws ProjectNotFoundException, UserNotFoundException, FollowProjectException;

    public List<ProjectEntity> getListOfFollowingProjectsByUserId(Long userId) throws UserNotFoundException;

    public List<ProfileEntity> getListOfFollowerByProjectId(Long projectId) throws ProjectNotFoundException;

    public ProjectEntity followProject(Long followerId, Long projectId) throws ProjectNotFoundException, UserNotFoundException, FollowProjectException;

    public Page<ProjectEntity> getFollowingProjectsByAccountId(Long accountId, Pageable pageable);

    public ProjectEntity addProjectOwner(Long projOwner, Long projOwnerToAdd, Long projectId) throws ProjectNotFoundException, ProjectNotFoundException, UnableToAddProjectOwnerException;

    public List<ProjectEntity> getSpotlightedProjects();

    public Page<ProjectEntity> getSpotlightedProjects(Pageable pageable);

    public ProjectEntity removeProjectOwner(Long editorId, Long projOwnerToRemoveId, Long projectId) throws UnableToRemoveProjectOwnerException, ProjectNotFoundException;
}
