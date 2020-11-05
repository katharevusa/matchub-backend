/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.FundCampaignEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author longluqian
 */
public interface FundCampaignEntityRepository extends JpaRepository<FundCampaignEntity, Long>{
    
    
    @Query(value = "SELECT f FROM FundCampaignEntity f WHERE f.payeeId = :payeeId")      
    public List<FundCampaignEntity> getFundCampaignsByPayeeId(Long payeeId);
    
    
}
