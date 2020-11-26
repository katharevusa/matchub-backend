/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.QuestionEntity;
import com.is4103.matchub.enumeration.QuestionTypeEnum;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class UpdateQuestionVO {
    
       @NotNull
    private String questionContent;

   
    private Map<Long, Long> optionToQuestion;
    
    @NotNull
    private QuestionTypeEnum questionType;
    
    
    private Long surveyId;
    
    
    private Long questionId;
    public void updateQuestion(QuestionEntity question){
        question.setQuestion(questionContent);
        question.setQuestionType(questionType);  
    }
    
}
