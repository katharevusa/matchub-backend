/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
public class ChatEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @Column(nullable = false)
    @NotNull
    private String chatTitle;

    @Column(nullable = false)
    @NotNull
    private String chatDescription;

    //optional field as there will only be 1 official grp for each project
    @Column(nullable = true)
    private Long projectId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<ProfileEntity> chatMembers = new ArrayList<>();

    @OneToMany
    private List<ChannelEntity> channels = new ArrayList<>();

    public ChatEntity(String chatTitle, String chatDescription, Long projectId) {
        this.chatTitle = chatTitle;
        this.chatDescription = chatDescription;
        this.projectId = projectId;
    }

}
