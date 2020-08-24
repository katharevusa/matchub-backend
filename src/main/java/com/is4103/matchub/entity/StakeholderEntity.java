package com.is4103.matchub.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tjle2
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StakeholderEntity extends ProfileEntity {

    @Column(nullable = false)
    @NotNull
    private String organizationName;

    @Column
    private String organizationDescription;

    @Column
    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> employees = new HashSet<>();

    public StakeholderEntity(String username, String password, String email, String organizationName) {
        super(username, password, email);
        this.organizationName = organizationName;
    }
}
