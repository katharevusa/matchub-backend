/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.repository;

import com.is4103.matchub.entity.SDGTargetEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author ngjin
 */
public interface SDGTargetEntityRepository extends JpaRepository<SDGTargetEntity, Long> {

    SDGTargetEntity findBySdgTargetId(Long sdgTargetId);

    @Query(value = "SELECT DISTINCT sdgTarget FROM SDGTargetEntity sdgTarget "
            + "WHERE sdgTarget.sdgTargetId IN :sdgTargetIds")
    List<SDGTargetEntity> findSDGTargetsByIds(long[] sdgTargetIds);

}
