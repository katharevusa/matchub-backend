/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.DonationEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author longluqian
 */
public interface DonationEntityRepository extends JpaRepository<DonationEntity, Long> {
    
      @Query(value = "SELECT DISTINCT de FROM DonationEntity de WHERE de.donationTime BETWEEN ?1 AND ?2 ")
    List<DonationEntity> findDonationsByTransactionTime(LocalDateTime from, LocalDateTime to);
}
