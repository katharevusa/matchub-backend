/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.vo.TaskColumnVO;
import java.util.List;

/**
 *
 * @author longluqian
 */
public interface TaskColumnService {

    public KanbanBoardEntity createNewColumn(TaskColumnVO vo);

    public KanbanBoardEntity updateColumn(TaskColumnVO vo);

    public KanbanBoardEntity deleteColumn(Long columnId);
    
    public TaskColumnEntity getColumnByColumnId(Long columnId);
    
    public List<TaskColumnEntity> getColumnsByKanbanBoardId(Long kanbanBoardId);
    
    public KanbanBoardEntity rearrangeColumn(Long kanbanBoardId, List<Long> columnIdSequence);

}
