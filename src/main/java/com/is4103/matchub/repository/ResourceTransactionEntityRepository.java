/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.ResourceTransactionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author longluqian
 */
public interface ResourceTransactionEntityRepository extends JpaRepository<ResourceTransactionEntity, Long>{
    
    
    @Query(value = "SELECT rt FROM ResourceTransactionEntity rt WHERE rt.payerId = :payerId")
    public List<ResourceTransactionEntity> getResourceTransactionsByPayerId(Long payerId);
}
