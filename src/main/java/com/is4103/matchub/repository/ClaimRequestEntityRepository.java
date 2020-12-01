package com.is4103.matchub.repository;

import com.is4103.matchub.entity.ClaimRequestEntity;
import com.is4103.matchub.entity.ProfileEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author tjle2
 */
public interface ClaimRequestEntityRepository extends JpaRepository<ClaimRequestEntity, Long> {

    @Query(value = "SELECT cre FROM ClaimRequestEntity cre WHERE cre.claimRequestStatus = com.is4103.matchub.enumeration.ClaimRequestStatusEnum.PENDING AND cre <> ?1 AND cre.profile = ?2 ",
            countQuery = "SELECT COUNT(ce) FROM ClaimRequestEntity cre WHERE cre.claimRequestStatus = com.is4103.matchub.enumeration.ClaimRequestStatusEnum.PENDING AND cre <> ?1 AND cre.profile = ?2")
    List<ClaimRequestEntity> findRemainingPendingClaimRequests(ClaimRequestEntity claimRequest, ProfileEntity profile);
}
