/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.JoinRequestEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.exception.DownvoteProjectException;
import com.is4103.matchub.exception.JoinProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.RevokeDownvoteException;
import com.is4103.matchub.exception.RevokeUpvoteException;
import com.is4103.matchub.exception.UpvoteProjectException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.service.ProjectService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class ProjectExplorationController {

    @Autowired
    ProjectService projectService;
    
   
    //Search a list of Projects based on Keyword (keywords)
    @RequestMapping(method = RequestMethod.GET, value = "/searchProjectByKeywords")
    Page<ProjectEntity> searchProjectByKeyword(@RequestParam(value = "keywords", defaultValue = "") String keywords, Pageable pageable) {
        return projectService.searchProjectByKeywords(keywords, pageable);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getAllProjects")
    Page<ProjectEntity> getAllProjects(Pageable pageable) {
        return projectService.getAllProjects(pageable);
    }
    
     //get a list of launched projects 
    @RequestMapping(method = RequestMethod.GET, value = "/getLaunchedProjects")
    Page<ProjectEntity> getLaunchedProjects(Pageable pageable) {
        return projectService.getLaunchedProjects(pageable);
    }
    
    // get projects by list of SDGs
    @RequestMapping(method = RequestMethod.GET, value = "/retrieveProjectBySDGIds")
    public Page<ProjectEntity> retrieveProjectBySDGIds(@RequestParam(value ="sdgIds",required = true)List<Long> sdgIds, Pageable pageable) throws ProjectNotFoundException {
        return projectService.retrieveProjectBySDGIds(sdgIds, pageable);
    }

    //upvote a project
    @RequestMapping(method = RequestMethod.POST, value = "/upvoteProject")
    public ProjectEntity upvoteProject(@RequestParam(value = "projectId", defaultValue = "") Long projectId, @RequestParam(value = "userId") Long userId) throws ProjectNotFoundException, UpvoteProjectException {
        return projectService.upvoteProject(projectId, userId);

    }

    // downvote a project
    @RequestMapping(method = RequestMethod.POST, value = "/downvoteProject")
    public ProjectEntity downvoteProject(@RequestParam(value = "projectId", defaultValue = "") Long projectId, @RequestParam(value = "userId") Long userId) throws ProjectNotFoundException, DownvoteProjectException {
        return projectService.downvoteProject(projectId, userId);

    }

    //revoke an upvote of a project
    @RequestMapping(method = RequestMethod.POST, value = "/revokeUpvote")
    public ProjectEntity revokeUpvote(@RequestParam(value = "projectId", defaultValue = "") Long projectId, @RequestParam(value = "userId") Long userId) throws ProjectNotFoundException, RevokeUpvoteException, UserNotFoundException {
        return projectService.revokeUpvote(projectId, userId);

    }

    //revoke an downvote of a project
    @RequestMapping(method = RequestMethod.POST, value = "/revokeDownvote")
    public ProjectEntity revokeDownvote(@RequestParam(value = "projectId", defaultValue = "") Long projectId, @RequestParam(value = "userId") Long userId) throws ProjectNotFoundException, RevokeDownvoteException, UserNotFoundException {
        return projectService.revokeDownvote(projectId, userId);

    }

    //join a project 
    @RequestMapping(method = RequestMethod.POST, value = "/createJoinRequest")
    public JoinRequestEntity createJoinRequest(@RequestParam(value ="projectId",required = true) Long projectId,@RequestParam(value ="profileId",required = true) Long profileId) throws ProjectNotFoundException, JoinProjectException {
        return projectService.createJoinRequest(projectId, profileId);
    }

  

}
