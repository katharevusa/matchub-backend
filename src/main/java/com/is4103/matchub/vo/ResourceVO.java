/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.ResourceEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */

@Data
public class ResourceVO {
    

    
    @NotNull(message = "Resource name can not be null")
    @NotBlank(message = "Resource name can not be blank")
    private String resourceName;

    
    @NotNull(message = "Resource description can not be null")
    @NotBlank(message = "Resource description can not be blank")
    private String resourceDescription;

    
    private List<String> uploadedFiles = new ArrayList<>();
    

    private LocalDateTime startTime;
    
    
    private LocalDateTime endTime;
    
   
    @NotNull(message = "Resource category can not be null")
    private Long resourceCategoryId;
    
    @NotNull(message = "Resource owner can not be null ")
    private Long resourceOwnerId;
    
    @NotNull(message = "Units can not be null ")
    private Integer units;
    
  
    private List<String> photos = new ArrayList<>();
    
    public void updateResource(ResourceEntity newResource){
        newResource.setResourceName(this.resourceName);
        newResource.setResourceDescription(this.resourceDescription);
        newResource.setUploadedFiles(this.uploadedFiles);
        newResource.setStartTime(this.startTime);
        newResource.setEndTime(this.endTime);
        newResource.setResourceCategoryId(this.resourceCategoryId);
        newResource.setResourceOwnerId(this.resourceOwnerId);
        newResource.setUnits(this.units);
        newResource.setPhotos(this.photos);
    }
   
    
}
