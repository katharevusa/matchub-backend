/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.exception.KanbanBoardNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.vo.KanbanBoardVO;
import java.util.List;

/**
 *
 * @author longluqian
 */
public interface KanbanBoardService {

    public KanbanBoardEntity createKanbanBoard(KanbanBoardVO vo);

    public KanbanBoardEntity getKanbanBoardByKanbanBoardId(Long id) throws KanbanBoardNotFoundException;

    public KanbanBoardEntity getKanbanBoardByChannelUid(String Uid) throws KanbanBoardNotFoundException;
    
    public List<KanbanBoardEntity> getAllKanbanBoardByProjectId(Long projectId)throws ProjectNotFoundException;

}
