/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.SurveyEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class SurveyVO {
    
    @NotNull
    private String name;
    
    private String description;

    @NotNull
    @FutureOrPresent
    private LocalDateTime openDate;

    @NotNull
    @FutureOrPresent
    private LocalDateTime closeDate;
    
    private Long surveyId;
    
    public void createSurvey(SurveyEntity surveyEntity){
        surveyEntity.setName(name);
        if(description!=null){
            surveyEntity.setDescription(description);
        }
        surveyEntity.setCreatedDate(LocalDateTime.now());
        surveyEntity.setOpenDate(openDate);
        surveyEntity.setCloseDate(closeDate);
        
    }
    
    public void updateSurvey(SurveyEntity surveyEntity){
        surveyEntity.setName(name);
        if(description!=null){
            surveyEntity.setDescription(description);
        }
        
        surveyEntity.setOpenDate(openDate);
        surveyEntity.setCloseDate(closeDate);
    }

}
