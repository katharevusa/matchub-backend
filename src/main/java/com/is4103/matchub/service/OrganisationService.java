/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author ngjin
 */
public interface OrganisationService {

//    OrganisationEntity addKAH(Long organ isationId, Long individualId);
    OrganisationEntity addMemberToOrganisation(Long organisationId, Long individualId);

    OrganisationEntity removeMemberFromOrganisation(Long organisationId, Long individualId);

    Page<ProfileEntity> viewOrganisationMembers(Long organisationId, Pageable pageable);

    OrganisationEntity addKahToOrganisation(Long organisationId, Long individualId);

    OrganisationEntity removeKahFromOrganisation(Long organisationId, Long individualId);

    Page<ProfileEntity> viewOrganisationKAHs(Long organisationId, Pageable pageable);

    Page<ProfileEntity> searchMembers(Long organisationId, String search, Pageable pageable);
}
