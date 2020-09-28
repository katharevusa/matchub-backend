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
public class ProjectBadgeUpdateVO {

    @NotNull(message = "AccountId can not be null.")
    private Long accountId;

    private String badgeTitle;

    private String icon;

    public void updateProjectBadge(BadgeEntity badge) {

        if (!this.badgeTitle.isEmpty()) {
            badge.setBadgeTitle(this.badgeTitle);
        }

        if (!this.icon.isEmpty()) {
            badge.setIcon(this.icon);
        }
        //cannot reassociate existing badge with a new project
    }
}
