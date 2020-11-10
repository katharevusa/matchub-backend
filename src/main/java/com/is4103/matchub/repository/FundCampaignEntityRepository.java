/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.FundCampaignEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author longluqian
 */
public interface FundCampaignEntityRepository extends JpaRepository<FundCampaignEntity, Long>{
    
    
    @Query(value = "SELECT f FROM FundCampaignEntity f WHERE f.payeeId = :payeeId")      
    public List<FundCampaignEntity> getFundCampaignsByPayeeId(Long payeeId);
    
    @Query(value = "SELECT f FROM FundCampaignEntity f WHERE f.campaignTitle LIKE %?1% OR f.campaignDescription LIKE %?1%",
        countQuery = "SELECT COUNT(f) FROM FundCampaignEntity f WHERE f.campaignTitle LIKE %?1% OR f.campaignDescription LIKE %?1%")
    public Page<FundCampaignEntity> searchFundCampaignsByKeyword(String keyword, Pageable pageable);
        
      
    
}
