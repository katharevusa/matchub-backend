/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.QuestionOptionEntity;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class CreateQuestionResponseVO {
    
    // list of question option
    
    public List<QuestionOptionEntity> selectedOptions = new ArrayList<>();
    // question id
    @NotNull
    public Long questionId;
    
    // response input
    public String input;
    //
    
}
