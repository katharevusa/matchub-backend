/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.DonationOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author longluqian
 */
public interface DonationOptionEntityRepository extends JpaRepository<DonationOptionEntity, Long> {
    
}
