/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.is4103.matchub.enumeration.TaskStatusEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
    private LocalDateTime createdTime;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime expectedDeadline;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private TaskColumnEntity taskColumn;

    //Key: filename, Value = docPath
    @ElementCollection
    private Map<String, String> documents = new HashMap<>();

    //Key: label, Value = colour
    @ElementCollection
    private Map<String, String> labelAndColour = new HashMap<>();

    @Column(nullable = true)
    private Long taskLeaderId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<ProfileEntity> taskdoers = new ArrayList<>();

    public TaskEntity(String taskTitle, String taskDescription, LocalDateTime createdTime, LocalDateTime expectedDeadline) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.createdTime = createdTime;
        this.expectedDeadline = expectedDeadline;
    }

    
}
