/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.TaskStatusEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class TaskEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @Column(nullable = false)
    @NotNull
    private String taskTitle;

    @Column(nullable = false)
    @NotNull
    private String taskDescription;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime expectedStartTime;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime expectedEndTime;

    @Column(nullable = false)
    @NotNull
    private TaskStatusEnum statusEnum;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private MilestoneEntity milestone;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<ProfileEntity> profiles = new ArrayList<>();

    @OneToMany
    private List<DocumentEntity> documents = new ArrayList<>();

    public TaskEntity(String taskTitle, String taskDescription, LocalDateTime expectedStartTime, LocalDateTime expectedEndTime, TaskStatusEnum statusEnum) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.expectedStartTime = expectedStartTime;
        this.expectedEndTime = expectedEndTime;
        this.statusEnum = statusEnum;
    }

}
