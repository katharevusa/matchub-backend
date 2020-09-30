/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.JoinRequestEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.enumeration.JoinRequestStatusEnum;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.exception.DeleteProjectException;
import com.is4103.matchub.exception.DownvoteProjectException;
import com.is4103.matchub.exception.JoinProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.RevokeDownvoteException;
import com.is4103.matchub.exception.RevokeUpvoteException;
import com.is4103.matchub.exception.TerminateProjectException;
import com.is4103.matchub.exception.UpdateProjectException;
import com.is4103.matchub.exception.UpvoteProjectException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.JoinRequestEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import com.is4103.matchub.vo.ProjectCreateVO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author longluqian
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private SDGEntityRepository sDGEntityRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private JoinRequestEntityRepository joinRequestEntityRepository;

    @Override
    public ProjectEntity createProject(ProjectCreateVO vo) {
        ProjectEntity newProject = new ProjectEntity();
        vo.updateProject(newProject);
        // associate with profile
        Optional<ProfileEntity> profile = profileEntityRepository.findById(newProject.getProjCreatorId());
        profile.get().getProjectsOwned().add(newProject);
        newProject.getProjectOwners().add(profile.get());

        // associate with SDGs
        // need not do newProject.clear() as it is empty since newly instantiated
        // passing in array of SDG ids
        for (Long sdgId : vo.getSdgs()) {
            SDGEntity sdgToAssociateWith = sDGEntityRepository.findBySdgId(sdgId);
            sdgToAssociateWith.getProjects().add(newProject);
            newProject.getSdgs().add(sdgToAssociateWith);
        }

        newProject = projectEntityRepository.saveAndFlush(newProject);

        return newProject;

    }

    @Override
    public ProjectEntity createProject(ProjectEntity newProject, Long creatorId) {
        newProject.setProjCreatorId(creatorId);
        Optional<ProfileEntity> profile = profileEntityRepository.findById(creatorId);
        profile.get().getProjectsOwned().add(newProject);
        newProject.getProjectOwners().add(profile.get());

        for (SDGEntity s : newProject.getSdgs()) {
            SDGEntity sDGEntity = sDGEntityRepository.findBySdgId(s.getSdgId());
            sDGEntity.getProjects().add(newProject);
        }

        newProject = projectEntityRepository.saveAndFlush(newProject);
        return newProject;

    }

    @Override
    public ProjectEntity retrieveProjectById(Long id) throws ProjectNotFoundException {
        Optional<ProjectEntity> optionalProjectEntity = projectEntityRepository.findById(id);
        if (optionalProjectEntity.isPresent()) {
            return optionalProjectEntity.get();
        } else {
            throw new ProjectNotFoundException("Project with id " + id + " does not exist");
        }
    }

    @Override
    public ProjectEntity updateProject(ProjectCreateVO vo, Long accountId, Long projectId) throws ProjectNotFoundException, UpdateProjectException {
        Optional<ProjectEntity> optionalProjectEntity = projectEntityRepository.findById(projectId);
        if (optionalProjectEntity.isPresent()) {
            ProjectEntity oldProject = optionalProjectEntity.get();
            Optional<ProfileEntity> updater = profileEntityRepository.findById(accountId);

            if (updater.isPresent()) {
                for (ProfileEntity p : oldProject.getProjectOwners()) {
                    if (p.getAccountId().equals(updater.get().getAccountId())) {
                        oldProject.setProjectTitle(vo.getProjectTitle());
                        oldProject.setProjectDescription(vo.getProjectDescription());
                        oldProject.setCountry(vo.getCountry());
                        oldProject.setStartDate(vo.getStartDate());
                        oldProject.setEndDate(vo.getEndDate());

                        //remove the old association： remove project from sdgs
                        for (SDGEntity sdg : oldProject.getSdgs()) {
                            SDGEntity sdgToAssociateWith = sDGEntityRepository.findBySdgId(sdg.getSdgId());
                            sdgToAssociateWith.getProjects().remove(oldProject);
                        }

                        oldProject.setSdgs(new ArrayList<>());
                        //new association
                        for (Long sdgId : vo.getSdgs()) {
                            SDGEntity sdgToAssociateWith = sDGEntityRepository.findBySdgId(sdgId);
                            sdgToAssociateWith.getProjects().add(oldProject);
                            oldProject.getSdgs().add(sdgToAssociateWith);
                        }

                        oldProject = projectEntityRepository.saveAndFlush(oldProject);
                        return oldProject;
                    }
                }
                throw new UpdateProjectException("You do not have the right to edit this project");
            } else {
                throw new UpdateProjectException("Updater does not exist");
            }

        } else {
            throw new ProjectNotFoundException("Project with id " + projectId + " does not exist");
        }
    }

    @Override
    public void deleteProject(Long projectId, Long accountId) throws DeleteProjectException {
        Optional<ProjectEntity> optionalProjectEntity = projectEntityRepository.findById(projectId);
        if (optionalProjectEntity.isPresent()) {
            ProjectEntity oldProject = optionalProjectEntity.get();
            if (Objects.equals(oldProject.getProjCreatorId(), accountId)) {
                if (oldProject.getProjStatus().equals(ProjectStatusEnum.ON_HOLD)) {
                    for (ProfileEntity p : oldProject.getProjectOwners()) {
                        p.getProjectsOwned().remove(oldProject);
                    }
                    oldProject.setProjectOwners(new ArrayList<>());
                    projectEntityRepository.delete(oldProject);

                } else {
                    throw new DeleteProjectException("Delete project exception: The project has either in progress or completed, can not be deleted");
                }

            } else {
                throw new DeleteProjectException("Delete project exception: Only project creator can delete project");
            }
        }
    }

    @Override
    public List<ProjectEntity> getJoinedProjects(Long profileId) throws UserNotFoundException {
        Optional<ProfileEntity> profile = profileEntityRepository.findById(profileId);
        if (profile.isPresent()) {
            return profile.get().getProjectsJoined();

        } else {
            throw new UserNotFoundException(profileId);
        }
    }

    @Override
    public List<ProjectEntity> getCreatedProjects(Long profileId) throws UserNotFoundException {
        Optional<ProfileEntity> profile = profileEntityRepository.findById(profileId);
        if (profile.isPresent()) {
            return projectEntityRepository.getCreatedProjectByProfileId(profileId);
        } else {
            throw new UserNotFoundException(profileId);
        }
    }

    //only project creator can terminate project
    @Override
    public void terminateProject(Long projectId, Long profileId) throws TerminateProjectException {
        Optional<ProfileEntity> profileOptional = profileEntityRepository.findById(profileId);
        if (!profileOptional.isPresent()) {
            throw new TerminateProjectException("Fail to terminate project: User is not found");
        }
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new TerminateProjectException("Fail to terminate project: Project is not found");
        }
        ProjectEntity project = projectOptional.get();
        if (project.getProjCreatorId() != profileId) {
            throw new TerminateProjectException("Only project creator can terminate project");
        }

        project.setEndDate(LocalDateTime.now());
        project.setProjStatus(ProjectStatusEnum.COMPLETED);
        projectEntityRepository.saveAndFlush(project);
        // Incomplete: timer to start the point allocation and reviewa

    }

    @Override
    public Page<ProjectEntity> searchProjectByKeywords(String keyword, Pageable pageable) {
        return projectEntityRepository.searchByKeywords(keyword, pageable);
    }

    @Override
    public Page<ProjectEntity> getLaunchedProjects(Pageable pageble) {
        return projectEntityRepository.getLaunchedProjects(pageble);
    }

    @Override
    public Page<ProjectEntity> getAllProjects(Pageable pageble) {
        return projectEntityRepository.findAll(pageble);
    }
    
    @Override
    public List<ProjectEntity> getOwnedProjects(Long userId) {
        ProfileEntity user = profileEntityRepository.findById(userId).get();
        return user.getProjectsOwned();
    }

    @Override
    public ProjectEntity setProjectProfilePic(Long projectId, String path) throws ProjectNotFoundException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Project not exist");
        }
        ProjectEntity project = projectOptional.get();
        project.setProjectProfilePic(path);

        project = projectEntityRepository.saveAndFlush(project);

        return project;
    }

    @Override
    public ProjectEntity deleteProjectProfilePic(Long projectId) throws ProjectNotFoundException, UpdateProjectException, IOException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Project not exist");
        }
        ProjectEntity project = projectOptional.get();
        if (project.getProjectProfilePic() == null) {
            throw new UpdateProjectException("Unable to delete profile picture: You currently do not have a profile picture  ");
        }
        attachmentService.deleteFile(project.getProjectProfilePic());
        project.setProjectProfilePic(null);

        return projectEntityRepository.saveAndFlush(project);

    }

    @Override
    public ProjectEntity uploadPhotos(Long projectId, MultipartFile[] photos) throws ProjectNotFoundException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Project not exist");
        }
        ProjectEntity project = projectOptional.get();

        for (MultipartFile photo : photos) {
            String path = attachmentService.upload(photo);
            project.getPhotos().add(path);

        }
        project = projectEntityRepository.saveAndFlush(project);
        return project;
    }

    @Override
    public ProjectEntity deletePhotos(Long projectId, String[] photoToDelete) throws ProjectNotFoundException, IOException, UpdateProjectException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Project not exist");
        }
        ProjectEntity project = projectOptional.get();

        for (String s : photoToDelete) {
            if (!project.getPhotos().contains(s)) {
                throw new UpdateProjectException("Unable to delete photos: photos not found");
            }
        }

        for (String s : photoToDelete) {
            project.getPhotos().remove(s);
            attachmentService.deleteFile(s);
        }

        return projectEntityRepository.saveAndFlush(project);
    }

    @Override
    public ProjectEntity uploadDocuments(Long projectId, MultipartFile[] documents) throws ProjectNotFoundException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Project not exist");
        }
        ProjectEntity project = projectOptional.get();

        for (MultipartFile photo : documents) {
            String path = attachmentService.upload(photo);
            String name = photo.getOriginalFilename();
            System.err.println("name: " + name);
            project.getDocuments().put(name, path);

        }
        project = projectEntityRepository.saveAndFlush(project);
        return project;
    }

    @Override
    public ProjectEntity deleteDocuments(Long projectId, String[] docsToDelete) throws IOException, ProjectNotFoundException, UpdateProjectException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Project not exist");
        }
        ProjectEntity project = projectOptional.get();
        Map<String, String> hashmap = project.getDocuments();

        //loop 1: check if all the documents are present
        for (String key : docsToDelete) {
            //get the path of the document to delete
            String selectedDocumentPath = hashmap.get(key);
            if (selectedDocumentPath == null) {
                throw new UpdateProjectException("Unable to delete project document (Document not found): " + key);
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
        project.setDocuments(hashmap);
        return projectEntityRepository.saveAndFlush(project);

    }

    @Override
    public ProjectEntity upvoteProject(Long projectId, Long profileId) throws ProjectNotFoundException, UpvoteProjectException, UserNotFoundException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        Optional<ProfileEntity> profOptional = profileEntityRepository.findById(profileId);
        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Upable to upvote project: Project not exist");
        }
        if (!profOptional.isPresent()) {
            throw new UserNotFoundException("Upable to upvote project: User not found");
        }
        //one user can only upvote one project
        ProfileEntity profile = profOptional.get();
        if (profile.getUpvotedProjectIds().contains(projectId)) {
            throw new UpvoteProjectException("Upable to upvote project: You have already upvoted this project");
        }

        if (profile.getDownvotedProjectIds().contains(projectId)) {
            throw new UpvoteProjectException("Upable to upvote project: Please revoke downvote before upvote the project");
        }

        //associate profile and project
        profile.getUpvotedProjectIds().add(projectId);
        ProjectEntity project = projectOptional.get();
        Integer upvote = project.getUpvotes() + 1;
        project.setUpvotes(upvote);

        //activate project once reaches 20
        if (project.getUpvotes() >= 20) {
            project.setProjStatus(ProjectStatusEnum.ACTIVE);
        }

        project = projectEntityRepository.saveAndFlush(project);
        System.err.println("upvote: " + project.getUpvotes());
        return project;

    }

    @Override
    public ProjectEntity downvoteProject(Long projectId, Long profileId) throws ProjectNotFoundException, DownvoteProjectException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        Optional<ProfileEntity> profOptional = profileEntityRepository.findById(profileId);

        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Upable to downvote project: Project not exist");
        }

        if (!profOptional.isPresent()) {
            throw new UserNotFoundException("Upable to downvote project: User not found");
        }

        ProjectEntity project = projectOptional.get();
        ProfileEntity profile = profOptional.get();
        if (profile.getDownvotedProjectIds().contains(projectId)) {
            throw new DownvoteProjectException("Upable to downvote project: You have already downvoted this project");
        }

        if (profile.getUpvotedProjectIds().contains(projectId)) {
            throw new DownvoteProjectException("Upable to downvote project: Please revoke upvote before downvote the project");
        }

        if (project.getUpvotes() == 0) {
            throw new DownvoteProjectException("Unable to downvote project: Minimum 0 upvotes");
        }

        if (project.getUpvotes() < 20) {
            project.setProjStatus(ProjectStatusEnum.ON_HOLD);
        }

        project.setUpvotes(project.getUpvotes() - 1);
        profile.getDownvotedProjectIds().add(projectId);
        project = projectEntityRepository.saveAndFlush(project);

        return project;

    }

    @Override
    public ProjectEntity revokeUpvote(Long projectId, Long profileId) throws ProjectNotFoundException, UserNotFoundException, RevokeUpvoteException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        Optional<ProfileEntity> profOptional = profileEntityRepository.findById(profileId);

        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Unable to revoke upvote: Project not exist");
        }

        if (!profOptional.isPresent()) {
            throw new UserNotFoundException("Unable to revoke upvote: User not found");
        }

        ProjectEntity project = projectOptional.get();
        ProfileEntity profile = profOptional.get();

        if (!profile.getUpvotedProjectIds().contains(projectId)) {
            throw new RevokeUpvoteException("Unable to revoke upvote: You have never upvoted this project");
        }

        if (profile.getDownvotedProjectIds().contains(projectId)) {
            throw new RevokeUpvoteException("Unable to revoke upvote: Please revoke downvote before you upvote the project ");
        }
        project.setUpvotes(project.getUpvotes() - 1);

        if (project.getUpvotes() < 20) {
            project.setProjStatus(ProjectStatusEnum.ON_HOLD);
        }
        profile.getUpvotedProjectIds().remove(projectId);
        project = projectEntityRepository.saveAndFlush(project);

        return project;

    }

    @Override
    public ProjectEntity revokeDownvote(Long projectId, Long profileId) throws ProjectNotFoundException, UserNotFoundException, RevokeDownvoteException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        Optional<ProfileEntity> profOptional = profileEntityRepository.findById(profileId);

        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Unable to revoke downvote: Project not exist");
        }

        if (!profOptional.isPresent()) {
            throw new UserNotFoundException("Unable to revoke downvote: User not found");
        }

        ProjectEntity project = projectOptional.get();
        ProfileEntity profile = profOptional.get();

        if (!profile.getDownvotedProjectIds().contains(projectId)) {
            throw new RevokeDownvoteException("Unable to revoke downvote: You have never downvoted this project");
        }

        if (profile.getUpvotedProjectIds().contains(projectId)) {
            throw new RevokeDownvoteException("Unable to revoke downvote: Please revoke upvote before you downvote the project ");
        }

        if (project.getUpvotes() >= 20) {
            project.setProjStatus(ProjectStatusEnum.ACTIVE);
        }

        project.setUpvotes(project.getUpvotes() + 1);
        profile.getDownvotedProjectIds().remove(projectId);
        project = projectEntityRepository.saveAndFlush(project);

        return project;

    }

    @Override
    public Page<ProjectEntity> retrieveProjectBySDGId(Long sdgId, Pageable pageable) throws ProjectNotFoundException {
        Optional<SDGEntity> optionalSDG = sDGEntityRepository.findById(sdgId);
        if (optionalSDG.isPresent()) {
            SDGEntity sdg = optionalSDG.get();
            List<ProjectEntity> projects = sdg.getProjects();
            Long start = pageable.getOffset();
            Long end = (start + pageable.getPageSize()) > projects.size() ? projects.size() : (start + pageable.getPageSize());
            Page<ProjectEntity> pages = new PageImpl<ProjectEntity>(projects.subList(start.intValue(), end.intValue()), pageable, projects.size());
            return pages;
        } else {
            throw new ProjectNotFoundException("SDG with id does not exist");
        }
    }

//    make a request to join project (project id, profile id)
    @Override
    public JoinRequestEntity createJoinRequest(Long projectId, Long profileId) throws ProjectNotFoundException, JoinProjectException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        Optional<ProfileEntity> profOptional = profileEntityRepository.findById(profileId);

        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Unable to revoke downvote: Project not exist");
        }

        if (!profOptional.isPresent()) {
            throw new UserNotFoundException("Unable to revoke downvote: User not found");
        }

        ProjectEntity project = projectOptional.get();
        ProfileEntity requestor = profOptional.get();

        // One user can only make one join request to one project 
        Optional<JoinRequestEntity> requestOptional = joinRequestEntityRepository.searchJoinRequestProjectByProjectAndRequestorAndStatus(projectId, profileId,JoinRequestStatusEnum.ON_HOLD);
        if (requestOptional.isPresent()) {
            throw new JoinProjectException("There is already one on-hold join request to this project created.");
        }
        // If user is already a teammember or project creators,  can not make join request
        if (project.getTeamMembers().contains(requestor) || project.getProjectOwners().contains(requestor)) {
            throw new JoinProjectException("You are already participating this project");
        }

        JoinRequestEntity joinRequest = new JoinRequestEntity();
        joinRequest.setProject(project);
        project.getJoinRequests().add(joinRequest);
        joinRequest.setRequestor(requestor);
        requestor.getJoinRequests().add(joinRequest);
        
        //make anouncement to project owners 
        
        return joinRequestEntityRepository.saveAndFlush(joinRequest);
    }

}
