package com.is4103.matchub.repository;

import com.is4103.matchub.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface AccountEntityRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByEmail(String email);

    @Query(value = "SELECT ae FROM AccountEntity ae WHERE ae.email LIKE %?1%",
            countQuery = "SELECT COUNT(ae) FROM AccountEntity ae WHERE ae.email LIKE %?1%")
    Page<AccountEntity> search(String search, Pageable pageable);
}