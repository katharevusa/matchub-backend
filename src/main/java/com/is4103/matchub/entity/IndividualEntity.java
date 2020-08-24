package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.GenderEnum;
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

    @Column(nullable = false)
    @NotNull
    private GenderEnum genderEnum;

    @Column
    private String profileDescription;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> projectFollowing = new HashSet<>();

    public IndividualEntity(String username, String password, String email, String firstName, String lastName, GenderEnum genderEnum) {
        super(username, password, email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.genderEnum = genderEnum;
    }
}
