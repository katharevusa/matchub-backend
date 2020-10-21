/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.repository.IndividualEntityRepository;
import com.is4103.matchub.repository.OrganisationEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author ngjin
 */
@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private IndividualEntityRepository individualEntityRepository;

    @Autowired
    private OrganisationEntityRepository organisationEntityRepository;

    @Override
    public Page<IndividualEntity> individualLeaderboard(Pageable pageable) {
        return individualEntityRepository.individualLeaderboard(pageable);
    }

    @Override
    public Page<OrganisationEntity> organisationalLeaderboard(Pageable pageable) {
        return organisationEntityRepository.organisationalLeaderboard(pageable);
    }
}
