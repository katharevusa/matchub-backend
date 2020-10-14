/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.exception.KanbanBoardNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.repository.KanbanBoardEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.vo.KanbanBoardVO;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class KanbanBoardServiceImpl implements KanbanBoardService {

    @Autowired
    private KanbanBoardEntityRepository kanbanBoardEntityRepository;
    
    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Override
    public KanbanBoardEntity createKanbanBoard(KanbanBoardVO vo) {
        KanbanBoardEntity kanbanBoard = new KanbanBoardEntity();
        vo.createNewKanbanBoard(kanbanBoard);
        return kanbanBoardEntityRepository.saveAndFlush(kanbanBoard);
    }

    @Override
    public KanbanBoardEntity updateKanbanBoard(KanbanBoardVO vo) {
        KanbanBoardEntity kanbanBoard = new KanbanBoardEntity();
        vo.updateNewKanbanBoard(kanbanBoard);
        return kanbanBoardEntityRepository.saveAndFlush(kanbanBoard);
    }

    @Override
    public KanbanBoardEntity getKanbanBoardByKanbanBoardId(Long id) throws KanbanBoardNotFoundException {
        Optional<KanbanBoardEntity> opt = kanbanBoardEntityRepository.findById(id);
        if (!opt.isPresent()) {
            throw new KanbanBoardNotFoundException("Kanban Board not found");
        }
        KanbanBoardEntity kanbanBoard = opt.get();

        for (TaskColumnEntity t : kanbanBoard.getTaskColumns()) {
            t.getListOfTasks();
        }
        return kanbanBoard;
    }

    @Override
    public KanbanBoardEntity getKanbanBoardByChannelUid(String Uid) throws KanbanBoardNotFoundException {
        Optional<KanbanBoardEntity> opt = kanbanBoardEntityRepository.findByChannelUId(Uid);
        if (!opt.isPresent()) {
            throw new KanbanBoardNotFoundException("Kanban Board not found");
        }
        KanbanBoardEntity kanbanBoard = opt.get();

        for (TaskColumnEntity t : kanbanBoard.getTaskColumns()) {
            t.getListOfTasks();
        }
        return kanbanBoard;
    }
    
    @Override
    public List<KanbanBoardEntity> getAllKanbanBoardByProjectId(Long projectId)throws ProjectNotFoundException{
        Optional<ProjectEntity> projectOpt  = projectEntityRepository.findById(projectId);
        if(!projectOpt.isPresent()){
            throw new ProjectNotFoundException("Project not found!");
            
        } 
        List<KanbanBoardEntity> list = kanbanBoardEntityRepository.findKanbanBoardsByProjectId(projectId);
        for(KanbanBoardEntity k : list){
            for(TaskColumnEntity t : k.getTaskColumns()){
                t.getListOfTasks();
            }        
        }     
        return list;
        
    }

}
