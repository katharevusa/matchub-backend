/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.ResourceRequestEntity;
import com.is4103.matchub.enumeration.RequestorEnum;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author longluqian
 */
@Data
public class ResourceRequestCreateVO {
    
    @NotBlank(message = "ResourceId cannot be blank.")
    @NotNull(message = "ResourceId cannot be null.")  
    private Long resourceId;
     
    @NotBlank(message = "ProjectId cannot be blank.")
    @NotNull(message = "ProjectId cannot be null.")
    private Long projectId;
    
    @NotBlank(message = "UnitsRequired cannot be blank.")
    @NotNull(message = "UnitsRequired cannot be null.")
    private Integer unitsRequired;
    
    @NotBlank(message = "RequestorId cannot be blank.")
    @NotNull(message = "RequestorId cannot be null.")
    private Long requestorId;
    
    private String message;
    
    public void createResourceRequestProjectOwner(ResourceRequestEntity newResourceRequest){
        newResourceRequest.setRequestorId(resourceId);
        newResourceRequest.setProjectId(projectId);
        newResourceRequest.setUnitsRequired(unitsRequired);
        newResourceRequest.setMessage(message);
        newResourceRequest.setRequestorEnum(RequestorEnum.PROJECT_OWNER);
        
        
    }
    
    public void createResourceRequestResourceOwner(ResourceRequestEntity newResourceRequest){
        newResourceRequest.setRequestorId(resourceId);
        newResourceRequest.setProjectId(projectId);
        newResourceRequest.setUnitsRequired(unitsRequired);
        newResourceRequest.setRequestorEnum(RequestorEnum.RESOURCE_OWNER);
        newResourceRequest.setMessage(message);
        
    }
}
