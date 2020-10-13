/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.JoinRequestEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import com.is4103.matchub.enumeration.JoinRequestStatusEnum;
import com.is4103.matchub.exception.LeaveProjectException;
import com.is4103.matchub.exception.RemoveTeamMemberException;
import com.is4103.matchub.exception.RespondToJoinProjectRequestException;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.JoinRequestEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class VolunteerImpl implements VolunteerService {

    @Autowired
    ProfileEntityRepository profileEntityRepository;

    @Autowired
    ProjectEntityRepository projectEntityRepository;

    @Autowired
    JoinRequestEntityRepository joinRequestEntityRepository;

    @Autowired
    AnnouncementService announcementService;

    @Autowired
    AnnouncementEntityRepository announcementEntityRepository;

    @Override
    public List<ProfileEntity> getWholeProjectGroup(Long projectId) {
        ProjectEntity project = projectEntityRepository.findById(projectId).get();
        List<ProfileEntity> list = project.getProjectOwners();
        list.addAll(project.getTeamMembers());
        return list;
    }

    @Override
    public List<JoinRequestEntity> getAllVolunteerJoinRequest(Long projectId) {
        ProjectEntity project = projectEntityRepository.findById(projectId).get();
        return project.getJoinRequests();
    }

    @Override
    public List<ProfileEntity> getTeamMembers(Long projectId) {
        ProjectEntity project = projectEntityRepository.findById(projectId).get();
        return project.getTeamMembers();
    }

    @Override
    public List<ProfileEntity> getProjectOwners(Long projectId) {
        ProjectEntity project = projectEntityRepository.findById(projectId).get();
        return project.getProjectOwners();
    }

    @Override
    public ProfileEntity getProjectCreator(Long projectId) {
        ProjectEntity project = projectEntityRepository.findById(projectId).get();
        return profileEntityRepository.findById(project.getProjCreatorId()).get();
    }

    @Override
    //Approve/Reject Volunteer’s Request to Join Project (requestid, boolean decision, decision maker)
    public JoinRequestEntity respondToJoinRequest(Long requestId, Long decisionMakerId, boolean decision) throws RespondToJoinProjectRequestException {
        JoinRequestEntity request = joinRequestEntityRepository.findById(requestId).get();
        ProfileEntity decisionMaker = profileEntityRepository.findById(decisionMakerId).get();
        ProjectEntity project = request.getProject();

        if (!project.getProjectOwners().contains(decisionMaker)) {
            throw new RespondToJoinProjectRequestException("Only project owners can make decision");
        }
        if (decision == true) {
            request.setStatus(JoinRequestStatusEnum.ACCEPTED);
            ProfileEntity requestor = request.getRequestor();
            project.getTeamMembers().add(request.getRequestor());
            requestor.getProjectsJoined().add(project);

        } else {
            request.setStatus(JoinRequestStatusEnum.REJECTED);

        }
        //create announcement
        ProfileEntity notifier = request.getRequestor();
        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTitle("Response to your join request");
        if (request.getStatus() == JoinRequestStatusEnum.ACCEPTED) {
            announcementEntity.setContent("Your request to join project '" + project.getProjectTitle() + "' has been approved.");
            announcementEntity.setType(AnnouncementTypeEnum.ACCEPT_JOIN_REQUEST);
        } else {
            announcementEntity.setContent("Your join request to project '" + project.getProjectTitle() + "' has been rejected.");
            announcementEntity.setType(AnnouncementTypeEnum.REJECT_JOIN_REQUEST);
        }
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setResourceId(requestId);

        // association
        announcementEntity.getNotifiedUsers().add(notifier);
        notifier.getAnnouncements().add(announcementEntity);
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification         
        announcementService.createNormalNotification(announcementEntity);

        return joinRequestEntityRepository.saveAndFlush(request);

    }

    //Remove Team Member
    @Override
    public void removeTeamMember(Long projectId, Long memberId, Long decisionMakerId) throws RemoveTeamMemberException {
//        JoinRequestEntity request = joinRequestEntityRepository.findById(requestId).get();
        ProfileEntity memberToDelete = profileEntityRepository.findById(memberId).get();
        ProfileEntity decisionMaker = profileEntityRepository.findById(decisionMakerId).get();
        ProjectEntity project = projectEntityRepository.findById(projectId).get();

        if (!project.getProjectOwners().contains(decisionMaker)) {
            throw new RemoveTeamMemberException("Only project owners can remove a member");
        }

        project.getTeamMembers().remove(memberToDelete);
        memberToDelete.getProjectsJoined().remove(project);
        // The join request status will become REJECTED   
        List<JoinRequestEntity> joinRequests = project.getJoinRequests();
        for (JoinRequestEntity j : joinRequests) {
            if (j.getRequestor().getAccountId().equals(memberId)) {
                j.setStatus(JoinRequestStatusEnum.REJECTED);
            }
        }
        projectEntityRepository.flush();

        //create announcement
        ProfileEntity notifier = memberToDelete;
        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTitle("Remove Notification");
        announcementEntity.setContent("You have been removed from project '" + project.getProjectTitle() + "'.");
        announcementEntity.setType(AnnouncementTypeEnum.REMOVE_MEMBER);
        announcementEntity.setTimestamp(LocalDateTime.now());
        

        // association
        announcementEntity.getNotifiedUsers().add(notifier);
        notifier.getAnnouncements().add(announcementEntity);
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification         
        announcementService.createNormalNotification(announcementEntity);

    }

    //Leave Project
    @Override
    public void leaveProject(Long projectId, Long memberId) throws LeaveProjectException {
//        JoinRequestEntity request = joinRequestEntityRepository.findById(requestId).get();
        ProfileEntity memberToLeave = profileEntityRepository.findById(memberId).get();
        ProjectEntity project = projectEntityRepository.findById(projectId).get();

        project.getTeamMembers().remove(memberToLeave);
        memberToLeave.getProjectsJoined().remove(project);
        // The join request status will become REJECTED   
        List<JoinRequestEntity> joinRequests = project.getJoinRequests();
        joinRequests.stream().filter((j) -> (j.getRequestor().getAccountId().equals(memberId))).forEachOrdered((j) -> {
            j.setStatus(JoinRequestStatusEnum.REJECTED);
        });

        // create announcement (notify project owners)
        String memberToLeaveName = "";
        if (memberToLeave instanceof IndividualEntity) {
            memberToLeaveName = ((IndividualEntity) memberToLeave).getFirstName() + " " + ((IndividualEntity) memberToLeave).getLastName();
        } else if (memberToLeave instanceof OrganisationEntity) {
            memberToLeaveName = ((OrganisationEntity) memberToLeave).getOrganizationName();
        }

        List<ProfileEntity> projectOwners = project.getProjectOwners();
        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTitle("Teammember Left Project");
        announcementEntity.setContent("Teammember " + memberToLeaveName + "has left project '" + project.getProjectTitle()+"'.");
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setType(AnnouncementTypeEnum.LEAVE_PROJECT);

        // association
        announcementEntity.getNotifiedUsers().addAll(projectOwners);
        for (ProfileEntity p : projectOwners) {
            p.getAnnouncements().add(announcementEntity);
        }
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification         
        announcementService.createNormalNotification(announcementEntity);
        projectEntityRepository.flush();

    }
}
