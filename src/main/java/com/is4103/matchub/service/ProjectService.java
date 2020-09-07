/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.exception.DeleteProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UpdateProjectException;
import com.is4103.matchub.vo.ProjectCreateVO;
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
     
    
}
