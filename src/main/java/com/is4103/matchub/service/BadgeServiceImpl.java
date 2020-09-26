/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.enumeration.BadgeTypeEnum;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UnableToCreateProjectBadgeException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.BadgeEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.vo.ProjectBadgeCreateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ngjin
 */
@Service
public class BadgeServiceImpl implements BadgeService {

    @Autowired
    private BadgeEntityRepository badgeEntityRepository;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Transactional
    @Override
    public BadgeEntity createProjectBadge(ProjectBadgeCreateVO createVO) throws ProjectNotFoundException {
        //check if the project exists
        ProjectEntity project = projectEntityRepository.findById(createVO.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("ProjectId: " + createVO.getProjectId() + " not found."));

        //check if the project has 1 projectBadge associated with it already or not
        if (project.getProjectBadge() != null) {
            throw new UnableToCreateProjectBadgeException("Unable to create project badge: projectId "
                    + createVO.getProjectId() + " already has a project badge created.");
        }

        BadgeEntity newBadge = new BadgeEntity();
        newBadge.setBadgeType(BadgeTypeEnum.PROJECT_SPECIFIC);

        createVO.updateProjectBadge(newBadge);
        newBadge.setProject(project);

        newBadge = badgeEntityRepository.saveAndFlush(newBadge);

        //set association on project
        project.setProjectBadge(newBadge);

        return newBadge;
    }

    @Override
    public Page<BadgeEntity> getBadgesByAccountId(Long id, Pageable pageable) {
        ProfileEntity profile = profileEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        return badgeEntityRepository.getBadgesByAccountId(id, pageable);
    }

}
