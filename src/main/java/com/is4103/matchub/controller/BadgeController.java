/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.service.BadgeService;
import com.is4103.matchub.vo.ProjectBadgeCreateVO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BadgeController {

    @Autowired
    BadgeService badgeService;

    @RequestMapping(method = RequestMethod.POST, value = "/createProjectBadge")
    BadgeEntity createProjectBadge(@Valid @RequestBody ProjectBadgeCreateVO createVO) throws ProjectNotFoundException {
        return badgeService.createProjectBadge(createVO);
    }

}
