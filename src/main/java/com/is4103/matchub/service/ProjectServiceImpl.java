/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.JoinRequestEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceRequestEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import com.is4103.matchub.enumeration.JoinRequestStatusEnum;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.enumeration.RequestStatusEnum;
import com.is4103.matchub.exception.CompleteProjectException;
import com.is4103.matchub.exception.DeleteProjectException;
import com.is4103.matchub.exception.DownvoteProjectException;
import com.is4103.matchub.exception.FollowProjectException;
import com.is4103.matchub.exception.JoinProjectException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.RevokeDownvoteException;
import com.is4103.matchub.exception.RevokeUpvoteException;
import com.is4103.matchub.exception.TerminateProjectException;
import com.is4103.matchub.exception.UnableToAddProjectOwnerException;
import com.is4103.matchub.exception.UnableToRemoveProjectOwnerException;
import com.is4103.matchub.exception.UpdateProjectException;
import com.is4103.matchub.exception.UpvoteProjectException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.AnnouncementEntityRepository;
import com.is4103.matchub.repository.JoinRequestEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import com.is4103.matchub.vo.ProjectCreateVO;
import com.is4103.matchub.vo.SendNotificationsToUsersVO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private AnnouncementEntityRepository announcementEntityRepository;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private ReputationPointsService reputationPointsService;

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
                        oldProject.setRelatedResources(vo.getRelatedResources());

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

    //only project creator can terminate project, no reputation points will be given
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
        if (!project.getProjCreatorId().equals(profileId)) {
            throw new TerminateProjectException("Only project creator can terminate project");
        }

        //only allow termination of active projects
//        if(project.getProjStatus()!=ProjectStatusEnum.ACTIVE){
//            throw new TerminateProjectException("Failed to terminate project: can only terminate active projects");
//        }
        project.setEndDate(LocalDateTime.now());
        project.setProjStatus(ProjectStatusEnum.TERMINATED);
        // all project's onhold resource request becomes expired 
        for (ResourceRequestEntity rr : project.getListOfRequests()) {
            if (rr.getStatus() == RequestStatusEnum.ON_HOLD) {
                rr.setStatus(RequestStatusEnum.EXPIRED);
            }
        }

        // all project's onhold join request becomes rejected
        for (JoinRequestEntity jr : project.getJoinRequests()) {
            if (jr.getStatus() == JoinRequestStatusEnum.ON_HOLD) {
                jr.setStatus(JoinRequestStatusEnum.REJECTED);
            }
        }

        projectEntityRepository.saveAndFlush(project);

    }

    // manually complete project for early completion of project, reputation point and review should be given
    @Override
    public void completeProject(Long projectId, Long profileId) throws CompleteProjectException, ProjectNotFoundException {
        Optional<ProfileEntity> profileOptional = profileEntityRepository.findById(profileId);
        if (!profileOptional.isPresent()) {
            throw new CompleteProjectException("Failed to complete project: User is not found");
        }
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new CompleteProjectException("Failed to complete project: Project is not found");
        }
        ProjectEntity project = projectOptional.get();
        if (!project.getProjCreatorId().equals(profileId)) {
            throw new CompleteProjectException("Only project creator can change the status of a project");
        }

        if (project.getProjStatus() != ProjectStatusEnum.ACTIVE) {
            throw new CompleteProjectException("You can only complete active projects");
        }

        project.setEndDate(LocalDateTime.now());
        project.setProjStatus(ProjectStatusEnum.COMPLETED);

        // all project's onhold resource request becomes expired 
        for (ResourceRequestEntity rr : project.getListOfRequests()) {
            if (rr.getStatus() == RequestStatusEnum.ON_HOLD) {
                rr.setStatus(RequestStatusEnum.EXPIRED);
            }
        }

        // all project's onhold join request becomes rejected
        for (JoinRequestEntity jr : project.getJoinRequests()) {
            if (jr.getStatus() == JoinRequestStatusEnum.ON_HOLD) {
                jr.setStatus(JoinRequestStatusEnum.REJECTED);
            }
        }
        projectEntityRepository.saveAndFlush(project);

        // Incomplete: reputation points, reviews, badge should be started
        /* trigger the issueProjectBadge method */
        badgeService.issueProjectBadge(project);
        reputationPointsService.issuePointsToFundDonors(project);
        reputationPointsService.issuePointsForCompletedTasks(project);

        //*************include notification to send to project owners & teamMembers to leave reviews
        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTitle("congratulation, the project " + project.getProjectTitle() + " is officially completed!");
        announcementEntity.setContent("Don't forget to give reviews and appreciation notes for your lovely teammates, reputations points allocation will also take consideration of those reviews.");
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setType(AnnouncementTypeEnum.PROJECT_COMPLETED);
        announcementEntity.setProjectId(projectId);
        // association
        announcementEntity.getNotifiedUsers().addAll(project.getProjectOwners());
        announcementEntity.getNotifiedUsers().addAll(project.getTeamMembers());
        for (ProfileEntity p : project.getProjectOwners()) {
            p.getAnnouncements().add(announcementEntity);
        }
        for (ProfileEntity p : project.getTeamMembers()) {
            p.getAnnouncements().add(announcementEntity);
        }

        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);
        // create notification
        announcementService.createNormalNotification(announcementEntity);

    }

    @Override
    public Page<ProjectEntity> searchProjectByKeywords(String keyword, Pageable pageable) {
        String[] keywords = keyword.split(" ");

        Set<ProjectEntity> temp = new HashSet<>();

        for (String s : keywords) {
            temp.addAll(projectEntityRepository.searchByKeywords(s));
        }

        List<ProjectEntity> projects = new ArrayList();
        for (ProjectEntity p : temp) {
            projects.add(p);
        }

        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > projects.size() ? projects.size() : (start + pageable.getPageSize());
        Page<ProjectEntity> pages = new PageImpl<ProjectEntity>(projects.subList(start.intValue(), end.intValue()), pageable, projects.size());

        return pages;
    }

    @Override
    public Page<ProjectEntity> projectGlobalSearch(String keyword, List<Long> sdgIds, String country, ProjectStatusEnum status, Pageable pageable) {
        // first search by keywords

        List<ProjectEntity> initProjects = new ArrayList();
        if (keyword.equals("")) {
            System.err.println("key word is null");
            initProjects = projectEntityRepository.findAll();
        } else {

            Set<ProjectEntity> temp = new HashSet<>();
            // search the whole keyword, if empty, then split
            if (projectEntityRepository.searchByKeywords(keyword).isEmpty()) {
                String[] keywords = keyword.split(" ");
                for (String s : keywords) {
                    temp.addAll(projectEntityRepository.searchByKeywords(s));
                }
            } else {
                temp.addAll(projectEntityRepository.searchByKeywords(keyword));
            }

            for (ProjectEntity p : temp) {
                initProjects.add(p);
            }
        }
        // filter by country
        List<ProjectEntity> resultFilterByCountry = new ArrayList();
        if (!country.equals("")) {
            for (ProjectEntity p : initProjects) {
                if (p.getCountry().toLowerCase().equals(country.toLowerCase())) {
                    resultFilterByCountry.add(p);
                }
            }
        } else {
            System.err.println("country is null");
            resultFilterByCountry = initProjects;
        }

        // filter by status
        List<ProjectEntity> resultFilterByStatus = new ArrayList();
        if (status != null) {
            for (ProjectEntity p : resultFilterByCountry) {
                if (p.getProjStatus() == status) {
                    resultFilterByStatus.add(p);
                }
            }
        } else {
            resultFilterByStatus = resultFilterByCountry;
        }

        // filter by sdgs
        System.err.println("sdgIds:" + sdgIds.toString());
        System.err.println("sdgIds.contains(4);" + sdgIds.contains(4L));
        List<ProjectEntity> resultFilterBySDGs = new ArrayList();
        if (!sdgIds.isEmpty()) {
            for (ProjectEntity p : resultFilterByStatus) {
                System.err.println("P " + p.getProjectId());
                boolean contain = false;
                for (SDGEntity s : p.getSdgs()) {
                    System.out.println("HAHA" + s.getSdgId());
                    if (sdgIds.contains(s.getSdgId())) {
                        System.err.println(" contain" + s.getSdgId());
                        contain = true;
                    }
                }
                if (contain == true) {
                    resultFilterBySDGs.add(p);
                }
            }
        } else {
            System.err.println("sdg is null");
            resultFilterBySDGs = resultFilterByStatus;
        }

        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > resultFilterBySDGs.size() ? resultFilterBySDGs.size() : (start + pageable.getPageSize());
        Page<ProjectEntity> pages = new PageImpl<ProjectEntity>(resultFilterBySDGs.subList(start.intValue(), end.intValue()), pageable, resultFilterBySDGs.size());

        return pages;

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
    public List<ProjectEntity> getSpotlightedProjects() {
        return projectEntityRepository.getSpotlightedProjects();
    }

    @Override
    public Page<ProjectEntity> getSpotlightedProjects(Pageable pageable) {
        return projectEntityRepository.getSpotlightedProjects(pageable);
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

        if (projectOptional.get().getProjStatus() == ProjectStatusEnum.COMPLETED) {
            throw new UpvoteProjectException("Unable to upvote completed project");
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

        //newly added to keep track of poolpoints
        project.setProjectPoolPoints(100 + project.getUpvotes());

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

        if (projectOptional.get().getProjStatus() == ProjectStatusEnum.COMPLETED) {
            throw new DownvoteProjectException("Unable to downvote completed project");
        }

        if (!profOptional.isPresent()) {
            throw new UserNotFoundException("Upable to downvote project: User not found");
        }

        ProjectEntity project = projectOptional.get();
        ProfileEntity profile = profOptional.get();

        //******* user needs to have sufficient rep points in order to perform the downvote action
//        if (profile.getReputationPoints() < 50) {
//            throw new DownvoteProjectException("Unable to downvote: Account does not have sufficient rep points to downvote");
//        }
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

        //newly added to keep track of poolpoints
        project.setProjectPoolPoints(100 + project.getUpvotes());

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
    public Page<ProjectEntity> retrieveProjectBySDGIds(List<Long> sdgIds, Pageable pageable) throws ProjectNotFoundException {
        List<ProjectEntity> projects = new ArrayList<>();
        for (Long id : sdgIds) {
            Optional<SDGEntity> optionalSDG = sDGEntityRepository.findById(id);
            if (optionalSDG.isPresent()) {
                SDGEntity sdg = optionalSDG.get();
                projects.addAll(sdg.getProjects());
            } else {
                throw new ProjectNotFoundException("SDG does not exist");
            }
        }
        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > projects.size() ? projects.size() : (start + pageable.getPageSize());
        Page<ProjectEntity> pages = new PageImpl<ProjectEntity>(projects.subList(start.intValue(), end.intValue()), pageable, projects.size());
        return pages;
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

        // Only active project can recieving resource request
        if (project.getProjStatus() != ProjectStatusEnum.ACTIVE) {
            throw new JoinProjectException("Sorry action is only allowed for activated projects");
        }

        // One user can only make one join request to one project 
        Optional<JoinRequestEntity> requestOptional = joinRequestEntityRepository.searchJoinRequestProjectByProjectAndRequestorAndStatus(projectId, profileId, JoinRequestStatusEnum.ON_HOLD);
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

        // yet to create actual entity (before sending notification)
        List<String> projectOwnerUuids = new ArrayList<>();
        for (ProfileEntity owner : project.getProjectOwners()) {
            projectOwnerUuids.add(owner.getUuid().toString());
        }

        String requestorName = "";
        if (requestor instanceof IndividualEntity) {
            requestorName = ((IndividualEntity) requestor).getFirstName() + " " + ((IndividualEntity) requestor).getLastName();
        } else if (requestor instanceof OrganisationEntity) {
            requestorName = ((OrganisationEntity) requestor).getOrganizationName();
        }

        String title = "Join Project Request";
        String body = requestorName + " has applied to join your '" + project.getProjectTitle() + "' project.";

        SendNotificationsToUsersVO notificationVO = new SendNotificationsToUsersVO(
                projectOwnerUuids,
                AnnouncementTypeEnum.JOIN_PROJ_REQUEST.toString(),
                title,
                body,
                ""
        );
        firebaseService.sendNotificationsToUsers(notificationVO);
        joinRequest = joinRequestEntityRepository.saveAndFlush(joinRequest);
        // // make anouncement to project owners 
        List<ProfileEntity> projectOwners = project.getProjectOwners();
        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTitle(notificationVO.getTitle());
        announcementEntity.setContent(notificationVO.getBody());
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setType(AnnouncementTypeEnum.JOIN_PROJ_REQUEST);
        announcementEntity.setJoinRequestId(joinRequest.getJoinRequestId());
        announcementEntity.setProjectId(projectId);

        // association
        announcementEntity.getNotifiedUsers().addAll(projectOwners);
        for (ProfileEntity p : projectOwners) {
            p.getAnnouncements().add(announcementEntity);
        }
        announcementEntityRepository.saveAndFlush(announcementEntity);

        return joinRequest;
    }

    @Override
    public List<ProjectEntity> getProjectsByListOfIds(List<Long> ids) throws ProjectNotFoundException {
        List<ProjectEntity> listOfProjects = new ArrayList<>();
        ProjectEntity project;
        for (Long id : ids) {
            Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(id);
            if (projectOptional.isPresent()) {
                project = projectOptional.get();
                listOfProjects.add(project);
            } else {
                throw new ProjectNotFoundException("Project is not found");
            }

        }
        return listOfProjects;

    }

    @Override
    public Page<ProjectEntity> getFollowingProjectsByAccountId(Long accountId, Pageable pageable) {

        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        List<ProjectEntity> projects = profile.getProjectsFollowing();

        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > projects.size() ? projects.size() : (start + pageable.getPageSize());
        Page<ProjectEntity> page = new PageImpl<ProjectEntity>(projects.subList(start.intValue(), end.intValue()), pageable, projects.size());

        return page;
    }

    @Override
    public ProjectEntity addProjectOwner(Long projOwner, Long projOwnerToAdd, Long projectId) throws ProjectNotFoundException, ProjectNotFoundException, UnableToAddProjectOwnerException {

        ProfileEntity projOwnerProfile = profileEntityRepository.findById(projOwner)
                .orElseThrow(() -> new UserNotFoundException(projOwner));

        ProfileEntity projOwnerToAddProfile = profileEntityRepository.findById(projOwnerToAdd)
                .orElseThrow(() -> new UserNotFoundException(projOwnerToAdd));

        ProjectEntity project = projectEntityRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project " + projectId + " cannot be found."));

        //check if action is authorised
        if (!project.getProjectOwners().contains(projOwnerProfile)) {
            throw new UnableToAddProjectOwnerException("Unable to add new project owner "
                    + "into project: account must be a project owner to perform this action");
        }

        //check if profile to add is already a projectOwner 
        if (project.getProjectOwners().contains(projOwnerToAddProfile)) {
            throw new UnableToAddProjectOwnerException("Unable to add new project owner "
                    + "into project: account is already a project owner.");
        }

        project.getProjectOwners().add(projOwnerToAddProfile);
        if (project.getTeamMembers().contains(projOwnerToAddProfile)) {
            project.getTeamMembers().remove(projOwnerToAddProfile);
        }

        if (projOwnerToAddProfile.getProjectsJoined().contains(project)) {
            projOwnerToAddProfile.getProjectsJoined().remove(project);
        }
        project = projectEntityRepository.saveAndFlush(project);

        projOwnerToAddProfile.getProjectsOwned().add(project);
        profileEntityRepository.saveAndFlush(projOwnerToAddProfile);

        return project;
    }

    @Override
    public ProjectEntity removeProjectOwner(Long editorId, Long projOwnerToRemoveId, Long projectId) throws ProjectNotFoundException, UnableToRemoveProjectOwnerException {
        ProfileEntity editor = profileEntityRepository.findById(editorId)
                .orElseThrow(() -> new UserNotFoundException(editorId));

        ProfileEntity projOwnerToRemove = profileEntityRepository.findById(projOwnerToRemoveId)
                .orElseThrow(() -> new UserNotFoundException(projOwnerToRemoveId));

        ProjectEntity project = projectEntityRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project " + projectId + " cannot be found."));

        //check if action is authorised
        if (!project.getProjectOwners().contains(editor)) {
            throw new UnableToAddProjectOwnerException("Unable to remove new project owner "
                    + "into project: account must be a project owner to perform this action");
        }

        //check if profile is already removed from projectOwner 
        if (!project.getProjectOwners().contains(projOwnerToRemove)) {
            throw new UnableToRemoveProjectOwnerException("Unable to remove project owner "
                    + "into project: account is already not a project owner.");
        }

        // remove from project owners 
        project.getProjectOwners().remove(projOwnerToRemove);

        // add back to project teammates
        if (!project.getTeamMembers().contains(projOwnerToRemove)) {
            project.getTeamMembers().add(projOwnerToRemove);
        }

        project = projectEntityRepository.saveAndFlush(project);

        //add back to project joined
        if (!projOwnerToRemove.getProjectsJoined().contains(project)) {
            projOwnerToRemove.getProjectsJoined().add(project);
        }
        // remove from project owned
        projOwnerToRemove.getProjectsOwned().remove(project);
        profileEntityRepository.saveAndFlush(projOwnerToRemove);

        return project;
    }

    @Override
    public ProjectEntity followProject(Long followerId, Long projectId) throws ProjectNotFoundException, UserNotFoundException, FollowProjectException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        Optional<ProfileEntity> profOptional = profileEntityRepository.findById(followerId);

        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Unable to follow: Project not exist");
        }

        if (!profOptional.isPresent()) {
            throw new UserNotFoundException("Unable to follow: User not found");
        }

        ProjectEntity project = projectOptional.get();
        ProfileEntity follower = profOptional.get();
        if (project.getProjectFollowers().contains(follower)) {
            throw new FollowProjectException("You are already following this project!");
        }

        project.getProjectFollowers().add(follower);
        follower.getProjectsFollowing().add(project);
        project = projectEntityRepository.saveAndFlush(project);
        profileEntityRepository.saveAndFlush(follower);

        //announce project owners
        String followerName = "";
        if (follower instanceof IndividualEntity) {
            followerName = ((IndividualEntity) follower).getFirstName() + " " + ((IndividualEntity) follower).getLastName();
        } else if (follower instanceof OrganisationEntity) {
            followerName = ((OrganisationEntity) follower).getOrganizationName();
        }

        AnnouncementEntity announcementEntity = new AnnouncementEntity();
        announcementEntity.setTitle("New Project Follower");
        announcementEntity.setContent("Your project '" + project.getProjectTitle() + "' has a new follower '" + followerName + "'.");
        announcementEntity.setTimestamp(LocalDateTime.now());
        announcementEntity.setType(AnnouncementTypeEnum.NEW_PROJECT_FOLLOWER);
        announcementEntity.setNewFollowerAndNewPosterProfileId(followerId);
        announcementEntity.setNewFollowerAndNewPosterUUID(follower.getUuid());
        // association
        announcementEntity.getNotifiedUsers().addAll(project.getProjectOwners());
        for (ProfileEntity p : project.getProjectOwners()) {
            p.getAnnouncements().add(announcementEntity);
        }
        announcementEntity = announcementEntityRepository.saveAndFlush(announcementEntity);

        // create notification         
        announcementService.createNormalNotification(announcementEntity);

        return project;
    }

    @Override
    public void UnfollowProject(Long followerId, Long projectId) throws ProjectNotFoundException, UserNotFoundException, FollowProjectException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        Optional<ProfileEntity> profOptional = profileEntityRepository.findById(followerId);

        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Unable to unfollow project: Project not exist");
        }

        if (!profOptional.isPresent()) {
            throw new UserNotFoundException("Unable to unfollow project: User not found");
        }

        ProjectEntity project = projectOptional.get();
        ProfileEntity follower = profOptional.get();
        if (!project.getProjectFollowers().contains(follower)) {
            throw new FollowProjectException("You have already unfollowed this project!");
        }

        project.getProjectFollowers().remove(follower);
        follower.getProjectsFollowing().remove(project);
        projectEntityRepository.saveAndFlush(project);
        profileEntityRepository.saveAndFlush(follower);

    }

    @Override
    public List<ProjectEntity> getListOfFollowingProjectsByUserId(Long userId) throws UserNotFoundException {
        Optional<ProfileEntity> profOptional = profileEntityRepository.findById(userId);
        if (!profOptional.isPresent()) {
            throw new UserNotFoundException("Unable to get following projects: User not found");
        }

        ProfileEntity user = profOptional.get();
        return user.getProjectsFollowing();

    }

    @Override
    public List<ProfileEntity> getListOfFollowerByProjectId(Long projectId) throws ProjectNotFoundException {
        Optional<ProjectEntity> projectOptional = projectEntityRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            throw new ProjectNotFoundException("Unable to get project follower: Project not exist");
        }

        ProjectEntity project = projectOptional.get();
        return project.getProjectFollowers();
    }

}
