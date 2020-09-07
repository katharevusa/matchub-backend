package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tjle2
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class AccountEntity {

    public static final String ROLE_SUPERUSER = "SUPERUSER";
    public static final String ROLE_SYSADMIN = "SYSADMIN";
    public static final String ROLE_USER = "USER";

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

//    @Column(nullable = false, unique = true)
    @Column(name = "apiKey", updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @NotNull
    private UUID uuid;

    @Column(nullable = false, unique = true)
    @NotNull
    private String email;

    @Column(nullable = false)
    @NotNull
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    @NotNull
    private Boolean accountLocked = false;

    @Column(nullable = false)
    @NotNull
    private Boolean accountExpired = false;

    @Column(nullable = false)
    @NotNull
    private Boolean disabled = true;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    @Column(nullable = false)
    @NotNull
    private Boolean isVerified = false;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime joinDate;

    public AccountEntity(String email, String password) {
        this.uuid = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.joinDate = LocalDateTime.now();
    }

}
