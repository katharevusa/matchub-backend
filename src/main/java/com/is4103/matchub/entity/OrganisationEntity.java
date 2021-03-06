package com.is4103.matchub.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 *
 * @author tjle2
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationEntity extends ProfileEntity {

    @Column(nullable = false)
    @NotNull
    private String organizationName;

    @Column(nullable = true)
    @Nullable
    private String organizationDescription;

    @Column(nullable = true)
    @Nullable
    private String address;
    
    @Column(nullable = true)
    @Nullable
    private String verificationDoc;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> employees = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> kahs = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> areasOfExpertise = new HashSet<>();

    public OrganisationEntity(String email, String password, String organizationName, String organizationDescription, String address) {
        super(email, password);
        this.organizationName = organizationName;
        this.organizationDescription = organizationDescription;
        this.address = address;
    }

}
