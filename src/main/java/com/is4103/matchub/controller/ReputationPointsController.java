/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import static com.google.cloud.storage.Storage.GetHmacKeyOption.projectId;
import com.is4103.matchub.entity.GamificationPointTiers;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.service.ProjectService;
import com.is4103.matchub.service.ReputationPointsService;
import com.is4103.matchub.service.ResourceService;
import com.is4103.matchub.vo.IssuePointsToResourceDonorsVO;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @Autowired
    ProjectService projectService;

    @Autowired
    ResourceService resourceService;

    @RequestMapping(method = RequestMethod.GET, value = "/getPointTiers")
    GamificationPointTiers getGamificationPointTiers() {
        return reputationPointsService.getGamificationPointTiers();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getResourceOfProject/{projectId}")
    Page<ResourceEntity> getResourceOfProject(@PathVariable("projectId") Long projectId, Pageable pageable) throws ProjectNotFoundException {
        return reputationPointsService.getResourceOfProject(projectId, pageable);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/issuePointsToResourceDonors")
    void issuePointsToResourceDonors(@Valid @RequestBody IssuePointsToResourceDonorsVO vo) throws ProjectNotFoundException {
        reputationPointsService.issuePointsToResourceDonors(vo);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/spotlightProject/{projectId}/{accountId}")
    ProjectEntity spotlightProject(@PathVariable("projectId") Long projectId, @PathVariable("accountId") Long accountId) throws ProjectNotFoundException {
        return reputationPointsService.spotlightProject(projectId, accountId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/spotlightResource/{resourceId}/{accountId}")
    ResourceEntity spotlightResource(@PathVariable("resourceId") Long resourceId, @PathVariable("accountId") Long accountId) throws ResourceNotFoundException {
        return reputationPointsService.spotlightResource(resourceId, accountId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list/getSpotlightedProjects")
    List<ProjectEntity> getSpotlightedProjects() {
        return projectService.getSpotlightedProjects();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/page/getSpotlightedProjects")
    Page<ProjectEntity> getSpotlightedProjects(Pageable pageable) {
        return projectService.getSpotlightedProjects(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list/getSpotlightedResources")
    List<ResourceEntity> getSpotlightedResources() {
        return resourceService.getSpotlightedResources();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/page/getSpotlightedResources")
    Page<ResourceEntity> getSpotlightedResources(Pageable pageable) {
        return resourceService.getSpotlightedResources(pageable);
    }

}
