/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.CommentEntity;
import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.entity.TaskEntity;
import com.is4103.matchub.exception.CreateTaskException;
import com.is4103.matchub.exception.DeleteTaskException;
import com.is4103.matchub.exception.UpdateTaskException;
import com.is4103.matchub.repository.CommentEntityRepository;
import com.is4103.matchub.repository.KanbanBoardEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.TaskColumnEntityRepository;
import com.is4103.matchub.repository.TaskEntityRepository;
import com.is4103.matchub.vo.CommentVO;
import com.is4103.matchub.vo.TaskVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author longluqian
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskEntityRepository taskEntityRepository;

    @Autowired
    private TaskColumnEntityRepository taskColumnEntityRepository;

    @Autowired
    private KanbanBoardEntityRepository kanbanBoardEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private CommentEntityRepository commentEntityRepository;

//Create Channel Tasks
    @Override
    public TaskEntity createTask(TaskVO vo) throws CreateTaskException{

        //Incomplete:  check if creator is channel admin
        ProfileEntity creator = profileEntityRepository.findById(vo.getTaskCreatorId()).get();

        //create new task
        TaskEntity task = new TaskEntity();
        vo.createTask(task);

        // set column to task
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(vo.getTaskColumnId()).get();
        task.setTaskColumn(taskColumn);

        // add taskdoer to task
        for (Long taskDoerId : vo.getTaskdoers()) {
            ProfileEntity taskDoer = profileEntityRepository.findById(taskDoerId).get();
            task.getTaskdoers().add(taskDoer);
        }

        task = taskEntityRepository.saveAndFlush(task);

        // add task to task column
        taskColumn.getListOfTasks().add(task);

        // add task to taskdoer
        for (Long taskDoerId : vo.getTaskdoers()) {
            ProfileEntity taskDoer = profileEntityRepository.findById(taskDoerId).get();
            taskDoer.getTasks().add(task);
        }
        taskColumnEntityRepository.flush();
        return task;
    }

    //View list of Tasks
    @Override
    public List<TaskEntity> getTasksByColumnId(Long columnId) {
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(columnId).get();
        return taskColumn.getListOfTasks();
    }

    //View a Particular Tasks
    @Override
    public TaskEntity getTaskById(Long taskId) {
        return taskEntityRepository.findById(taskId).get();
    }

    //Update Tasks
    @Override
    public TaskEntity updateTask(TaskVO vo) throws  UpdateTaskException{
        //Incomplete:  check if creator is channel admin
        ProfileEntity creator = profileEntityRepository.findById(vo.getTaskCreatorId()).get();

        TaskEntity task = taskEntityRepository.findById(vo.getTaskId()).get();
        vo.updateTask(task);

        task = taskEntityRepository.saveAndFlush(task);
        return task;

    }

    @Override
    public TaskEntity updateTaskDoers(List<Long> newTaskDoerList, Long taskId, Long updatorId) throws UpdateTaskException{

        // Incomplete : only channel admin can update task doers
        TaskEntity task = taskEntityRepository.findById(taskId).get();

        // remove previous taskdoer - task relationship
        for (ProfileEntity taskDoer : task.getTaskdoers()) {
            taskDoer.getTasks().remove(task);
        }
        task.setTaskdoers(new ArrayList<>());

        // add taskdoer to task
        for (Long taskDoerId : newTaskDoerList) {
            ProfileEntity taskDoer = profileEntityRepository.findById(taskDoerId).get();
            task.getTaskdoers().add(taskDoer);
        }

        // add task to taskdoer
        for (Long taskDoerId : newTaskDoerList) {
            ProfileEntity taskDoer = profileEntityRepository.findById(taskDoerId).get();
            taskDoer.getTasks().add(task);
        }
        task = taskEntityRepository.saveAndFlush(task);
        profileEntityRepository.flush();
        return task;
    }

    @Override
    public TaskColumnEntity deleteTask(Long taskId, Long deletorId) throws IOException, DeleteTaskException {

        // Incomplete : only channel admin can update task doers
        TaskEntity task = taskEntityRepository.findById(taskId).get();

        // remove task from column
        TaskColumnEntity column = task.getTaskColumn();
        column.getListOfTasks().remove(task);

        // remove previous taskdoer - task relationship
        for (ProfileEntity taskDoer : task.getTaskdoers()) {
            taskDoer.getTasks().remove(task);
        }
        task.setTaskdoers(new ArrayList<>());

        // delete all documents
        Map<String, String> hashmap = task.getDocuments();

        //loop 1: check if all the documents are present
        for (String key : hashmap.keySet()) {
            //get the path of the document to delete
            String selectedDocumentPath = hashmap.get(key);
            if (selectedDocumentPath == null) {
                System.err.println("Problem deleting documents");
            }
        }

        //loop2: delete the actual file when all files are present
        for (String key : hashmap.keySet()) {
            //get the path of the document to delete
            String selectedDocumentPath = hashmap.get(key);
            //if file is present, call attachmentService to delete the actual file from /build folder
            attachmentService.deleteFile(selectedDocumentPath);

            //successfully removed the actual file from /build folder, update organisation hashmap
            hashmap.remove(key);
        }
        //save once all documents are removed successfully
        task.setDocuments(hashmap);

        //delete all comments
        List<CommentEntity> comments = task.getComments();
        task.setComments(new ArrayList<>());

        commentEntityRepository.deleteAll(comments);
        taskEntityRepository.delete(task);
        return taskColumnEntityRepository.saveAndFlush(column);

    }

    @Override //Move task around
    public KanbanBoardEntity rearrangeTasks(Map<Long, List<Long>> columnIdAndTaskIdSequence, Long kanbanboardId, Long arrangerId)throws UpdateTaskException{
        // incomplete check : only channel admins and task leader can move the task around

        for (Map.Entry<Long, List<Long>> entry : columnIdAndTaskIdSequence.entrySet()) {
            TaskColumnEntity column = taskColumnEntityRepository.findById(entry.getKey()).get();
            column.setListOfTasks(new ArrayList<>());
            for (Long taskId : entry.getValue()) {
                TaskEntity task = taskEntityRepository.findById(taskId).get();
                // remove task from old column
                TaskColumnEntity oldColumn = task.getTaskColumn();
                if (oldColumn.getListOfTasks().contains(task)) {
                    oldColumn.getListOfTasks().remove(task);
                }
                //new column add task
                column.getListOfTasks().add(task);
                //task set new column
                task.setTaskColumn(column);
            }
        }
        taskColumnEntityRepository.flush();
        taskEntityRepository.flush();

        return kanbanBoardEntityRepository.findById(kanbanboardId).get();
    }

    //Add comments to task
    @Override
    public TaskEntity addCommentToTask(Long taskId, CommentVO vo)throws UpdateTaskException{
        CommentEntity newComment = new CommentEntity();
        vo.createTaskComment(newComment);
        newComment = commentEntityRepository.saveAndFlush(newComment);
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        task.getComments().add(newComment);
        return taskEntityRepository.saveAndFlush(task);
    }
    //Delete task comments

    @Override
    public TaskEntity deleteTaskComment(Long taskId, Long commentId) throws UpdateTaskException{
        CommentEntity comment = commentEntityRepository.findById(commentId).get();
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        task.getComments().remove(comment);
        commentEntityRepository.delete(comment);
        return taskEntityRepository.saveAndFlush(task);
    }

    // get list of comments by taskId  
    
    @Override 
    public List<CommentEntity> getListOfCommentsByTaskId(Long taskId){
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        return task.getComments();
        
    }
    
    @Override
    public TaskEntity updateLabel(Map<String, String> labelAndColour, Long taskId){
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        task.setLabelAndColour(labelAndColour);
        return taskEntityRepository.saveAndFlush(task);
    }
    
    
    
    @Override
    public TaskEntity uploadDocuments(Long taskId, MultipartFile[] documents)  {
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        for (MultipartFile photo : documents) {
            String path = attachmentService.upload(photo);
            String name = photo.getOriginalFilename();
            System.err.println("name: " + name);
            task.getDocuments().put(name, path);

        }
        task = taskEntityRepository.saveAndFlush(task);
        return task;
    }

    @Override
    public TaskEntity deleteDocuments(Long taskId, String[] docsToDelete) throws IOException,UpdateTaskException{
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        Map<String, String> hashmap = task.getDocuments();

        //loop 1: check if all the documents are present
        for (String key : docsToDelete) {
            //get the path of the document to delete
            String selectedDocumentPath = hashmap.get(key);
            if (selectedDocumentPath == null) {
                throw new UpdateTaskException("Unable to delete project document (Document not found): " + key);
            }
        }

        //loop2: delete the actual file when all files are present
        for (String key : docsToDelete) {
            //get the path of the document to delete
            String selectedDocumentPath = hashmap.get(key);
            //if file is present, call attachmentService to delete the actual file from /build folder
            attachmentService.deleteFile(selectedDocumentPath);

            //successfully removed the actual file from /build folder, update organisation hashmap
            hashmap.remove(key);
        }

        //save once all documents are removed successfully
        task.setDocuments(hashmap);
        return taskEntityRepository.saveAndFlush(task);

    }
    //Filter Tasks by Assignee  
}
