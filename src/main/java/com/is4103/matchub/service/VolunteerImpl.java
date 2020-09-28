/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.JoinRequestEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.enumeration.JoinRequestStatusEnum;
import com.is4103.matchub.exception.LeaveProjectException;
import com.is4103.matchub.exception.RemoveTeamMemberException;
import com.is4103.matchub.exception.RespondToJoinProjectRequestException;
import com.is4103.matchub.repository.JoinRequestEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
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
            //notify user

        } else {
            request.setStatus(JoinRequestStatusEnum.REJECTED);
            //notify user
        }
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
        
        projectEntityRepository.flush();

    }
}
