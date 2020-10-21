/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.service.ReputationPointsService;
import com.is4103.matchub.vo.IssuePointsToResourceDonorsVO;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/authenticated")
public class ReputationPointsController {

    @Autowired
    ReputationPointsService reputationPointsService;

    @RequestMapping(method = RequestMethod.GET, value = "/getResourceOfProject/{projectId}")
    Page<ResourceEntity> getResourceOfProject(@PathVariable("projectId") Long projectId, Pageable pageable) throws ProjectNotFoundException {
        return reputationPointsService.getResourceOfProject(projectId, pageable);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/issuePointsToResourceDonors")
    void issuePointsToResourceDonors(@Valid @RequestBody IssuePointsToResourceDonorsVO vo) throws ProjectNotFoundException {
        reputationPointsService.issuePointsToResourceDonors(vo);
    }

}
