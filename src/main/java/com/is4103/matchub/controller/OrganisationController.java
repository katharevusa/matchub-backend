/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.service.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ngjin
 */
@RestController
@RequestMapping("/authenticated")
public class OrganisationController {

    @Autowired
    OrganisationService organisationService;

    @RequestMapping(method = RequestMethod.PUT, value = "organisation/addMember/{organisationId}/{individualId}")
    OrganisationEntity addMember(@PathVariable("organisationId") Long organisationId, @PathVariable("individualId") Long individualId) {
        return organisationService.addMemberToOrganisation(organisationId, individualId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "organisation/removeMember/{organisationId}/{individualId}")
    OrganisationEntity removeMember(@PathVariable("organisationId") Long organisationId, @PathVariable("individualId") Long individualId) {
        return organisationService.removeMemberFromOrganisation(organisationId, individualId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "organisation/viewMembers/{organisationId}")
    Page<ProfileEntity> viewOrganisationMembers(@PathVariable("organisationId") Long organisationId, Pageable pageable) {
        return organisationService.viewOrganisationMembers(organisationId, pageable);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "organisation/addKAH/{organisationId}/{individualId}")
    OrganisationEntity addKAH(@PathVariable("organisationId") Long organisationId, @PathVariable("individualId") Long individualId) {
        return organisationService.addKahToOrganisation(organisationId, individualId);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "organisation/viewKAHs/{organisationId}")
    Page<ProfileEntity> viewOrganisationKAHs(@PathVariable("organisationId") Long organisationId, Pageable pageable) {
        return organisationService.viewOrganisationKAHs(organisationId, pageable);
    }
}
