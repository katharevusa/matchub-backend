/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author ngjin
 */
public interface LeaderboardService {

    Page<IndividualEntity> individualLeaderboard(Pageable pageable);

    Page<OrganisationEntity> organisationalLeaderboard(Pageable pageable);

    Integer rank(Long accountId);
}
