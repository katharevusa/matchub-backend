/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.entity.TaskEntity;
import com.is4103.matchub.exception.DeleteTaskColumnException;
import com.is4103.matchub.exception.UpdateColumnException;
import com.is4103.matchub.repository.KanbanBoardEntityRepository;
import com.is4103.matchub.repository.TaskColumnEntityRepository;
import com.is4103.matchub.repository.TaskEntityRepository;
import com.is4103.matchub.vo.ChannelDetailsVO;
import com.is4103.matchub.vo.DeleteColumnVO;
import com.is4103.matchub.vo.TaskColumnVO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class TaskColumnServiceImpl implements TaskColumnService {
    //create taskColumn

    @Autowired
    TaskColumnEntityRepository taskColumnEntityRepository;

    @Autowired
    KanbanBoardEntityRepository kanbanBoardEntityRepository;

    @Autowired
    FirebaseService firebaseService;

    @Autowired
    TaskService taskService;

    @Autowired
    TaskEntityRepository taskEntityRepository;

    @Override
    public KanbanBoardEntity createNewColumn(TaskColumnVO vo) throws UpdateColumnException {
        TaskColumnEntity newTaskColumn = new TaskColumnEntity();
        vo.createNewTaskColumn(newTaskColumn);
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(newTaskColumn.getKanbanBoardId()).get();

//        // checking only channel admins can create column 
        ChannelDetailsVO channelDetails = firebaseService.getChannelDetails(kanbanBoardEntity.getChannelUid());
        if (!channelDetails.getAdminIds().contains(vo.getEditorId())) {
            throw new UpdateColumnException("Only channel admin can create column");
        }

        newTaskColumn = taskColumnEntityRepository.saveAndFlush(newTaskColumn);
        kanbanBoardEntity.getTaskColumns().add(newTaskColumn);
        kanbanBoardEntity = kanbanBoardEntityRepository.saveAndFlush(kanbanBoardEntity);
        return kanbanBoardEntity;
    }

    @Override
    public KanbanBoardEntity updateColumn(TaskColumnVO vo) throws UpdateColumnException {
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(vo.getColumnId()).get();
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(taskColumn.getKanbanBoardId()).get();
//        // checking only channel admins can update column  
        ChannelDetailsVO channelDetails = firebaseService.getChannelDetails(kanbanBoardEntity.getChannelUid());
        if (!channelDetails.getAdminIds().contains(vo.getEditorId())) {
            throw new UpdateColumnException("Only channel admin can create column");
        }

        vo.updateTaskColumn(taskColumn);
        taskColumn = taskColumnEntityRepository.saveAndFlush(taskColumn);
        kanbanBoardEntityRepository.flush();

        return kanbanBoardEntityRepository.findById(taskColumn.getKanbanBoardId()).get();
    }

    @Override
    public KanbanBoardEntity deleteColumn(DeleteColumnVO deleteVO) throws DeleteTaskColumnException {

       
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(deleteVO.getDeleteColumnId()).get();
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(taskColumn.getKanbanBoardId()).get();
        
//         // checking only channel admins can delete column 
        ChannelDetailsVO channelDetails = firebaseService.getChannelDetails(kanbanBoardEntity.getChannelUid());
        if (!channelDetails.getAdminIds().contains(deleteVO.getDeletorId())) {
            throw new DeleteTaskColumnException("Only channel admin can delete column");
        }

        if (taskColumn.isDone()) {
            throw new DeleteTaskColumnException("Can not delete done column");
        }

        //rearrange tasks if column contain task
        if (!taskColumn.getListOfTasks().isEmpty()) {
            TaskColumnEntity newColumn = taskColumnEntityRepository.findById(deleteVO.getTransferredColumnId()).get();
            System.out.println("com.is4103.matchub.service.TaskColumnServiceImpl.deleteColumn()start"+newColumn.getListOfTasks().size());
            for (TaskEntity t : taskColumn.getListOfTasks()) {
                t.setTaskColumn(newColumn);
                newColumn.getListOfTasks().add(t);
            }
            taskColumn.setListOfTasks(new ArrayList<>());
           newColumn = taskColumnEntityRepository.saveAndFlush(newColumn);
            System.out.println("com.is4103.matchub.service.TaskColumnServiceImpl.deleteColumn()end"+newColumn.getListOfTasks().size());
        }
        //remove column from kanban board 
        kanbanBoardEntity.getTaskColumns().remove(taskColumn);
        // delete task column   
        taskColumnEntityRepository.delete(taskColumn);
        taskColumnEntityRepository.flush();
        kanbanBoardEntity = kanbanBoardEntityRepository.saveAndFlush(kanbanBoardEntity);
        return kanbanBoardEntity;
    }

    @Override
    public TaskColumnEntity getColumnByColumnId(Long columnId) {
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(columnId).get();
        taskColumn.getListOfTasks();
        return taskColumn;
    }

    @Override
    public List<TaskColumnEntity> getColumnsByKanbanBoardId(Long kanbanBoardId) {
        KanbanBoardEntity kanbanBoard = kanbanBoardEntityRepository.findById(kanbanBoardId).get();
        return kanbanBoard.getTaskColumns();
    }

    @Override
    public KanbanBoardEntity rearrangeColumn(Long kanbanBoardId, List<Long> columnIdSequence, Long editorId) throws UpdateColumnException {
//        // only channel admins can rearrangecolumn
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(kanbanBoardId).get();
        ChannelDetailsVO channelDetails = firebaseService.getChannelDetails(kanbanBoardEntity.getChannelUid());
        if (!channelDetails.getAdminIds().contains(editorId)) {
            throw new UpdateColumnException("Only channel admin can rearrange column");
        }

        KanbanBoardEntity kanbanBoard = kanbanBoardEntityRepository.findById(kanbanBoardId).get();
        kanbanBoard.setTaskColumns(new ArrayList<>());
        
        for (Long columnId : columnIdSequence) {
            kanbanBoard.getTaskColumns().add(taskColumnEntityRepository.findById(columnId).get());

        }
        kanbanBoard = kanbanBoardEntityRepository.saveAndFlush(kanbanBoard);

        return kanbanBoard;

    }

}
