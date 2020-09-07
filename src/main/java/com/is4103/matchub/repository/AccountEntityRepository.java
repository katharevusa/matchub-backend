package com.is4103.matchub.repository;

import com.is4103.matchub.entity.AccountEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountEntityRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByEmail(String email);

//    @Query(value = "SELECT ae FROM AccountEntity ae WHERE blob_uuid LIKE %:uuid%")
    Optional<AccountEntity> findByUuid(UUID uuid);

    List<AccountEntity> findAll();

    @Query("SELECT a FROM AccountEntity a WHERE a.accountLocked = FALSE AND a.accountExpired = FALSE AND a.disabled = FALSE AND a.isVerified = TRUE")
    List<AccountEntity> findAllActiveAccounts();

    @Query(value = "SELECT ae FROM AccountEntity ae WHERE ae.email LIKE %?1%",
            countQuery = "SELECT COUNT(ae) FROM AccountEntity ae WHERE ae.email LIKE %?1%")
    Page<AccountEntity> search(String search, Pageable pageable);
}
