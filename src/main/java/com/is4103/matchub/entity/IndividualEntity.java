package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.GenderEnum;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualEntity extends ProfileEntity {

    @Column(nullable = false)
    @NotNull
    private String firstName;

    @Column(nullable = false)
    @NotNull
    private String lastName;

    @Column(nullable = true)
//    @NotNull
    private GenderEnum genderEnum;

    @Column(nullable = true)
    private String profileDescription;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> projectFollowing = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> skillSet = new HashSet<>();

    public IndividualEntity(String email, String password, String firstName, String lastName, GenderEnum genderEnum) {
        super(email, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.genderEnum = genderEnum;
    }

}
