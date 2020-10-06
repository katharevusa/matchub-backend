/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
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
import javax.mail.Multipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
@Service
public class BadgeServiceImpl implements BadgeService {

    private static final List<String> badgeIcons = new ArrayList<>();

    @Autowired
    private BadgeEntityRepository badgeEntityRepository;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private AttachmentService attachmentService;

    private static void setBadgeIcons() {
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/animal.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/cities.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/construction.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/education.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/environment.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/food.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/gender-equality.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/healthcare.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/help-community.png");
        badgeIcons.add("https://localhost:8443/api/v1/files/badgeIcons/partnerships.png");
    }

    @Override
    public List<String> retrieveBadgeIcons() {
        if (badgeIcons.size() == 0) {
            setBadgeIcons();
        }
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
        projectEntityRepository.save(project);

        return newBadge;
    }

    @Transactional
    @Override
    public BadgeEntity uploadBadgeIcon(Long badgeId, MultipartFile icon) {
        //check if the badge exists
        BadgeEntity badge = badgeEntityRepository.findById(badgeId)
                .orElseThrow(() -> new BadgeNotFoundException("Badge id: " + badgeId + " cannot be found"));

        String path = attachmentService.upload(icon);
        badge.setIcon(path);

        badge = badgeEntityRepository.saveAndFlush(badge);
        return badge;
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

    // **************** SYSTEM USE CASE ****************
    @Override
    public void leaderboardTop10() {
        //find the top10 badge 
        BadgeEntity top10Badge = badgeEntityRepository.findByBadgeTitle("TOP 10 IN LEADERBOARD");

        //find the top 10
        Pageable top10 = PageRequest.of(0, 10);
        List<ProfileEntity> profiles = profileEntityRepository.leaderboard(top10).toList();

        //issue and remove badge 
        removeAndIssueLeaderboardBadges(top10Badge, profiles);

    }

    @Override
    public void leaderboardTop50() {
        //find the top50 badge 
        BadgeEntity top50Badge = badgeEntityRepository.findByBadgeTitle("TOP 50 IN LEADERBOARD");

        //find the top 50
        Pageable top50 = PageRequest.of(0, 50);
        List<ProfileEntity> profiles = profileEntityRepository.leaderboard(top50).toList();

        //issue and remove badge 
        removeAndIssueLeaderboardBadges(top50Badge, profiles.subList(10, profiles.size() - 1));

    }

    @Override
    public void leaderboardTop100() {
        //find the top100 badge 
        BadgeEntity top100Badge = badgeEntityRepository.findByBadgeTitle("TOP 100 IN LEADERBOARD");

        //find the top 100
        Pageable top100 = PageRequest.of(0, 100);
        List<ProfileEntity> profiles = profileEntityRepository.leaderboard(top100).toList();

        //issue and remove badge 
        removeAndIssueLeaderboardBadges(top100Badge, profiles.subList(50, profiles.size() - 1));

    }

    private void removeAndIssueLeaderboardBadges(BadgeEntity leaderboardBadge, List<ProfileEntity> profiles) {
        //unassociate the initial people with the badges 
        for (ProfileEntity p : leaderboardBadge.getProfiles()) {
            p.getBadges().remove(leaderboardBadge);

            profileEntityRepository.save(p);
        }
        //clear the leaderboard badge first
        leaderboardBadge.getProfiles().clear();

        //associate the new profiles with the badge (2 ways)
        for (ProfileEntity p : profiles) {
            p.getBadges().add(leaderboardBadge);
            profileEntityRepository.save(p);

            leaderboardBadge.getProfiles().add(p);
        }

        badgeEntityRepository.save(leaderboardBadge);
    }

    @Override
    public void issueLongServiceAward1YearBadge(ProfileEntity profile) {
        BadgeEntity oneYear = badgeEntityRepository.findByBadgeTitle("1 YEAR WITH MATCHUB");

        profile.getBadges().add(oneYear);
        profileEntityRepository.save(profile);

        oneYear.getProfiles().add(profile);
        badgeEntityRepository.save(oneYear);
    }

    @Override
    public void issueLongServiceAward2YearsBadge(ProfileEntity profile) {
        //add the 2 year badge 
        BadgeEntity twoYears = badgeEntityRepository.findByBadgeTitle("2 YEARS WITH MATCHUB");

        profile.getBadges().add(twoYears);
        twoYears.getProfiles().add(profile);

        //remove the 1 year badge
        BadgeEntity oneYear = badgeEntityRepository.findByBadgeTitle("1 YEAR WITH MATCHUB");
        profile.getBadges().remove(oneYear);
        oneYear.getProfiles().remove(profile);

        profileEntityRepository.save(profile);
        badgeEntityRepository.save(twoYears);
        badgeEntityRepository.save(oneYear);
    }

    @Override
    public void issueLongServiceAward5YearsBadge(ProfileEntity profile) {
        //add the 5 year badge 
        BadgeEntity fiveYears = badgeEntityRepository.findByBadgeTitle("5 YEARS WITH MATCHUB");

        profile.getBadges().add(fiveYears);
        fiveYears.getProfiles().add(profile);

        //remove the 2 year badge
        BadgeEntity twoYears = badgeEntityRepository.findByBadgeTitle("2 YEARS WITH MATCHUB");
        profile.getBadges().remove(twoYears);
        twoYears.getProfiles().remove(profile);

        profileEntityRepository.save(profile);
        badgeEntityRepository.save(twoYears);
        badgeEntityRepository.save(fiveYears);
    }

    @Override
    public void issueProjectBadge(ProjectEntity project) {

        BadgeEntity badge = project.getProjectBadge();

        for (ProfileEntity p : project.getTeamMembers()) {
            badge.getProfiles().add(p);
            p.getBadges().add(badge);

            profileEntityRepository.save(p);
        }

        for (ProfileEntity p : project.getProjectOwners()) {
            badge.getProfiles().add(p);
            p.getBadges().add(badge);

            profileEntityRepository.save(p);
        }

        badgeEntityRepository.save(badge);

        //trigger the significantprojectcontributor badge method
        significantProjectContributorBadge(project.getTeamMembers());
        significantProjectContributorBadge(project.getProjectOwners());
    }

    public void significantProjectContributorBadge(List<ProfileEntity> profiles) {

        System.out.println("Significant Project Contributor Badge: ***************");

        for (ProfileEntity p : profiles) {
            Integer completed = projectEntityRepository.getCompletedProjectsByAccountId(p.getAccountId()).size();

            System.out.println("acountId " + p.getAccountId() + " = completed " + completed + " projects");

            BadgeEntity badge;

            //query for the correct significant project contributor badge
            if (completed == 5) {
                badge = badgeEntityRepository.findByBadgeTitle("5 PROJECT CONTRIBUTIONS");
            } else if (completed == 10) {
                badge = badgeEntityRepository.findByBadgeTitle("10 PROJECT CONTRIBUTIONS");
            } else if (completed == 50) {
                badge = badgeEntityRepository.findByBadgeTitle("50 PROJECT CONTRIBUTIONS");
            } else {
                continue;
            }

            badge.getProfiles().add(p);
            badgeEntityRepository.save(badge);

            p.getBadges().add(badge);
            profileEntityRepository.save(p);

        }
    }

}
