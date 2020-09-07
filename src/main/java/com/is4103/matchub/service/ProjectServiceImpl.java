/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.exception.DeleteProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UpdateProjectException;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.vo.ProjectCreateVO;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class ProjectServiceImpl implements ProjectService{
    @Autowired
    private ProjectEntityRepository projectEntityRepository;
    
    @Autowired
    private ProfileEntityRepository profileEntityRepository;
    
    @Override
    public ProjectEntity createProject(ProjectCreateVO vo) {
        ProjectEntity newProject = new ProjectEntity();
        vo.updateProject(newProject);
//        newProject = projectEntityRepository.save(newProject);
        Optional<ProfileEntity> profile = profileEntityRepository.findById(newProject.getProjCreatorId());
        profile.get().getProjectsOwned().add(newProject);
        newProject.getProjectOwners().add(profile.get());
        newProject = projectEntityRepository.save(newProject);
        return newProject;
        
    }
    
    @Override
    public ProjectEntity createProject(ProjectEntity newProject, Long creatorId){
        newProject.setProjCreatorId(creatorId);
        Optional<ProfileEntity> profile = profileEntityRepository.findById(creatorId);
        profile.get().getProjectsOwned().add(newProject);
        newProject.getProjectOwners().add(profile.get());
        newProject = projectEntityRepository.save(newProject);
        return newProject;
        
    }
    
    @Override
    public ProjectEntity retrieveProjectById(Long id) throws ProjectNotFoundException{
        Optional<ProjectEntity> optionalProjectEntity = projectEntityRepository.findById(id);
        if(optionalProjectEntity.isPresent()){
            return optionalProjectEntity.get();
        }else{
            throw new ProjectNotFoundException("Project with id "+id+"does not exist");
        } 
    }
    
    
    
    @Override
    public ProjectEntity updateProject(ProjectCreateVO vo, Long accountId, Long projectId) throws ProjectNotFoundException, UpdateProjectException{
        Optional<ProjectEntity> optionalProjectEntity = projectEntityRepository.findById(projectId);
       if(optionalProjectEntity.isPresent()){
           ProjectEntity oldProject = optionalProjectEntity.get();
           Optional<ProfileEntity> updater = profileEntityRepository.findById(accountId);
           
           if(updater.isPresent()){
              for(ProfileEntity p: oldProject.getProjectOwners()){
               if (p.getAccountId().equals(updater.get().getAccountId())){
               oldProject.setProjectTitle(vo.getProjectTitle());
               oldProject.setProjectDescription(vo.getProjectDescription());
               oldProject.setCountry(vo.getCountry());
               oldProject.setStartDate(vo.getStartDate());
               oldProject.setEndDate(vo.getStartDate());
               oldProject.setPhotos(vo.getPhotos());
               return oldProject;
               }  
              }
               throw new UpdateProjectException("You do not have the right to edit this project");
           }else{
               throw new UpdateProjectException("Updater does not exist");
           }
           
           
        }else{
            throw new ProjectNotFoundException("Project with id "+projectId+" does not exist");
        }
    }
    
    
    public void deleteProject(Long projectId, Long accountId)throws DeleteProjectException{
        Optional<ProjectEntity> optionalProjectEntity = projectEntityRepository.findById(projectId);
        if(optionalProjectEntity.isPresent() ){
           ProjectEntity oldProject = optionalProjectEntity.get();
           if(Objects.equals(oldProject.getProjCreatorId(), accountId)){
               if(oldProject.getProjStatus().equals(ProjectStatusEnum.ON_HOLD)){
                  for(ProfileEntity p:oldProject.getProjectOwners()){
                      p.getProjectsOwned().remove(oldProject);
                  }
                  oldProject.setProjectOwners(new ArrayList<>() );
                   projectEntityRepository.delete(oldProject);
                   
               }else{
                   throw  new DeleteProjectException("Delete project exception: The project has either in progress or completed, can not be deleted");
               }
           
            }else{
               throw new DeleteProjectException("Delete project exception: Only project creator can delete project");
            }      
        }    
 
        
        
        
    }
    
}
