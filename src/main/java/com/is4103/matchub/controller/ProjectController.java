/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.exception.DeleteProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UpdateProjectException;
import com.is4103.matchub.service.ProjectService;
import com.is4103.matchub.vo.ProjectCreateVO;
import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    ResponseEntity<ProjectEntity> createNewProject(@Valid @RequestBody ProjectCreateVO vo)throws URISyntaxException{
        ProjectEntity createdProject = projectService.createProject(vo);
        if (createdProject== null){
            return ResponseEntity.notFound().build();
        }else {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdProject.getProjectId())
                    .toUri();
        
            return ResponseEntity.ok().body(createdProject);         
        }
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getProject")
    ResponseEntity<ProjectEntity> getProjectById(@RequestParam(value = "projectId", defaultValue = "")Long projectId){
        try {
            ProjectEntity project = projectService.retrieveProjectById(projectId);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "/updateProject")
    ResponseEntity<ProjectEntity> updateProjectById(@Valid @RequestBody ProjectCreateVO vo,
             @RequestParam(value = "updaterId", defaultValue = "") Long updaterId,  @RequestParam(value = "projectId", defaultValue = "") Long projectId){
        try {
            ProjectEntity project = projectService.updateProject(vo, updaterId, projectId);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch(UpdateProjectException e){
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), e);
        }
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteProject")
    ResponseEntity<Object> deleteProject(@RequestParam(value = "projectId",defaultValue = "") Long projectId,
             @RequestParam(value = "updaterId", defaultValue = "") Long updaterId){
        try {
            projectService.deleteProject(projectId, updaterId);
            return ResponseEntity.ok().build();
        } catch (DeleteProjectException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), e);
        }
    }
    
    
     
    
    
}
