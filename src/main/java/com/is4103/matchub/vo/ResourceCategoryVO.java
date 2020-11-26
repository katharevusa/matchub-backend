/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.ResourceCategoryEntity;
import javax.validation.constraints.NotNull;

/**
 *
 * @author longluqian
 */
public class ResourceCategoryVO {
     
    @NotNull (message = "resource category name can not be null" )
    private String resourceCategoryName;


    @NotNull(message = "resource description  can not be null" )
    private String resourceCategoryDescription;


    @NotNull(message = "communityPointsGuideline can not be null" )
    private Integer communityPointsGuideline;
    
    @NotNull(message = "unit quantiy can not be null" )
    private Integer perUnit;
    
    @NotNull(message = "unit quantity  can not be null" )
    private String unitName;
    
    public Long resourceCategoryId;
    
    public void createResourceCategory(ResourceCategoryEntity newResourceCategoryEntity){
        newResourceCategoryEntity.setResourceCategoryName(resourceCategoryName);
        newResourceCategoryEntity.setResourceCategoryDescription(resourceCategoryDescription);
        newResourceCategoryEntity.setCommunityPointsGuideline(communityPointsGuideline);
        newResourceCategoryEntity.setPerUnit(perUnit);
        newResourceCategoryEntity.setUnitName(unitName);
    }
    
    public void updateResourceCategory(ResourceCategoryEntity newResourceCategoryEntity){
        newResourceCategoryEntity.setResourceCategoryName(resourceCategoryName);
        newResourceCategoryEntity.setResourceCategoryDescription(resourceCategoryDescription);
        newResourceCategoryEntity.setCommunityPointsGuideline(communityPointsGuideline);
        newResourceCategoryEntity.setPerUnit(perUnit);
        newResourceCategoryEntity.setUnitName(unitName);
    }
}
