package com.is4103.matchub.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

    @Column(nullable = true)
    private String organizationDescription;

    @Column
    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> employees = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ProjectEntity> projects = new ArrayList<>();

    @OneToMany
//        (mappedBy = "stakeholder")
    private List<IndividualEntity> kah = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    private IndividualEntity admin;

    public StakeholderEntity(String username, String password, String email, String organizationName) {
        super(username, password, email);
        this.organizationName = organizationName;
    }

    public StakeholderEntity(String username, String password, String email, String organizationName, String organizationDescription, String address, IndividualEntity admin) {
        super(username, password, email);
        this.organizationName = organizationName;
        this.organizationDescription = organizationDescription;
        this.address = address;
        this.admin = admin;
    }

}
