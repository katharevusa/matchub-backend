/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.CommentEntity;
import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.entity.TaskEntity;
import com.is4103.matchub.exception.CreateTaskException;
import com.is4103.matchub.exception.DeleteTaskException;
import com.is4103.matchub.exception.RearrangeTaskException;
import com.is4103.matchub.exception.UpdateTaskException;
import com.is4103.matchub.vo.CommentVO;
import com.is4103.matchub.vo.CreateTaskVO;
import com.is4103.matchub.vo.RearrangeTaskVO;
import com.is4103.matchub.vo.UpdateLabelVO;
import com.is4103.matchub.vo.UpdateTaskVO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author longluqian
 */
public interface TaskService {

    public TaskEntity createTask(CreateTaskVO vo) throws CreateTaskException;

    public List<TaskEntity> getTasksByColumnId(Long columnId);

    public TaskEntity getTaskById(Long taskId);

    public TaskEntity updateTask(UpdateTaskVO vo) throws UpdateTaskException;

    public TaskEntity updateTaskDoers(List<Long> newTaskDoerList, Long taskId, Long updatorId, Long kanbanBoardId) throws UpdateTaskException;

       public TaskColumnEntity deleteTask(Long taskId, Long deletorId, Long kanbanBoardId) throws IOException, DeleteTaskException;
    
     public KanbanBoardEntity rearrangeTasks(RearrangeTaskVO vo) throws RearrangeTaskException;
      
     public TaskEntity addCommentToTask(Long taskId, CommentVO vo)throws UpdateTaskException;
     
     public TaskEntity deleteTaskComment(Long taskId, Long commentId) throws UpdateTaskException;
     
     public List<CommentEntity> getListOfCommentsByTaskId(Long taskId);
     
     public TaskEntity updateLabel(UpdateLabelVO vo);
     
     public TaskEntity uploadDocuments(Long taskId, MultipartFile[] documents);
     
     public TaskEntity deleteDocuments(Long taskId, String[] docsToDelete) throws IOException,UpdateTaskException;
     
     public List<TaskEntity> getTasksByChannelUID(String channelUId);
     
     public TaskEntity updateTaskStatus(Long taskId, Long oldColumnId, Long newColumnId);
   
         
    
         
   

}
