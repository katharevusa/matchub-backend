/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.exception.CompleteProjectException;
import com.is4103.matchub.exception.DeleteProjectException;
import com.is4103.matchub.exception.DownvoteProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.TerminateProjectException;
import com.is4103.matchub.exception.UnableToRemoveProjectOwnerException;
import com.is4103.matchub.exception.UpdateProjectException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.ProjectService;
import com.is4103.matchub.vo.ProjectCreateVO;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author longluqian
 */
@RestController
@RequestMapping("/authenticated")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Autowired
    AttachmentService attachmentService;

    //Create new project does not contain the photos upload, call separate photo upload method
    @RequestMapping(method = RequestMethod.POST, value = "/createNewProject")
    ProjectEntity createNewProject(@Valid @RequestBody ProjectCreateVO vo) {
        return projectService.createProject(vo);
    }

    //Update project does not contain the photos upload, call separate photo upload method
    @RequestMapping(method = RequestMethod.PUT, value = "/updateProject")
    ProjectEntity updateProjectById(@Valid @RequestBody ProjectCreateVO vo,
            @RequestParam(value = "updaterId", required = true) Long updaterId, @RequestParam(value = "projectId", required = true) Long projectId) throws ProjectNotFoundException, UpdateProjectException {

        return projectService.updateProject(vo, updaterId, projectId);

    }

    //Get a project based on the projectId
    @RequestMapping(method = RequestMethod.GET, value = "/getProject")
    ProjectEntity getProjectById(@RequestParam(value = "projectId", required = true) Long projectId) throws ProjectNotFoundException {
        return projectService.retrieveProjectById(projectId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getFollowingProjectsByAccountId/{accountId}")
    Page<ProjectEntity> getFollowingProjectsByAccountId(@PathVariable("accountId") Long accountId, Pageable pageable) {
        return projectService.getFollowingProjectsByAccountId(accountId, pageable);
    }

    //Get a list of joined projects (profile id)
    @RequestMapping(method = RequestMethod.GET, value = "/getJoinedProjects")
    List<ProjectEntity> getJoinedProjects(@RequestParam(value = "profileId", required = true) Long profileId) throws UserNotFoundException {
        return projectService.getJoinedProjects(profileId);
    }

    //Get a list of self-created projects (profile id)
    @RequestMapping(method = RequestMethod.GET, value = "/getCreatedProjects")
    List<ProjectEntity> getCreatedProjects(@RequestParam(value = "profileId", required = true) Long profileId) throws UserNotFoundException {
        return projectService.getCreatedProjects(profileId);
    }

    //terminate a project ( project id, terminator id)
    @RequestMapping(method = RequestMethod.PUT, value = "/terminateProject")
    void terminateProject(@RequestParam(value = "projectId", required = true) Long projectId, @RequestParam(value = "profileId", required = true) Long profileId) throws TerminateProjectException {
        projectService.terminateProject(projectId, profileId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/completeProject")
    void completeProject(@RequestParam(value = "projectId", required = true) Long projectId, @RequestParam(value = "profileId", required = true) Long profileId) throws CompleteProjectException, ProjectNotFoundException {
        projectService.completeProject(projectId, profileId);
    }

    // get a list of owned projects
    @RequestMapping(method = RequestMethod.GET, value = "/getOwnedProjects")
    List<ProjectEntity> getOwnedProjects(@RequestParam(value = "userId", required = true) Long userId) {
        return projectService.getOwnedProjects(userId);
    }

    // get a list of projects by list of project ids
    @RequestMapping(method = RequestMethod.GET, value = "/getProjectsByListOfIds")
    List<ProjectEntity> getProjectsByListOfIds(@RequestParam(value = "ids", required = true) List<Long> ids) throws ProjectNotFoundException {
        return projectService.getProjectsByListOfIds(ids);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteProject")
    void deleteProject(
            @RequestParam(value = "projectId", required = true) Long projectId,
            @RequestParam(value = "updaterId", required = true) Long updaterId) throws DeleteProjectException {
        projectService.deleteProject(projectId, updaterId);

    }

    //upload projectProfilePic
    @RequestMapping(method = RequestMethod.POST, value = "/updateProject/updateProjectProfilePic")
    public ProjectEntity updateProjectProfilePic(@RequestParam(value = "profilePic") MultipartFile profilePic, @RequestParam("projectId") Long projectId) throws ProjectNotFoundException {

        String filePath = attachmentService.upload(profilePic);

        return projectService.setProjectProfilePic(projectId, filePath);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/updateProject/deleteProfilePic")
    public ProjectEntity deleteProjectProfilePic(@RequestParam(value = "projectId") Long projectId) throws ProjectNotFoundException, UpdateProjectException, IOException {
        return projectService.deleteProjectProfilePic(projectId);
    }

    //upload list of photos
    @RequestMapping(method = RequestMethod.POST, value = "/updateProject/uploadPhotos")
    public ProjectEntity uploadPhotos(@RequestParam(value = "photos") MultipartFile[] photos, @RequestParam("projectId") Long projectId) throws ProjectNotFoundException {
        return projectService.uploadPhotos(projectId, photos);
    }

    // pass photo full path
    @RequestMapping(method = RequestMethod.DELETE, value = "/updateProject/deletePhotos")
    public ProjectEntity deletePhotos(@RequestParam(value = "projectId") Long projectId, @RequestParam(value = "photosToDelete") String[] photosToDelete) throws ProjectNotFoundException, UpdateProjectException, IOException {
        return projectService.deletePhotos(projectId, photosToDelete);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateProject/uploadDocuments")
    public ProjectEntity uploadDocuments(@RequestParam(value = "documents") MultipartFile[] documents, @RequestParam("projectId") Long projectId) throws ProjectNotFoundException {
        return projectService.uploadDocuments(projectId, documents);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/updateProject/deleteDocuments")
    public ProjectEntity deleteDocuments(@RequestParam(value = "projectId") Long projectId, @RequestParam(value = "docsToDelete") String[] docsToDelete) throws ProjectNotFoundException, UpdateProjectException, IOException {
        return projectService.deleteDocuments(projectId, docsToDelete);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/addProjectOwner")
    public ProjectEntity addProjectOwner(@RequestParam(value = "projOwnerId") Long projOwnerId, @RequestParam(value = "projOwnerToAddId") Long projOwnerToAddId, @RequestParam(value = "projectId") Long projectId) throws ProjectNotFoundException {
        return projectService.addProjectOwner(projOwnerId, projOwnerToAddId, projectId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/removeProjectOwner")
    public ProjectEntity removeProjectOwner(@RequestParam(value = "editorId") Long editorId,
            @RequestParam(value = "projOwnerToRemoveId") Long projOwnerToRemoveId,
            @RequestParam(value = "projectId") Long projectId) throws UnableToRemoveProjectOwnerException, ProjectNotFoundException {
        return projectService.removeProjectOwner(editorId, projOwnerToRemoveId, projectId);
    }

}
