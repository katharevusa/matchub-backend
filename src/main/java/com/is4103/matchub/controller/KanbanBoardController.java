/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.exception.KanbanBoardNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.service.KanbanBoardService;
import com.is4103.matchub.service.TaskColumnService;
import com.is4103.matchub.service.TaskService;
import com.is4103.matchub.vo.KanbanBoardVO;
import com.is4103.matchub.vo.TaskColumnVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author longluqian
 */
@RestController
@RequestMapping("/authenticated")
public class KanbanBoardController {

    @Autowired
    KanbanBoardService kanbanBoardService;

    @Autowired
    TaskColumnService taskColumnService;

    @Autowired
    TaskService taskService;

    @RequestMapping(method = RequestMethod.POST, value = "/createKanbanBoard")
    public KanbanBoardEntity createKanbanBoard(KanbanBoardVO vo) {
        return kanbanBoardService.createKanbanBoard(vo);

    }

    // this update is only for kanbanboard title, descriptions only
    @RequestMapping(method = RequestMethod.POST, value = "/updateKanbanBoard")
    public KanbanBoardEntity updateKanbanBoard(KanbanBoardVO vo) {
        return kanbanBoardService.updateKanbanBoard(vo);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/getKanbanBoardByKanbanBoardId")
    public KanbanBoardEntity getKanbanBoardByKanbanBoardId(@RequestParam(value = "kanbanBoardId", required = true) Long kanbanBoardId) throws KanbanBoardNotFoundException {
        return kanbanBoardService.getKanbanBoardByKanbanBoardId(kanbanBoardId);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/getKanbanBoardByChannelUid")
    public KanbanBoardEntity getKanbanBoardByChannelUid(@RequestParam(value = "ChannelUId", required = true) String ChannelUId) throws KanbanBoardNotFoundException {
        return kanbanBoardService.getKanbanBoardByChannelUid(ChannelUId);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllKanbanBoardByProjectId")
    public List<KanbanBoardEntity> getAllKanbanBoardByProjectId(@RequestParam(value = "projectId", required = true) Long projectId) throws ProjectNotFoundException {
        return kanbanBoardService.getAllKanbanBoardByProjectId(projectId);

    }
//****************************** Column Methods Below *************************

    @RequestMapping(method = RequestMethod.POST, value = "/createNewColumn")
    public KanbanBoardEntity createNewColumn(TaskColumnVO vo) {
        return taskColumnService.createNewColumn(vo);
    }

    // update only column title and decription
    @RequestMapping(method = RequestMethod.PUT, value = "/updateColumn")
    public KanbanBoardEntity updateColumn(TaskColumnVO vo) {
        return taskColumnService.updateColumn(vo);
    }

    // it's a PUT method with updated kanbanboard returned
    @RequestMapping(method = RequestMethod.PUT, value = "/deleteColumn")
    public KanbanBoardEntity deleteColumn(@RequestParam(value = "columnId", required = true)Long columnId) {
        return taskColumnService.deleteColumn(columnId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getColumnByColumnId")
    public TaskColumnEntity getColumnByColumnId(@RequestParam(value = "columnId", required = true)Long columnId) {
        return taskColumnService.getColumnByColumnId(columnId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getColumnsByKanbanBoardId")
    public List<TaskColumnEntity> getColumnsByKanbanBoardId(@RequestParam(value = "kanbanBoardId", required = true)Long kanbanBoardId) {
        return taskColumnService.getColumnsByKanbanBoardId(kanbanBoardId);

    }
    //****************************** Task Methods Below *************************

}
