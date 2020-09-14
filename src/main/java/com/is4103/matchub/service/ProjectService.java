/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.exception.DeleteProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.TerminateProjectException;
import com.is4103.matchub.exception.UpdateProjectException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.vo.ProjectCreateVO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */

public interface ProjectService {
    public ProjectEntity createProject(ProjectCreateVO vo);
    
    public ProjectEntity retrieveProjectById(Long id) throws ProjectNotFoundException;
    
    public ProjectEntity updateProject(ProjectCreateVO vo, Long accountId, Long projectId) throws ProjectNotFoundException, UpdateProjectException;
    
    public void deleteProject(Long projectId, Long accountId)throws DeleteProjectException;
    
    public ProjectEntity createProject(ProjectEntity newProject, Long creatorId);
     
    public List<ProjectEntity> getJoinedProjects(Long profileId) throws UserNotFoundException;
     
    public List<ProjectEntity> getCreatedProjects(Long profileId) throws UserNotFoundException;
    
     public void terminateProject(Long projectId, Long profileId) throws TerminateProjectException;
     
     public Page<ProjectEntity> searchProjectByKeywords(String keyword, Pageable pageable);
     
     public Page<ProjectEntity> getLaunchedProjects(Pageable pageble);
}
