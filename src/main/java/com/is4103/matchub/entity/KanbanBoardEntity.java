/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
public class KanbanBoardEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kanbanBoardId;

    @Column(nullable = false)
    @NotNull
    private String kanbanBoardTitle;

    @Column(nullable = false)
    @NotNull
    private String kanbanBoardDescription;

    @OneToMany(mappedBy = "kanbanBoard")
    private List<MilestoneEntity> milestones = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private ProjectEntity project;

    public KanbanBoardEntity(String kanbanBoardTitle, String kanbanBoardDescription) {
        this.kanbanBoardTitle = kanbanBoardTitle;
        this.kanbanBoardDescription = kanbanBoardDescription;
    }

}
