/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.entity.TaskEntity;
import com.is4103.matchub.exception.KanbanBoardNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UpdateColumnException;
import com.is4103.matchub.repository.KanbanBoardEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.TaskColumnEntityRepository;
import com.is4103.matchub.vo.KanbanBoardVO;
import com.is4103.matchub.vo.TaskColumnVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    
    @Autowired
    private TaskColumnEntityRepository taskColumnEntityRepository;
    
    
 

    @Override
    public KanbanBoardEntity createKanbanBoard(KanbanBoardVO vo) {
        KanbanBoardEntity kanbanBoard = new KanbanBoardEntity();
        vo.createNewKanbanBoard(kanbanBoard);
        kanbanBoard = kanbanBoardEntityRepository.saveAndFlush(kanbanBoard);
        System.out.println("*****"+kanbanBoard);
        
        // init new default columns
        TaskColumnEntity newColumn = new TaskColumnEntity();
        newColumn.setColumnTitle("New");
        newColumn.setColumnDescription("All new tasks");
        newColumn.setKanbanBoardId(kanbanBoard.getKanbanBoardId());
        newColumn = taskColumnEntityRepository.saveAndFlush(newColumn);
        kanbanBoard.getTaskColumns().add(newColumn);
        
        
        TaskColumnEntity inProgressColumn = new TaskColumnEntity();
        inProgressColumn.setColumnTitle("In progress");
        inProgressColumn.setColumnDescription("All in progress tasks");
        inProgressColumn.setKanbanBoardId(kanbanBoard.getKanbanBoardId());
        inProgressColumn = taskColumnEntityRepository.saveAndFlush(inProgressColumn);
        kanbanBoard.getTaskColumns().add(inProgressColumn);
        
        
        TaskColumnEntity doneColumn = new TaskColumnEntity();
        doneColumn.setColumnTitle("Done");
        doneColumn.setColumnDescription("All done tasks. There is only one column with done status for one kanban board.");
        doneColumn.setKanbanBoardId(kanbanBoard.getKanbanBoardId());
        doneColumn.setDone(true);
        doneColumn = taskColumnEntityRepository.saveAndFlush(doneColumn);
        kanbanBoard.getTaskColumns().add(doneColumn);
        
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
    
    @Override
    public Map<String, String> getAllLabelsByKanbanBoardId(Long kanbanBoardId){
        Map<String, String> labelAndColour = new HashMap<>();
        KanbanBoardEntity kanbanBoard = kanbanBoardEntityRepository.findById(kanbanBoardId).get();
        for(TaskColumnEntity t : kanbanBoard.getTaskColumns()){
            for(TaskEntity te : t.getListOfTasks()){
                for(Map.Entry<String, String> i : labelAndColour.entrySet()){
                    if(!labelAndColour.containsKey(i.getKey())){
                        labelAndColour.put(i.getKey(), i.getValue());
                    }
                }
            }
        }
        
        return labelAndColour;
    }
            

}
