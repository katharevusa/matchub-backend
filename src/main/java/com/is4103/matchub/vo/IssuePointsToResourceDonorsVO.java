/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author ngjin
 */
@Data
public class IssuePointsToResourceDonorsVO {

    //Long is for resourceId
    //Integer is for additional points 
    //only add in the resources that are awarded extra points, 
    //do not need to add in resources if they are not awarded additional points
    private Map<Long, Integer> hashmap = new HashMap<>();

    @NotNull(message = "projectId can not be null.")
    private Long projectId;

}
