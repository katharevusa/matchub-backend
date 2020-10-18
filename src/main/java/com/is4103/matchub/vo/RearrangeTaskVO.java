/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author longluqian
 */
@Data
public class RearrangeTaskVO {

    Map<Long, List<Long>> columnIdAndTaskIdSequence;
    
    @NotNull(message = "Kanban Board Id can not be null")
    Long kanbanBoardId;
    
    @NotNull(message = "Arranger Id can not be null")
    Long arrangerId;
}
