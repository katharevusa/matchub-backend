/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.service.MatchingService;
import com.is4103.matchub.service.MatchingServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
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
public class MatchingController {

    @Autowired
    MatchingService matchingService;

    @Autowired
    MatchingServiceImpl matchingServiceImpl;

    @RequestMapping(method = RequestMethod.GET, value = "/ws4j")
    public void ws4j(@RequestParam("word1") String word1, @RequestParam("word2") String word2) {
        matchingServiceImpl.runWS4J(word1, word2);
    }

    // for web
    @RequestMapping(method = RequestMethod.GET, value = "/recommendResources/list/{projectId}")
    public List<ResourceEntity> recommendListOfResources(@PathVariable("projectId") Long projectId) throws ProjectNotFoundException {
        return matchingService.recommendResources(projectId);
    }

    //for mobile
    @RequestMapping(method = RequestMethod.GET, value = "/recommendResources/pageable/{projectId}")
    public Page<ResourceEntity> recommendPageableOfResources(@PathVariable("projectId") Long projectId, Pageable pageable) throws ProjectNotFoundException {
        return matchingService.recommendResourcesAsPageable(projectId, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recommendProjects/list/{resourceId}")
    public List<ProjectEntity> recommendListOfProjects(@PathVariable("resourceId") Long resourceId) throws ResourceNotFoundException {
        return matchingService.recommendProjects(resourceId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recommendProjects/pageable/{resourceId}")
    public Page<ProjectEntity> recommendPageableOfProjects(@PathVariable("resourceId") Long resourceId, Pageable pageable) throws ResourceNotFoundException {
        return matchingService.recommendProjectsAsPageable(resourceId, pageable);
    }
}
