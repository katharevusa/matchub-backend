package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.RoleEnum;
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
    private String organizationDescription;

    @Column
    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> employees = new HashSet<>();
    
     @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> kahs = new HashSet<>();
     
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> areasOfExpertise = new HashSet<>();




    public OrganisationEntity(String password, String email, String organizationName, String organizationDescription, String address, RoleEnum role) {
        super(password, email, role);
        this.organizationName = organizationName;
        this.organizationDescription = organizationDescription;
        this.address = address;
    }

}
