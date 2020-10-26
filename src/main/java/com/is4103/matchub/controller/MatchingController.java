/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.ProfileEntity;
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

    //for testing purposes only
    @RequestMapping(method = RequestMethod.GET, value = "/ws4j")
    public void ws4j(@RequestParam("word1") String word1, @RequestParam("word2") String word2) {
        matchingServiceImpl.runWS4J(word1, word2);
    }

    // for web
    @RequestMapping(method = RequestMethod.GET, value = "/recommendResources/list/{projectId}")
    public List<ResourceEntity> recommendListOfResources(@PathVariable("projectId") Long projectId) throws ProjectNotFoundException {
        return matchingService.recommendResources(projectId);
    }

    // for web
    @RequestMapping(method = RequestMethod.GET, value = "/recommendSameCountryResources/list/{projectId}")
    public List<ResourceEntity> recommendListOfSameCountryResources(@PathVariable("projectId") Long projectId) throws ProjectNotFoundException {
        return matchingService.recommendSameCountryResources(projectId);
    }

    //for mobile
    @RequestMapping(method = RequestMethod.GET, value = "/recommendResources/page/{projectId}")
    public Page<ResourceEntity> recommendPageableOfResources(@PathVariable("projectId") Long projectId, Pageable pageable) throws ProjectNotFoundException {
//        return matchingService.recommendSameCountryResourcesAsPageable(projectId, pageable);
        return matchingService.recommendResourcesAsPageable(projectId, pageable);
    }

    //for mobile
    @RequestMapping(method = RequestMethod.GET, value = "/recommendSameCountryResources/page/{projectId}")
    public Page<ResourceEntity> recommendPageableOfSameCountryResources(@PathVariable("projectId") Long projectId, Pageable pageable) throws ProjectNotFoundException {
        return matchingService.recommendSameCountryResourcesAsPageable(projectId, pageable);
    }

    // for web
    @RequestMapping(method = RequestMethod.GET, value = "/recommendProjects/list/{resourceId}")
    public List<ProjectEntity> recommendListOfProjects(@PathVariable("resourceId") Long resourceId) throws ResourceNotFoundException {
        return matchingService.recommendProjects(resourceId);
    }

    // for web
    @RequestMapping(method = RequestMethod.GET, value = "/recommendSameCountryProjects/list/{resourceId}")
    public List<ProjectEntity> recommendListOfSameCountryProjects(@PathVariable("resourceId") Long resourceId) throws ResourceNotFoundException {
        return matchingService.recommendSameCountryProjects(resourceId);
    }

    //for mobile
    @RequestMapping(method = RequestMethod.GET, value = "/recommendProjects/page/{resourceId}")
    public Page<ProjectEntity> recommendPageableOfProjects(@PathVariable("resourceId") Long resourceId, Pageable pageable) throws ResourceNotFoundException {
        return matchingService.recommendProjectsAsPageable(resourceId, pageable);
    }

    //for mobile
    @RequestMapping(method = RequestMethod.GET, value = "/recommendSameCountryProjects/page/{resourceId}")
    public Page<ProjectEntity> recommendPageableOfSameCountryProjects(@PathVariable("resourceId") Long resourceId, Pageable pageable) throws ResourceNotFoundException {
        return matchingService.recommendSameCountryProjectsAsPageable(resourceId, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recommendProfiles/{accountId}")
    public Page<ProfileEntity> recommendProfiles(@PathVariable("accountId") Long accountId, Pageable pageable) {
        return matchingService.recommendProfiles(accountId, pageable);
    }
}
