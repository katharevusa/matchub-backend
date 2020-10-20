/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author longluqian
 */
@Data
public class UpdateLabelVO {

    Map<String, String> labelAndColour;

    
    Long taskId;
}
