/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.JoinRequestEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.LeaveProjectException;
import com.is4103.matchub.exception.RemoveTeamMemberException;
import com.is4103.matchub.exception.RespondToJoinProjectRequestException;
import com.is4103.matchub.service.VolunteerService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author longluqian
 */
@RestController
@RequestMapping("/authenticated")
public class VolunteerController {

    @Autowired
    VolunteerService volunteerService;

    // View A List of Volunteers’ join requests
    @RequestMapping(method = RequestMethod.GET, value = "/getAllVolunteerJoinRequest")
    List<JoinRequestEntity> getAllVolunteerJoinRequest(@RequestParam(value = "projectId", defaultValue = "") Long projectId) {
        return volunteerService.getAllVolunteerJoinRequest(projectId);
    }

    //view a list of teammembers (project id)
    @RequestMapping(method = RequestMethod.GET, value = "/getOnlyTeamMembers")
    List<ProfileEntity> getOnlyTeamMembers(@RequestParam(value = "projectId", defaultValue = "") Long projectId) {
        return volunteerService.getTeamMembers(projectId);
    }

    // get project owners
    @RequestMapping(method = RequestMethod.GET, value = "/getProjectOwners")
    List<ProfileEntity> getProjectOwners(@RequestParam(value = "projectId", defaultValue = "") Long projectId) {
        return volunteerService.getProjectOwners(projectId);
    }

    //get project creator
    @RequestMapping(method = RequestMethod.GET, value = "/getProjectCreator")
    ProfileEntity getProjectCreator(@RequestParam(value = "projectId", defaultValue = "") Long projectId) {
        return volunteerService.getProjectCreator(projectId);
    }

    //get a list of project owners + project teammembers in a project
    @RequestMapping(method = RequestMethod.GET, value = "/getWholeProjectGroup")
    List<ProfileEntity> getWholeProjectGroup(@RequestParam(value = "projectId", defaultValue = "") Long projectId) {
        return volunteerService.getWholeProjectGroup(projectId);
    }

    //Approve/Reject Volunteer’s Request to Join Project (requestid, boolean decision, decision maker)
    @RequestMapping(method = RequestMethod.POST, value = "/respondToJoinRequest")
    public JoinRequestEntity respondToJoinRequest(@RequestParam(value = "requestId", required = true) Long requestId, @RequestParam(value = "decisionMakerId", required = true) Long decisionMakerId, @RequestParam(value = "decision", required = true) boolean decision) throws RespondToJoinProjectRequestException {
        return volunteerService.respondToJoinRequest(requestId, decisionMakerId, decision);
    }

    //Remove Team Member
    @RequestMapping(method = RequestMethod.DELETE, value = "/removeTeamMember")
    public void removeTeamMember(@RequestParam(value = "projectId", required = true) Long projectId, @RequestParam(value = "memberId", required = true) Long memberId, @RequestParam(value = "decisionMakerId", required = true) Long decisionMakerId) throws RemoveTeamMemberException {
        volunteerService.removeTeamMember(projectId, memberId, decisionMakerId);
    }

    //Leave Project
    @RequestMapping(method = RequestMethod.DELETE, value = "/leaveProject")
    public void leaveProject(@RequestParam(value = "projectId", required = true) Long projectId, @RequestParam(value = "memberId", required = true) Long memberId) throws LeaveProjectException {
        volunteerService.leaveProject(projectId, memberId);
    }
}
