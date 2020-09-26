/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.BadgeEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author ngjin
 */
@Data
public class ProjectBadgeCreateVO {
    
    @NotNull(message = "Project Id can not be null.")
    private Long projectId;
    
    @NotNull(message = "Badge Title can not be null.")
    @NotBlank(message = "Badge Title can not be blank.")
    private String badgeTitle;
    
    @NotNull(message = "Badge Icon can not be null.")
    @NotBlank(message = "Badge Icon can not be blank.")
    private String icon;
    
    public void updateProjectBadge(BadgeEntity badge) {
        badge.setBadgeTitle(this.badgeTitle);
        badge.setIcon(this.icon);
        //associate project with badge in service class
    }
}
