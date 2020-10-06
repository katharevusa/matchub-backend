/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.service.AttachmentService;
import com.is4103.matchub.service.BadgeService;
import com.is4103.matchub.vo.ProjectBadgeCreateVO;
import com.is4103.matchub.vo.ProjectBadgeUpdateVO;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/authenticated")
public class BadgeController {

    @Autowired
    BadgeService badgeService;

    @RequestMapping(method = RequestMethod.GET, value = "/getProjectBadgeIcons")
    List<String> getProjectBadgeIcons() {
        return badgeService.retrieveBadgeIcons();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createProjectBadge")
    BadgeEntity createProjectBadge(@Valid @RequestBody ProjectBadgeCreateVO createVO) throws ProjectNotFoundException {
        return badgeService.createProjectBadge(createVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "projectBadge/uploadBadgeIcon/{badgeId}")
    public BadgeEntity uploadBadgeIcon(@RequestParam(value = "icon") MultipartFile icon, @PathVariable("badgeId") Long badgeId) {
        return badgeService.uploadBadgeIcon(badgeId, icon);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getBadgesByAccountId/{accountId}")
    Page<BadgeEntity> getBadgesByAccountId(@PathVariable("accountId") Long postId, Pageable pageable) {
        return badgeService.getBadgesByAccountId(postId, pageable);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateProjectBadge/{badgeId}")
    BadgeEntity updateProjectBadge(@PathVariable("badgeId") Long badgeId, @Valid @RequestBody ProjectBadgeUpdateVO vo) {
        return badgeService.updateProjectBadge(badgeId, vo);
    }

}
