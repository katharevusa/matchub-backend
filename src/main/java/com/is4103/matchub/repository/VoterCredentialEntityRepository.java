package com.is4103.matchub.repository;

import com.is4103.matchub.entity.CompetitionEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.VoterCredentialEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author tjle2
 */
public interface VoterCredentialEntityRepository extends JpaRepository<VoterCredentialEntity, Long> {

    @Query(value = "SELECT vce FROM VoterCredentialEntity vce WHERE vce.voter = ?1 AND vce.competition = ?2",
            countQuery = "SELECT COUNT(vce) FROM VoterCredentialEntity vce WHERE ve.voter = ?1 AND vce.competition = ?2")
    Optional<VoterCredentialEntity> findExistingVoterCredentialDetails(ProfileEntity profile, CompetitionEntity competition);

    Optional<VoterCredentialEntity> findByVoterSecret(String voterSecret);
}
