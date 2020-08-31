package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.is4103.matchub.enumeration.RoleEnum;
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

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false, unique = true)
    @NotNull
    private UUID uuid;

    @Column(nullable = false)
    @NotNull
    @JsonIgnore
    private String password;

    @Column(nullable = false, unique = true)
    @NotNull
    private String email;

    @Column(nullable = false)
    @NotNull
    private Boolean accountLocked = false;

    @Column(nullable = false)
    @NotNull
    private Boolean accountExpired = false;

    @Column(nullable = false)
    @NotNull
    private Boolean disabled = true;
    
    @Column(nullable = false)
    @NotNull
    private RoleEnum role;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isVerified = false;
    

  
    public AccountEntity(String password, String email, RoleEnum role) {
        this.uuid = UUID.randomUUID();
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
