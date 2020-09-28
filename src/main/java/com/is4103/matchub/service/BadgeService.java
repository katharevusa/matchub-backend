/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.vo.ProjectBadgeCreateVO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author ngjin
 */
public interface BadgeService {

    List<String> retrieveBadgeIcons();

    BadgeEntity createProjectBadge(ProjectBadgeCreateVO createVO) throws ProjectNotFoundException;

    Page<BadgeEntity> getBadgesByAccountId(Long id, Pageable pageable);
}
