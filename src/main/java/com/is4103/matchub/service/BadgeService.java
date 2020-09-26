/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.vo.ProjectBadgeCreateVO;

/**
 *
 * @author ngjin
 */
public interface BadgeService {

    BadgeEntity createProjectBadge(ProjectBadgeCreateVO createVO) throws ProjectNotFoundException;
}
