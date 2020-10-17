/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.google.cloud.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author longluqian
 */

@Data
public class ChannelMappingVO {
    List<String> admins = new ArrayList<>();
    
    Timestamp createdAt;
    
    String createdBy;
    
    String description;
    
    String id;
    
    List<String> members = new ArrayList<>();
    
    String name;
    
    String projectId;
    
    
    
    
    
    
}
