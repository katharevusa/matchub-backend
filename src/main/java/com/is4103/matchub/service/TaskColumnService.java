/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.exception.DeleteTaskColumnException;
import com.is4103.matchub.exception.UpdateColumnException;
import com.is4103.matchub.vo.DeleteColumnVO;
import com.is4103.matchub.vo.TaskColumnVO;
import java.util.List;

/**
 *
 * @author longluqian
 */
public interface TaskColumnService {

    public KanbanBoardEntity createNewColumn(TaskColumnVO vo) throws UpdateColumnException;

    public KanbanBoardEntity updateColumn(TaskColumnVO vo)throws UpdateColumnException;

    public KanbanBoardEntity deleteColumn(DeleteColumnVO deleteVO)throws DeleteTaskColumnException;
    
    public TaskColumnEntity getColumnByColumnId(Long columnId);
    
    public List<TaskColumnEntity> getColumnsByKanbanBoardId(Long kanbanBoardId);
    
   public KanbanBoardEntity rearrangeColumn(Long kanbanBoardId, List<Long> columnIdSequence, Long editorId) throws UpdateColumnException;

}
