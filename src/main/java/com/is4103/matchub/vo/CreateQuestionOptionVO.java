/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class CreateQuestionOptionVO {
    
   
    @NotNull
    private String optionContent;
    
   
    @NotNull
    private Long questionId;
    
}
