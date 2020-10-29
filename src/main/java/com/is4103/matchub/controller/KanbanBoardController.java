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
import com.is4103.matchub.exception.DeleteTaskColumnException;
import com.is4103.matchub.exception.DeleteTaskException;
import com.is4103.matchub.exception.KanbanBoardNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.RearrangeTaskException;
import com.is4103.matchub.exception.UpdateColumnException;
import com.is4103.matchub.exception.UpdateTaskException;
import com.is4103.matchub.service.KanbanBoardService;
import com.is4103.matchub.service.TaskColumnService;
import com.is4103.matchub.service.TaskService;
import com.is4103.matchub.vo.CommentVO;
import com.is4103.matchub.vo.CreateFullTaskVO;
import com.is4103.matchub.vo.DeleteColumnVO;
import com.is4103.matchub.vo.KanbanBoardVO;
import com.is4103.matchub.vo.TaskColumnVO;
import com.is4103.matchub.vo.CreateTaskVO;
import com.is4103.matchub.vo.RearrangeTaskVO;
import com.is4103.matchub.vo.UpdateLabelVO;
import com.is4103.matchub.vo.UpdateTaskVO;
import com.is4103.matchub.vo.UpdateFullTaskVO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
    public KanbanBoardEntity createKanbanBoard(@Valid @RequestBody KanbanBoardVO vo) {
        return kanbanBoardService.createKanbanBoard(vo);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/getKanbanBoardByKanbanBoardId")
    public KanbanBoardEntity getKanbanBoardByKanbanBoardId(@RequestParam(value = "kanbanBoardId", required = true) Long kanbanBoardId) throws KanbanBoardNotFoundException {
        return kanbanBoardService.getKanbanBoardByKanbanBoardId(kanbanBoardId);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/getKanbanBoardByChannelUid")
    public KanbanBoardEntity getKanbanBoardByChannelUid(@RequestParam(value = "channelUId", required = true) String ChannelUId) throws KanbanBoardNotFoundException {
        return kanbanBoardService.getKanbanBoardByChannelUid(ChannelUId);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllKanbanBoardByProjectId")
    public List<KanbanBoardEntity> getAllKanbanBoardByProjectId(@RequestParam(value = "projectId", required = true) Long projectId) throws ProjectNotFoundException {
        return kanbanBoardService.getAllKanbanBoardByProjectId(projectId);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllLabelsByKanbanBoardId")
    public Map<String, String> getAllLabelsByKanbanBoardId(@RequestParam(value = "kanbanBoardId", required = true) Long kanbanBoardId) {
        return kanbanBoardService.getAllLabelsByKanbanBoardId(kanbanBoardId);
    }
//****************************** Column Methods Below *************************

    @RequestMapping(method = RequestMethod.POST, value = "/createNewColumn")
    public KanbanBoardEntity createNewColumn(@Valid @RequestBody TaskColumnVO vo) throws UpdateColumnException {
        return taskColumnService.createNewColumn(vo);
    }

    // update only column title and decription
    @RequestMapping(method = RequestMethod.PUT, value = "/updateColumn")
    public KanbanBoardEntity updateColumn(@Valid @RequestBody TaskColumnVO vo) throws UpdateColumnException {
        return taskColumnService.updateColumn(vo);
    }

    // it's a PUT method with updated kanbanboard returned
    @RequestMapping(method = RequestMethod.PUT, value = "/deleteColumn")
    public KanbanBoardEntity deleteColumn(@Valid @RequestBody DeleteColumnVO vo) throws DeleteTaskColumnException {
        return taskColumnService.deleteColumn(vo);
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
    public KanbanBoardEntity rearrangeColumn(@RequestParam(value = "kanbanBoardId", required = true) Long kanbanBoardId,
            @RequestParam(value = "columnIdSequence", required = true) List<Long> columnIdSequence,
            @RequestParam(value = "editorId", required = true) Long editorId) throws UpdateColumnException {
        return taskColumnService.rearrangeColumn(kanbanBoardId, columnIdSequence, editorId);
    }
    //****************************** Task Methods Below *************************

    // basic information 
    @RequestMapping(method = RequestMethod.POST, value = "/createTask")
    public TaskEntity createTask(@Valid @RequestBody CreateTaskVO vo) throws CreateTaskException {
        return taskService.createTask(vo);
    }

    // create full task
    @RequestMapping(method = RequestMethod.POST, value = "/createFullTask")
    public TaskEntity createFullTask(@Valid @RequestBody CreateFullTaskVO createFullTaskVO) throws CreateTaskException, UpdateTaskException {

        // create initial task
        CreateTaskVO createTaskVO = new CreateTaskVO();
        createTaskVO.setTaskTitle(createFullTaskVO.getTaskTitle());
        if (createFullTaskVO.getTaskDescription() != null) {
            createTaskVO.setTaskDescription(createFullTaskVO.getTaskDescription());
        }

        if (createFullTaskVO.getExpectedDeadline() != null) {
            createTaskVO.setExpectedDeadline(createFullTaskVO.getExpectedDeadline());
        }

        if (createFullTaskVO.getTaskLeaderId() != null) {
            createTaskVO.setTaskLeaderId(createFullTaskVO.getTaskLeaderId());
        }

        createTaskVO.setTaskColumnId(createFullTaskVO.getTaskColumnId());
        createTaskVO.setTaskCreatorOrEditorId(createFullTaskVO.getTaskCreatorOrEditorId());
        createTaskVO.setKanbanboardId(createFullTaskVO.getKanbanboardId());

        TaskEntity task = taskService.createTask(createTaskVO);
        // update task doers
        taskService.updateTaskDoers(createFullTaskVO.getNewTaskDoerList(), task.getTaskId(), createFullTaskVO.getTaskCreatorOrEditorId(), createFullTaskVO.getKanbanboardId());

        // update task labels
        UpdateLabelVO updateLabelVO = new UpdateLabelVO();
        updateLabelVO.setLabelAndColour(createFullTaskVO.getLabelAndColour());
        updateLabelVO.setTaskId(task.getTaskId());

        return taskService.updateLabel(updateLabelVO);

    }

    // update basic informations: Title/description/leaderId
    @RequestMapping(method = RequestMethod.POST, value = "/updateTask")
    public TaskEntity updateTask(@Valid @RequestBody UpdateTaskVO vo) throws UpdateTaskException {
        return taskService.updateTask(vo);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateFullTask")
    public TaskEntity updateFullTask(@RequestBody @Valid UpdateFullTaskVO updateFullTaskVO) throws UpdateTaskException {
        UpdateTaskVO updateTaskVO = new UpdateTaskVO();
        updateFullTaskVO.updateTask(updateTaskVO);

        TaskEntity task = taskService.updateTask(updateTaskVO);
        taskService.updateTaskDoers(updateFullTaskVO.getNewTaskDoerList(), task.getTaskId(), updateFullTaskVO.getTaskCreatorOrEditorId(), updateFullTaskVO.getKanbanboardId());

        // update task labels
        UpdateLabelVO updateLabelVO = new UpdateLabelVO();
        updateLabelVO.setLabelAndColour(updateFullTaskVO.getLabelAndColour());
        updateLabelVO.setTaskId(task.getTaskId());

        return taskService.updateLabel(updateLabelVO);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getTasksByColumnId")
    public List<TaskEntity> getTasksByColumnId(@RequestParam(value = "columnId", required = true) Long columnId) {
        return taskService.getTasksByColumnId(columnId);
    }

    // get all tasks by channelUid
    @RequestMapping(method = RequestMethod.GET, value = "/getTasksByChannelUID")
    public List<TaskEntity> getTasksByChannelUID(@RequestParam(value = "channelUid", required = true) String channelUid) {
        return taskService.getTasksByChannelUID(channelUid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getTaskById")
    public TaskEntity getTaskById(@RequestParam(value = "taskId", required = true) Long taskId) {
        return taskService.getTaskById(taskId);

    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateTaskDoers")
    public TaskEntity updateTaskDoers(@RequestParam(value = "newTaskDoerList", defaultValue = "") List<Long> newTaskDoerList,
            @RequestParam(value = "taskId", required = true) Long taskId,
            @RequestParam(value = "updatorId", required = true) Long updatorId,
            @RequestParam(value = "kanbanBoardId", required = true) Long kanbanBoardId) throws UpdateTaskException {

        return taskService.updateTaskDoers(newTaskDoerList, taskId, updatorId, kanbanBoardId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/deleteTask")
    public TaskColumnEntity deleteTask(@RequestParam(value = "taskId", required = true) Long taskId,
            @RequestParam(value = "deletorId", required = true) Long deletorId,
            @RequestParam(value = "kanbanBoardId", required = true) Long kanbanBoardId) throws IOException, DeleteTaskException {
        return taskService.deleteTask(taskId, deletorId, kanbanBoardId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/rearrangeTasks")
    public KanbanBoardEntity rearrangeTasks(@RequestBody @Valid RearrangeTaskVO vo) throws RearrangeTaskException {
        return taskService.rearrangeTasks(vo);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addCommentToTask")
    public TaskEntity addCommentToTask(@RequestParam(value = "taskId", required = true) Long taskId, @Valid @RequestBody CommentVO vo) throws UpdateTaskException {
        return taskService.addCommentToTask(taskId, vo);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/deleteTaskComment")
    public TaskEntity deleteTaskComment(@RequestParam(value = "taskId", required = true) Long taskId, @RequestParam(value = "commentId", required = true) Long commentId) throws UpdateTaskException {
        return taskService.deleteTaskComment(taskId, commentId);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/getListOfCommentsByTaskId")
    public List<CommentEntity> getListOfCommentsByTaskId(@RequestParam(value = "taskId", required = true) Long taskId) {
        return taskService.getListOfCommentsByTaskId(taskId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateLabel")
    public TaskEntity updateLabel(@RequestBody @Valid UpdateLabelVO vo) {
        return taskService.updateLabel(vo);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/uploadDocuments")
    public TaskEntity uploadDocuments(@RequestParam(value = "taskId", required = true) Long taskId, MultipartFile[] documents) {
        return taskService.uploadDocuments(taskId, documents);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/deleteDocuments")
    public TaskEntity deleteDocuments(@RequestParam(value = "taskId", required = true) Long taskId, String[] docsToDelete) throws IOException, UpdateTaskException {
        return taskService.deleteDocuments(taskId, docsToDelete);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateTaskStatus")
    public TaskEntity updateTaskStatus(
            @RequestParam(value = "taskId", required = true) Long taskId,
            @RequestParam(value = "oldColumnId", required = true) Long oldColumnId,
            @RequestParam(value = "newColumnId", required = true) Long newColumnId) {
        return taskService.updateTaskStatus(taskId, oldColumnId, newColumnId);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getUnfinishedTasksByUserId")
    public List<TaskEntity> getUnfinishedTasksByUserId(@RequestParam(value = "kanbanboardId", required = true) Long kanbanboardId,
                                                       @RequestParam(value = "userId", required = true) Long userId){
        return taskService.getUnfinishedTasksByUserId(kanbanboardId, userId);
    }
     
    @RequestMapping(method = RequestMethod.GET, value = "/getUnfinishedTasksByKanbanBoardId")
     public List<TaskEntity> getUnfinishedTasksByKanbanBoardId(@RequestParam(value = "kanbanboardId", required = true) Long kanbanboardId){
         return taskService.getUnfinishedTasksByKanbanBoardId(kanbanboardId);
     }
     
     
     @RequestMapping(method = RequestMethod.GET, value = "/getUnfinishedTasksByChannelUId")
     public List<TaskEntity> getUnfinishedTasksByChannelUId(String channelUId){
         return taskService.getUnfinishedTasksByChannelUId(channelUId);
     }
}
