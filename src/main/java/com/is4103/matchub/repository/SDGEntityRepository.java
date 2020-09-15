/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.SDGEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author ngjin
 */
public interface SDGEntityRepository extends JpaRepository<SDGEntity, Long> {
    
    SDGEntity findBySdgId(Long sdgId);
    
    SDGEntity findBySdgName(String name);

}
