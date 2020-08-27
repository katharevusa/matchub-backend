/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ngjin
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pageId;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timeCreated;

    @Column(nullable = false)
    @NotNull
    private String content;

    @Column(nullable = false)
    @NotNull
    private Long pageCreator;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<PageEntity> parentPages = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<PageEntity> childPages = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ProjectEntity project;

    public PageEntity(LocalDateTime timeCreated, String content, Long pageCreator, ProjectEntity project) {
        this.timeCreated = timeCreated;
        this.content = content;
        this.pageCreator = pageCreator;
        this.project = project;
    }

}
