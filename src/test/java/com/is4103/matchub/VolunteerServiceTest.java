/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.entity.JoinRequestEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.enumeration.JoinRequestStatusEnum;
import com.is4103.matchub.exception.RemoveTeamMemberException;
import com.is4103.matchub.exception.RespondToJoinProjectRequestException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.service.VolunteerService;
import com.is4103.matchub.vo.TaskColumnVO;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author markt
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VolunteerServiceTest {

    public VolunteerServiceTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Test
    @Transactional
    public void testGetWholeProjectGroup() {
        List result = volunteerService.getWholeProjectGroup(1L);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    @Transactional
    public void testLeaveProject() {
        int initialSize = projectEntityRepository.findById(1L).get().getTeamMembers().size();
        volunteerService.leaveProject(1L, 9L);
        int afterSize = projectEntityRepository.findById(1L).get().getTeamMembers().size();
        Assert.assertTrue(initialSize - 1 == afterSize);
    }

    @Test
    @Order(2)
    @Transactional
    public void testRemoveMember() {
        try {
            int initialSize = projectEntityRepository.findById(11L).get().getTeamMembers().size();
            volunteerService.removeTeamMember(11L, 9L, 5L);
            int afterSize = projectEntityRepository.findById(11L).get().getTeamMembers().size();
            Assert.assertTrue(initialSize - 1 == afterSize);
        } catch (RemoveTeamMemberException ex) {
        }
    }

    @Test(expected = RemoveTeamMemberException.class)
    @Transactional
    public void testRemoveMemberRemoveTeamMemberException() throws RemoveTeamMemberException {
        volunteerService.removeTeamMember(11L, 9L, 4L); //project owner is not 4L
    }

    @Test
    @Transactional
    public void testRespondToJoinRequest() {
        try {
            JoinRequestEntity result = volunteerService.respondToJoinRequest(3L, 5L, true);
            Assert.assertTrue(result.getStatus() == JoinRequestStatusEnum.ACCEPTED);
        } catch (RespondToJoinProjectRequestException e) {
        }
    }

    @Test(expected = RespondToJoinProjectRequestException.class)
    @Transactional
    public void testRespondToJoinRequestRespondToJoinProjectRequestException() throws RespondToJoinProjectRequestException {
        JoinRequestEntity result = volunteerService.respondToJoinRequest(4L, 2L, true); //Profile 2L isnt a project owner, doesnt have the right to respond
    }

    @Test
    public void testGetProjectCreator() {
        ProfileEntity p = volunteerService.getProjectCreator(1L); //project Id = 1L
        Assert.assertTrue(p.getAccountId() == 5L); //project Creator Id = 5L
    }

    @Test
    @Transactional
    public void testGetProjectOwners() {
        List pOwners = volunteerService.getProjectOwners(1L); //project Id = 1L
        ProfileEntity projectOwner = profileEntityRepository.findById(5L).get(); //profile 5L is project owner
        Assert.assertTrue(pOwners.contains(projectOwner));

    }

    @Test
    @Transactional
    public void testGetTeamMembers() {
        List pMembers = volunteerService.getTeamMembers(13L); //project Id = 13L
        ProfileEntity teamMember = profileEntityRepository.findById(9L).get(); // profile 9L is a team member of project 13
        Assert.assertTrue(pMembers.contains(teamMember));
    }

    @Test
    @Transactional
    public void testGetAllVolunteerJoinRequest() {
        List joinRequests = volunteerService.getAllVolunteerJoinRequest(3L);
        Assert.assertFalse(joinRequests.isEmpty());
    }
}
