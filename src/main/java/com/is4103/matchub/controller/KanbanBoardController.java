/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.CommentEntity;
import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.entity.TaskEntity;
import com.is4103.matchub.exception.CreateTaskException;
import com.is4103.matchub.exception.DeleteTaskException;
import com.is4103.matchub.exception.KanbanBoardNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UpdateTaskException;
import com.is4103.matchub.service.KanbanBoardService;
import com.is4103.matchub.service.TaskColumnService;
import com.is4103.matchub.service.TaskService;
import com.is4103.matchub.vo.CommentVO;
import com.is4103.matchub.vo.KanbanBoardVO;
import com.is4103.matchub.vo.TaskColumnVO;
import com.is4103.matchub.vo.TaskVO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public KanbanBoardEntity deleteColumn(@RequestParam(value = "columnId", required = true) Long columnId) {
        return taskColumnService.deleteColumn(columnId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getColumnByColumnId")
    public TaskColumnEntity getColumnByColumnId(@RequestParam(value = "columnId", required = true) Long columnId) {
        return taskColumnService.getColumnByColumnId(columnId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getColumnsByKanbanBoardId")
    public List<TaskColumnEntity> getColumnsByKanbanBoardId(@RequestParam(value = "kanbanBoardId", required = true) Long kanbanBoardId) {
        return taskColumnService.getColumnsByKanbanBoardId(kanbanBoardId);

    }
    @RequestMapping(method = RequestMethod.PUT, value = "/rearrangeColumn")
     public KanbanBoardEntity rearrangeColumn(Long kanbanBoardId, List<Long> columnIdSequence){
         return taskColumnService.rearrangeColumn(kanbanBoardId, columnIdSequence);
     }
    //****************************** Task Methods Below *************************

    // basic information 
    @RequestMapping(method = RequestMethod.POST, value = "/createTask")
    public TaskEntity createTask(TaskVO vo) throws CreateTaskException {
        return taskService.createTask(vo);
    }

    // update basic informations: Title/description/leaderId
    public TaskEntity updateTask(TaskVO vo) throws UpdateTaskException {
        return taskService.updateTask(vo);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getTasksByColumnId")
    public List<TaskEntity> getTasksByColumnId(@RequestParam(value = "columnId", required = true)Long columnId) {
        return taskService.getTasksByColumnId(columnId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getTaskById")
    public TaskEntity getTaskById(@RequestParam(value = "taskId", required = true)Long taskId) {
        return taskService.getTaskById(taskId);

    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateTaskDoers")
    public TaskEntity updateTaskDoers(@RequestParam(value = "newTaskDoerList", required = true)List<Long> newTaskDoerList, @RequestParam(value = "taskId", required = true)Long taskId,@RequestParam(value = "updatorId", required = true) Long updatorId) throws UpdateTaskException {
        return taskService.updateTaskDoers(newTaskDoerList, taskId, updatorId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/deleteTask")
    public TaskColumnEntity deleteTask(@RequestParam(value = "taskId", required = true)Long taskId,@RequestParam(value = "deletorId", required = true) Long deletorId) throws IOException, DeleteTaskException {
        return taskService.deleteTask(taskId, deletorId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rearrangeTasks")
    public KanbanBoardEntity rearrangeTasks(Map<Long, List<Long>> columnIdAndTaskIdSequence, @RequestParam(value = "kanbanBoardId", required = true)Long kanbanBoardId,@RequestParam(value = "arrangerId", required = true) Long arrangerId) throws UpdateTaskException {
        return taskService.rearrangeTasks(columnIdAndTaskIdSequence, kanbanBoardId, arrangerId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addCommentToTask")
    public TaskEntity addCommentToTask(@RequestParam(value = "taskId", required = true)Long taskId, CommentVO vo) throws UpdateTaskException {
        return taskService.addCommentToTask(taskId, vo);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/deleteTaskComment")
    public TaskEntity deleteTaskComment(@RequestParam(value = "taskId", required = true)Long taskId,@RequestParam(value = "commentId", required = true) Long commentId) throws UpdateTaskException {
        return taskService.deleteTaskComment(taskId, commentId);

    }

    @RequestMapping(method = RequestMethod.POST, value = "/getListOfCommentsByTaskId")
    public List<CommentEntity> getListOfCommentsByTaskId(@RequestParam(value = "taskId", required = true)Long taskId) {
        return taskService.getListOfCommentsByTaskId(taskId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateLabel")
    public TaskEntity updateLabel(Map<String, String> labelAndColour,@RequestParam(value = "taskId", required = true) Long taskId) {
        return taskService.updateLabel(labelAndColour, taskId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/uploadDocuments")
    public TaskEntity uploadDocuments(@RequestParam(value = "taskId", required = true)Long taskId, MultipartFile[] documents) {
        return taskService.uploadDocuments(taskId, documents);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/deleteDocuments")
    public TaskEntity deleteDocuments(@RequestParam(value = "taskId", required = true)Long taskId, String[] docsToDelete) throws IOException, UpdateTaskException {
        return taskService.deleteDocuments(taskId, docsToDelete);
    }
}
