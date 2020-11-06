/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author longluqian
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PostEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false, length = 1000)
    @NotNull
    private String content;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timeCreated;

    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn()
    private List<String> photos = new ArrayList<>();

    @Column(nullable = true)
    private Long originalPostId;

    @Column(nullable = false)
    @NotNull
    private Long likes = 0L;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties(value = {"posts"})
    private ProfileEntity postCreator;

    @OneToMany
    private List<CommentEntity> listOfComments = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> likedUsersId = new HashSet<>();

    public PostEntity(String content, LocalDateTime timeCreated, List<String> photos, Long originalPostId, ProfileEntity postCreator) {
        this.content = content;
        this.timeCreated = timeCreated;
        this.photos = photos;
        this.originalPostId = originalPostId;
//        this.previousPostId = previousPostId;
        this.postCreator = postCreator;
    }

}
