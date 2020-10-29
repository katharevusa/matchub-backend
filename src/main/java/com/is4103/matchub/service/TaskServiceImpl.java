/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.CommentEntity;
import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.TaskColumnEntity;
import com.is4103.matchub.entity.TaskEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import com.is4103.matchub.exception.CreateTaskException;
import com.is4103.matchub.exception.DeleteTaskException;
import com.is4103.matchub.exception.RearrangeTaskException;
import com.is4103.matchub.exception.UpdateTaskException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.CommentEntityRepository;
import com.is4103.matchub.repository.KanbanBoardEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.TaskColumnEntityRepository;
import com.is4103.matchub.repository.TaskEntityRepository;
import com.is4103.matchub.vo.ChannelDetailsVO;
import com.is4103.matchub.vo.CommentVO;
import com.is4103.matchub.vo.CreateTaskVO;
import com.is4103.matchub.vo.RearrangeTaskVO;
import com.is4103.matchub.vo.UpdateLabelVO;
import com.is4103.matchub.vo.UpdateTaskVO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementEntityRepository announcementEntityRepository;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;
//Create Channel Tasks

    @Override
    @Transactional
    public TaskEntity createTask(CreateTaskVO vo) throws CreateTaskException {

        //check if creator is channel admin
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(vo.getKanbanboardId()).get();
        ProjectEntity project = projectEntityRepository.findById(kanbanBoardEntity.getProjectId()).get();
        ChannelDetailsVO channelDetails = firebaseService.getChannelDetails(kanbanBoardEntity.getChannelUid());
        if (!channelDetails.getAdminIds().contains(vo.getTaskCreatorOrEditorId())) {
            throw new CreateTaskException("Only channel admin can create task");
        }
        //create new task
        TaskEntity task = new TaskEntity();
        vo.createTask(task);

        // set column to task
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(vo.getTaskColumnId()).get();
        task.setTaskColumn(taskColumn);

        // add task to task column
        taskColumn.getListOfTasks().add(task);

        task = taskEntityRepository.saveAndFlush(task);
        taskColumnEntityRepository.flush();

        //notify task leader
        if (task.getTaskLeaderId() != null) {
            ProfileEntity taskLeader = profileEntityRepository.findById(task.getTaskLeaderId()).get();
            AnnouncementEntity announcementEntity = new AnnouncementEntity();
            announcementEntity.setTitle("You have been assigned to be a leader of one task :'" + task.getTaskTitle() + "'.");
            announcementEntity.setContent("From project: '" + project.getProjectTitle() + "'.");
            announcementEntity.setTimestamp(LocalDateTime.now());
            announcementEntity.setType(AnnouncementTypeEnum.TASK_LEADER_APPOINTMENT);
            announcementEntity.setTaskId(task.getTaskId());
            announcementEntity.setProjectId(kanbanBoardEntity.getProjectId());
            // association
            announcementEntity.getNotifiedUsers().add(taskLeader);
            taskLeader.getAnnouncements().add(announcementEntity);
            announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);
            // create notification
            announcementService.createNormalNotification(announcementEntity);
        }

        return task;
    }

    //View list of Tasks
    @Override
    public List<TaskEntity> getTasksByColumnId(Long columnId) {
        TaskColumnEntity taskColumn = taskColumnEntityRepository.findById(columnId).get();
        return taskColumn.getListOfTasks();
    }

    @Override
    public List<TaskEntity> getTasksByChannelUID(String channelUId) {
        List<TaskEntity> tasks = new ArrayList<>();
        KanbanBoardEntity kanbanboard = kanbanBoardEntityRepository.findByChannelUId(channelUId).get();
        for (TaskColumnEntity tc : kanbanboard.getTaskColumns()) {
            tasks.addAll(tc.getListOfTasks());
        }

        return tasks;

    }

    //View a Particular Tasks
    @Override
    public TaskEntity getTaskById(Long taskId) {
        return taskEntityRepository.findById(taskId).get();
    }

    //Update Tasks
    @Override
    public TaskEntity updateTask(UpdateTaskVO vo) throws UpdateTaskException {
        //Check: channel admins can update task
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(vo.getKanbanboardId()).get();
        ChannelDetailsVO channelDetails = firebaseService.getChannelDetails(kanbanBoardEntity.getChannelUid());
        if (!channelDetails.getAdminIds().contains(vo.getTaskCreatorOrEditorId())) {
            throw new UpdateTaskException("Only channel admin can update task");
        }

        TaskEntity task = taskEntityRepository.findById(vo.getTaskId()).get();
        vo.updateTask(task);

        task = taskEntityRepository.saveAndFlush(task);
        return task;

    }

    @Override
    public TaskEntity updateTaskDoers(List<Long> newTaskDoerList, Long taskId, Long updatorId, Long kanbanBoardId) throws UpdateTaskException {

//     //    only channel admin can update task doers
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(kanbanBoardId).get();
        ProjectEntity project = projectEntityRepository.findById(kanbanBoardEntity.getProjectId()).get();
        ChannelDetailsVO channelDetails = firebaseService.getChannelDetails(kanbanBoardEntity.getChannelUid());
        if (!channelDetails.getAdminIds().contains(updatorId)) {
            throw new UpdateTaskException("Only channel admins can update task");
        }
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
            taskDoer.getTasks().add(task);
        }

        profileEntityRepository.flush();

        task = taskEntityRepository.saveAndFlush(task);

        // check if there exists task doers
        if (!task.getTaskdoers().isEmpty()) {
            // notify task doers  
            AnnouncementEntity announcementEntity = new AnnouncementEntity();
            announcementEntity.setTitle("A new task has been assigned to you: '" + task.getTaskTitle() + "'. ");
            announcementEntity.setContent("From project: '" + project.getProjectTitle() + "'.");
            announcementEntity.setTimestamp(LocalDateTime.now());
            announcementEntity.setType(AnnouncementTypeEnum.TASK_ASSIGNED);
            announcementEntity.setTaskId(task.getTaskId());
            announcementEntity.setProjectId(kanbanBoardEntity.getProjectId());
            // association
            announcementEntity.getNotifiedUsers().addAll(task.getTaskdoers());
            for (ProfileEntity p : task.getTaskdoers()) {
                p.getAnnouncements().add(announcementEntity);
            }
            announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);
            // create notification
            announcementService.createNormalNotification(announcementEntity);
        }

        return task;
    }

    @Override
    public TaskColumnEntity deleteTask(Long taskId, Long deletorId, Long kanbanBoardId) throws IOException, DeleteTaskException {

        // Incomplete : only channel admin can update task doers
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(kanbanBoardId).get();
        ChannelDetailsVO channelDetails = firebaseService.getChannelDetails(kanbanBoardEntity.getChannelUid());
        if (!channelDetails.getAdminIds().contains(deletorId)) {
            throw new DeleteTaskException("Only channel admin can delete task");
        }
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
    public KanbanBoardEntity rearrangeTasks(RearrangeTaskVO vo) throws RearrangeTaskException {
        // incomplete check : only channel admins and task leader can move the task around
//        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(vo.getKanbanBoardId()).get();
//        ChannelDetailsVO channelDetails = firebaseService.getChannelDetails(kanbanBoardEntity.getChannelUid());
//        if (!channelDetails.getAdminIds().contains(vo.getArrangerId()) ) {
//            throw new RearrangeTaskException("Only channel admin can delete task");
//        }
        Map<Long, List<Long>> columnIdAndTaskIdSequence = vo.getColumnIdAndTaskIdSequence();
        Long kanbanboardId = vo.getKanbanBoardId();
        Long arrangerId = vo.getArrangerId();

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
    public TaskEntity addCommentToTask(Long taskId, CommentVO vo) throws UpdateTaskException {
        CommentEntity newComment = new CommentEntity();
        vo.createTaskComment(newComment);
        newComment = commentEntityRepository.saveAndFlush(newComment);
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        task.getComments().add(newComment);
        return taskEntityRepository.saveAndFlush(task);
    }
    //Delete task comments

    @Override
    public TaskEntity deleteTaskComment(Long taskId, Long commentId) throws UpdateTaskException {
        CommentEntity comment = commentEntityRepository.findById(commentId).get();
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        task.getComments().remove(comment);
        commentEntityRepository.delete(comment);
        return taskEntityRepository.saveAndFlush(task);
    }

    // get list of comments by taskId  
    @Override
    public List<CommentEntity> getListOfCommentsByTaskId(Long taskId) {
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        return task.getComments();

    }

    @Override
    public TaskEntity updateLabel(UpdateLabelVO vo) {
        Map<String, String> labelAndColour = vo.getLabelAndColour();
        Long taskId = vo.getTaskId();
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        task.setLabelAndColour(labelAndColour);
        return taskEntityRepository.saveAndFlush(task);
    }

    @Override
    public TaskEntity uploadDocuments(Long taskId, MultipartFile[] documents) {
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
    public TaskEntity deleteDocuments(Long taskId, String[] docsToDelete) throws IOException, UpdateTaskException {
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

    @Override
    public TaskEntity updateTaskStatus(Long taskId, Long oldColumnId, Long newColumnId) {
        TaskEntity task = taskEntityRepository.findById(taskId).get();
        TaskColumnEntity oldColumn = taskColumnEntityRepository.findById(oldColumnId).get();
        TaskColumnEntity newColumn = taskColumnEntityRepository.findById(newColumnId).get();

        oldColumn.getListOfTasks().remove(task);
        newColumn.getListOfTasks().add(task);
        task.setTaskColumn(newColumn);

        taskColumnEntityRepository.flush();
        return taskEntityRepository.saveAndFlush(task);

    }


    @Override
    public List<TaskEntity> getUnfinishedTasksByKanbanBoardId(Long kanbanboardId) {
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(kanbanboardId).get();
        List<TaskEntity> unFinishedTasks = new ArrayList<>();
        for (TaskColumnEntity tc : kanbanBoardEntity.getTaskColumns()) {
            if (!tc.isDone()) {
                unFinishedTasks.addAll(tc.getListOfTasks());

            }
        }
        return unFinishedTasks;

    }

    @Override
    public List<TaskEntity> getUnfinishedTasksByChannelUId(String channelUId) {
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findByChannelUId(channelUId).get();
        List<TaskEntity> unFinishedTasks = new ArrayList<>();
        for (TaskColumnEntity tc : kanbanBoardEntity.getTaskColumns()) {
            if (!tc.isDone()) {
                unFinishedTasks.addAll(tc.getListOfTasks());

            }
        }
        return unFinishedTasks;

    }

    @Override
    public List<TaskEntity> getUnfinishedTasksByUserId(Long kanbanboardId, Long userId) {
        ProfileEntity user = profileEntityRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        KanbanBoardEntity kanbanBoardEntity = kanbanBoardEntityRepository.findById(kanbanboardId).get();
        List<TaskEntity> unFinishedTasks = new ArrayList<>();
        for (TaskColumnEntity tc : kanbanBoardEntity.getTaskColumns()) {
            if (!tc.isDone()) {
                for (TaskEntity t : tc.getListOfTasks()) {
                    if (t.getTaskdoers().contains(user)) {
                        unFinishedTasks.add(t);
                    }
                }
            }
        }
        return unFinishedTasks;

    }

}
