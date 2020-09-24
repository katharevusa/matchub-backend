/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.exception.OrganisationNotFoundException;
import com.is4103.matchub.exception.UnableToAddMemberToOrganisationException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.OrganisationEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ngjin
 */
@Service
public class OrganisationServiceImpl implements OrganisationService {

    @Autowired
    private OrganisationEntityRepository organisationEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

//    @Transactional
//    @Override
//    public OrganisationEntity addKAH(Long organisationId, Long individualId) {
//        
//        OrganisationEntity organisation = organisationEntityRepository.findById(organisationId)
//                .orElseThrow(() -> new UserNotFoundException(organisationId));
//    }
    @Transactional
    @Override
    public OrganisationEntity addMemberToOrganisation(Long organisationId, Long individualId) {

        OrganisationEntity organisation = organisationEntityRepository.findById(organisationId)
                .orElseThrow(() -> new OrganisationNotFoundException("Organisatio with id: "+ organisationId + " not found."));

        ProfileEntity memberToAdd = profileEntityRepository.findById(individualId)
                .orElseThrow(() -> new UserNotFoundException(individualId));

        //check if individual is already inside organisation
        if (!organisation.getEmployees().contains(memberToAdd.getAccountId())) {
            organisation.getEmployees().add(memberToAdd.getAccountId());
            
            organisation = organisationEntityRepository.saveAndFlush(organisation);
            return organisation;
        } else {
            throw new UnableToAddMemberToOrganisationException("Unable to add account " + individualId
                    + " into organisationId " + organisationId + ": account is already a member of organisation.");
        }

    }

}
