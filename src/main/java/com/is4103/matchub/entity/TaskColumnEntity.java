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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author longluqian
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskColumnEntity {
    
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskColumnId;
    
    @Column(nullable = false)
    @NotNull
    private String columnTitle;
    
    @Column(nullable = false)
    @NotNull
    private String columnDescription;
    
    @OneToMany(mappedBy = "taskColumn")
    private List<TaskEntity> listOfTasks = new ArrayList<>();

    public TaskColumnEntity(String columnTitle, String columnDescription) {
        this.columnTitle = columnTitle;
        this.columnDescription = columnDescription;
    }
    
    
    
}
