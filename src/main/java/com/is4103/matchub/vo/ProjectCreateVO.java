/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class ProjectCreateVO {

   
    @NotNull(message = "Project title can not be null")
    @NotBlank(message = "Project title can not be blank")
    private String projectTitle;

   
    @NotNull(message = "Project description can not be null")
    @NotBlank(message = "Project description can not be blank")
    private String projectDescription;

   
    @NotNull(message = "Country can not be null")
    @NotBlank(message = "Country can not be blank")
    private String country;

    @FutureOrPresent
    @NotNull(message = "Project start time can not be null")
    private LocalDateTime startDate;

    
    @NotNull(message = "Project end time can not be null")
    @FutureOrPresent
    private LocalDateTime endDate;
    
    @NotNull
    private Long projCreatorId;
    
    private Set<String> photos = new HashSet<>();
    
    public void updateProject(ProjectEntity newProject){
        newProject.setProjectTitle(this.projectTitle);
        newProject.setProjectDescription(this.projectDescription);
        newProject.setCountry(this.country);
        newProject.setStartDate(this.startDate);
        newProject.setEndDate(this.endDate);
        newProject.setPhotos(this.photos);
        newProject.setProjCreatorId(projCreatorId);
        System.out.println("com.is4103.matchub.vo.ProjectCreateVO.updateProject(): "+newProject.getCountry());
        System.out.println("com.is4103.matchub.vo.ProjectCreateVO.updateProject(): "+this.country);
    }
    
}
