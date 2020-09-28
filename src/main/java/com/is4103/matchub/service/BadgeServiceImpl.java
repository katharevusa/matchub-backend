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
import com.is4103.matchub.exception.BadgeNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UnableToCreateProjectBadgeException;
import com.is4103.matchub.exception.UnableToUpdateBadgeException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.BadgeEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.vo.ProjectBadgeCreateVO;
import com.is4103.matchub.vo.ProjectBadgeUpdateVO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    @Override
    public List<String> retrieveBadgeIcons() {
        List<String> badgeIcons = new ArrayList<>();
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/cities.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/construction.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/education.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/environment.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/food.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/gender-equality.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/healthcare.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/help-community.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/partnerships.png");

        return badgeIcons;
    }

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

    @Override
    public BadgeEntity updateProjectBadge(Long badgeId, ProjectBadgeUpdateVO updateVO) {
        //check if the badge exists
        BadgeEntity badgeToUpdate = badgeEntityRepository.findById(badgeId)
                .orElseThrow(() -> new BadgeNotFoundException("Badge id: " + badgeId + " cannot be found"));

        //check if account updating the badge is authorised
        List<ProfileEntity> projectOwners = badgeToUpdate.getProject().getProjectOwners();

        List<Long> projectOwnersId = projectOwners.stream()
                .map(ProfileEntity::getAccountId)
                .collect(Collectors.toList());
        
        if (projectOwnersId.contains(updateVO.getAccountId())) {
            //update the badge 
            updateVO.updateProjectBadge(badgeToUpdate);
            
            badgeToUpdate = badgeEntityRepository.saveAndFlush(badgeToUpdate);
            return badgeToUpdate;
        } else {
            throw new UnableToUpdateBadgeException("Unable to update badge: accountId "
                    + updateVO.getAccountId() + " is not a project owner and does not have "
                    + "rights to update project badge.");
        }
    }

}
