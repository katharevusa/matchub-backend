/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.exception.DeleteProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.TerminateProjectException;
import com.is4103.matchub.exception.UpdateProjectException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.service.ProjectService;
import com.is4103.matchub.vo.ProjectCreateVO;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author longluqian
 */
@RestController
@RequestMapping("/authenticated")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @RequestMapping(method = RequestMethod.POST, value = "/createNewProject")
    ProjectEntity createNewProject(@Valid @RequestBody ProjectCreateVO vo) {
        return projectService.createProject(vo);
    }

    //Get a project based on the projectId
    @RequestMapping(method = RequestMethod.GET, value = "/getProject")
    ProjectEntity getProjectById(@RequestParam(value = "projectId", defaultValue = "") Long projectId) throws ProjectNotFoundException {
        return projectService.retrieveProjectById(projectId);
    }

    //Get a list of joined projects (profile id)
    @RequestMapping(method = RequestMethod.GET, value = "/getJoinedProjects")
    List<ProjectEntity> getJoinedProjects(@RequestParam(value = "profileId", defaultValue = "") Long profileId) throws UserNotFoundException {
        return projectService.getJoinedProjects(profileId);
    }

    //Get a list of self-created projects (profile id)
    @RequestMapping(method = RequestMethod.GET, value = "/getCreatedProjects")
    List<ProjectEntity> getCreatedProjects(@RequestParam(value = "profileId", defaultValue = "") Long profileId) throws UserNotFoundException {
        return projectService.getCreatedProjects(profileId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllProjects")
    Page<ProjectEntity> getAllProjects(Pageable pageable) {
        return projectService.getAllProjects(pageable);
    }

    //terminate a project ( project id, terminator id)
    @RequestMapping(method = RequestMethod.PUT, value = "/terminateProject")
    void terminateProject(@RequestParam(value = "projectId", defaultValue = "") Long projectId, @RequestParam(value = "profileId", defaultValue = "") Long profileId) throws TerminateProjectException {
        projectService.terminateProject(projectId, profileId);
    }

    //get a list of launched projects 
    @RequestMapping(method = RequestMethod.GET, value = "/getLaunchedProjects")
    Page<ProjectEntity> getLaunchedProjects(Pageable pageable) {
        return projectService.getLaunchedProjects(pageable);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateProject")
    ProjectEntity updateProjectById(@Valid @RequestBody ProjectCreateVO vo,
            @RequestParam(value = "updaterId", defaultValue = "") Long updaterId, @RequestParam(value = "projectId", defaultValue = "") Long projectId) throws ProjectNotFoundException, UpdateProjectException {

        return projectService.updateProject(vo, updaterId, projectId);

    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteProject")
    void deleteProject(
            @RequestParam(value = "projectId", defaultValue = "") Long projectId,
            @RequestParam(value = "updaterId", defaultValue = "") Long updaterId) throws DeleteProjectException {
        projectService.deleteProject(projectId, updaterId);

    }
    //Search a list of Projects based on Keywords (keywords)

    @RequestMapping(method = RequestMethod.GET, value = "/searchProjectByKeywords")
    Page<ProjectEntity> searchProjectByKeywords(@RequestParam(value = "keyword", defaultValue = "") String keyword, Pageable pageable) {
        return projectService.searchProjectByKeywords(keyword, pageable);
    }

    //get a list of projects based on SDGs: Filter projects based on SDGs
//    @RequestMapping(method = RequestMethod.GET, value = "/searchProjectBySDGs")
//    List<ProjectEntity> searchProjectBySDGs(@RequestParam (value = "SDGs", defaultValue = "")List<Long> sdgIds){
//        return projectService.searchProjectBySDGs(sdgIds);
//    }
//    
    // make a request to join project (project id, profile id)
    // upvote project (project id, profile id)
}
