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
public class DeleteColumnVO {
    
    @NotNull(message = "the id of the column to be deleted can not be null")
    private Long deleteColumnId;
    
    @NotNull(message = "the id of the person who wants to delete the column")
    private Long deletorId;
    
    
    private Long transferredColumnId;
}
