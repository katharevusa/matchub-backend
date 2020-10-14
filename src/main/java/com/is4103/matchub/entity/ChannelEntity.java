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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
public class ChannelEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelId;

    @Column(nullable = false)
    @NotNull
    private String channelTitle;

    @Column(nullable = false)
    @NotNull
    private String channelDescription;

    
    @ManyToMany
    private List<ProfileEntity> channelMembers = new ArrayList<>();
    
    @ManyToMany
    private List<ProfileEntity> channelAdmins = new ArrayList<>();
    
    @OneToOne
    private KanbanBoardEntity kanbanBoard;
    
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true)
    private ProjectEntity project;

    public ChannelEntity(String channelTitle, String channelDescription) {
        this.channelTitle = channelTitle;
        this.channelDescription = channelDescription;
    }

}
