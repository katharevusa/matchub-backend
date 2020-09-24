/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.JoinRequestEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.LeaveProjectException;
import com.is4103.matchub.exception.RemoveTeamMemberException;
import com.is4103.matchub.exception.RespondToJoinProjectRequestException;
import java.util.List;

/**
 *
 * @author longluqian
 */
public interface VolunteerService {

    public List<ProfileEntity> getWholeProjectGroup(Long projectId);

    public void leaveProject(Long projectId, Long memberId) throws LeaveProjectException;

    public void removeTeamMember(Long projectId, Long memberId, Long decisionMakerId) throws RemoveTeamMemberException;

    public JoinRequestEntity respondToJoinRequest(Long requestId, Long decisionMakerId, boolean decision) throws RespondToJoinProjectRequestException;

    public ProfileEntity getProjectCreator(Long projectId);

    public List<ProfileEntity> getProjectOwners(Long projectId);

    public List<ProfileEntity> getTeamMembers(Long projectId);

    public List<JoinRequestEntity> getAllVolunteerJoinRequest(Long projectId);
 
}
