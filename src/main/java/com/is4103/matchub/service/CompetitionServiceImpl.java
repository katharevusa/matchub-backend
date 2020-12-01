package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.CompetitionEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.entity.VoterCredentialEntity;
import com.is4103.matchub.enumeration.CompetitionStatusEnum;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.exception.CompetitionNotFoundException;
import com.is4103.matchub.exception.EmailExistException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.SDGEntityNotFoundException;
import com.is4103.matchub.exception.UnableToCreateVotingDetailsException;
import com.is4103.matchub.exception.UnableToDeleteCompetitionException;
import com.is4103.matchub.exception.UnableToJoinCompetitionException;
import com.is4103.matchub.exception.UnableToUpdateCompetitionException;
import com.is4103.matchub.exception.UnableToVoteForProjectException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.exception.VoterCredentialException;
import com.is4103.matchub.helper.RandomAlphanumericString;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.CompetitionEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import com.is4103.matchub.vo.CompetitionVO;
import com.is4103.matchub.vo.VoterVO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.is4103.matchub.repository.VoterCredentialEntityRepository;

/**
 *
 * @author tjle2
 */
@Service
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    CompetitionEntityRepository competitionEntityRepository;

    @Autowired
    ProjectEntityRepository projectEntityRepository;

    @Autowired
    ProfileEntityRepository profileEntityRepository;

    @Autowired
    AccountEntityRepository accountEntityRepository;

    @Autowired
    VoterCredentialEntityRepository voterCredentialEntityRepository;

    @Autowired
    SDGEntityRepository sdgEntityRepository;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public CompetitionEntity createCompetition(CompetitionVO vo) {
        CompetitionEntity newCompetition = new CompetitionEntity();
        vo.updateCompetition(newCompetition);

        return competitionEntityRepository.saveAndFlush(newCompetition);
    }

    @Transactional
    @Override
    public CompetitionEntity uploadPhotos(Long competitionId, MultipartFile[] photos) {
        CompetitionEntity competitionToAddPhotosTo = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        for (MultipartFile photo : photos) {
            String path = attachmentService.upload(photo);
            competitionToAddPhotosTo.getPhotos().add(path);
        }

        return competitionEntityRepository.saveAndFlush(competitionToAddPhotosTo);
    }

    @Transactional
    @Override
    public CompetitionEntity deletePhotos(Long competitionId, String[] photoToDelete) throws IOException {
        CompetitionEntity competitionToDeletePhotosFrom = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        for (String s : photoToDelete) {
            if (!competitionToDeletePhotosFrom.getPhotos().contains(s)) {
                throw new UnableToUpdateCompetitionException("Unable to delete photos: photos not found");
            }
        }

        for (String s : photoToDelete) {
            competitionToDeletePhotosFrom.getPhotos().remove(s);
            attachmentService.deleteFile(s);
        }

        return competitionEntityRepository.saveAndFlush(competitionToDeletePhotosFrom);
    }

    @Transactional
    @Override
    public CompetitionEntity uploadDocuments(Long competitionId, MultipartFile[] documents) {
        CompetitionEntity competitionToAddDocumentsTo = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        for (MultipartFile document : documents) {
            String path = attachmentService.upload(document);
            String name = document.getOriginalFilename();
            competitionToAddDocumentsTo.getDocuments().put(name, path);
        }

        return competitionEntityRepository.saveAndFlush(competitionToAddDocumentsTo);
    }

    @Transactional
    @Override
    public CompetitionEntity deleteDocuments(Long competitionId, String[] docsToDelete) throws IOException {
        CompetitionEntity competitionToDeleteDocumentsFrom = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        Map<String, String> hashmap = competitionToDeleteDocumentsFrom.getDocuments();

        //loop 1: check if all the documents are present
        for (String key : docsToDelete) {
            //get the path of the document to delete
            String selectedDocumentPath = hashmap.get(key);
            if (selectedDocumentPath == null) {
                throw new UnableToUpdateCompetitionException("Unable to delete competition document (Document not found): " + key);
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
        competitionToDeleteDocumentsFrom.setDocuments(hashmap);
        return competitionEntityRepository.saveAndFlush(competitionToDeleteDocumentsFrom);

    }

    @Transactional
    @Override
    public List<CompetitionEntity> retrieveAllCompetitions() {
        return competitionEntityRepository.findAll();
    }

    @Transactional
    @Override
    public List<CompetitionEntity> retrieveAllActiveCompetitions() {
        return competitionEntityRepository.findActiveCompetitions();
    }

    @Transactional
    @Override
    public CompetitionEntity retrieveCompetitionById(Long competitionId) {
        return competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));
    }

    @Transactional
    @Override
    public CompetitionEntity updateCompetition(CompetitionVO vo, Long competitionId) {
        CompetitionEntity competitionToUpdate = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        vo.updateCompetition(competitionToUpdate);

        return competitionEntityRepository.saveAndFlush(competitionToUpdate);
    }

    @Transactional
    @Override
    public void deleteCompetition(Long competitionId) {
        CompetitionEntity competitionToDelete = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        if (competitionToDelete.getProjects().size() > 0) {
            throw new UnableToDeleteCompetitionException("Competition already has projects which joined, unable to delete.");
        } else {
            competitionEntityRepository.delete(competitionToDelete);
        }
    }

    @Transactional
    @Override
    public ProjectEntity joinCompetition(Long competitionId, Long projectId) throws ProjectNotFoundException {
        CompetitionEntity competitionToAssociateWith = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        ProjectEntity projectToAssociateWith = projectEntityRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " does not exist"));

        if (projectToAssociateWith.getCompetition() != null) {
            throw new UnableToJoinCompetitionException("Project has already been entered into a competition before.");
        }

        if (projectToAssociateWith.getProjStatus() != ProjectStatusEnum.ACTIVE) {
            throw new UnableToJoinCompetitionException("Only active projects are allowed to joined competitions.");
        }

        // reset competition votes to 0
        projectToAssociateWith.setCompetitionVotes(0);

        competitionToAssociateWith.getProjects().add(projectToAssociateWith);
        projectToAssociateWith.setCompetition(competitionToAssociateWith);

        competitionEntityRepository.saveAndFlush(competitionToAssociateWith);
        projectEntityRepository.saveAndFlush(projectToAssociateWith);

        return projectToAssociateWith;
    }

    @Transactional
    @Override
    public CompetitionEntity activateCompetition(Long competitionId) throws MessagingException, IOException {
        List<CompetitionEntity> currentActiveCompetitions = competitionEntityRepository.findActiveCompetitions();

        if (currentActiveCompetitions.size() > 0) {
            throw new UnableToUpdateCompetitionException("There is currently one ongoing active competition already.");
        }

        CompetitionEntity competitionToActivate = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        LocalDateTime timeNow = LocalDateTime.now();

        if (timeNow.isAfter(competitionToActivate.getEndDate())) {
            throw new UnableToUpdateCompetitionException("Unable to activate competition as the end date is in the past.");
        }

        competitionToActivate.setCompetitionStatus(CompetitionStatusEnum.ACTIVE);

//      currently send to only 1 person
//      dummy person
        AccountEntity account = userService.getAccount(5l);

//      method is set to send to tellybuddy3106@gmail.com;
        emailService.sendCompetitionEmailAlert(account, competitionToActivate);

//        by right supposed to send to all accounts, but commented out to prevent spam
//        List<AccountEntity> activeAccounts = userService.getAllActiveAccounts();
//
//        for (AccountEntity accountEntity : activeAccounts) {
//            emailService.sendCompetitionEmailAlert(accountEntity, competitionToActivate);
//        }
        return competitionEntityRepository.saveAndFlush(competitionToActivate);
    }

    @Transactional
    @Override
    public List<ProjectEntity> retrieveProjectsByCompetitionId(Long competitionId) {
        CompetitionEntity competition = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        return competition.getProjects();
    }

    @Transactional
    @Override
    public List<ProjectEntity> retrieveProjectsByCompetitionIdAndSdgIdAndSdgTargets(Long competitionId, Long sdgId, long[] sdgTargetIds) {
        CompetitionEntity competition = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        SDGEntity sdg = sdgEntityRepository.findById(sdgId)
                .orElseThrow(() -> new SDGEntityNotFoundException("SDG with id " + sdgId + " does not exist"));

        return competitionEntityRepository.findActiveCompetitionProjectsBySdgAndSdgTargets(competition, sdg, sdgTargetIds);
    }

    @Transactional
    @Override
    public void registerAsVoter(VoterVO vo, Long competitionId) throws IOException, MessagingException {

        CompetitionEntity competition = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        Optional<AccountEntity> account = accountEntityRepository.findByEmail(vo.getEmail());

        if (account.isPresent()) {
            throw new EmailExistException("Email has already been used once, try another email.");
        }

        String randomGeneratedPassword = RandomAlphanumericString.randomString(12);

        IndividualEntity newIndividual = new IndividualEntity();
        newIndividual.setFirstName(vo.getFirstName());
        newIndividual.setLastName(vo.getLastName());
        newIndividual.setEmail(vo.getEmail());
        newIndividual.getRoles().add(AccountEntity.ROLE_USER);
        newIndividual.setUuid(UUID.randomUUID());
        newIndividual.setJoinDate(LocalDateTime.now());
        newIndividual.setPassword(passwordEncoder.encode(randomGeneratedPassword));

        AccountEntity newAccount = (AccountEntity) newIndividual;
        newAccount = accountEntityRepository.saveAndFlush(newAccount);

        ProfileEntity newProfile = (ProfileEntity) newAccount;

        VoterCredentialEntity voterCredential = new VoterCredentialEntity();
        voterCredential.setVoter(newProfile);
        voterCredential.setCompetition(competition);
        voterCredential.setVoterSecret(RandomAlphanumericString.randomString(12));

        voterCredential = voterCredentialEntityRepository.saveAndFlush(voterCredential);

        competition.getVoterCredentials().add(voterCredential);
        competition = competitionEntityRepository.saveAndFlush(competition);

        newIndividual = (IndividualEntity) newAccount;

        emailService.sendVoterRegisterEmail(newIndividual, randomGeneratedPassword, voterCredential, competition);
    }

    @Transactional
    @Override
    public void createVotingDetailsForExistingUsers(Long competitionId, Long accountId) throws IOException, MessagingException {

        CompetitionEntity competition = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        ProfileEntity profileToIssueVotingDetailsTo = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + accountId + " does not exist"));

        Optional<VoterCredentialEntity> existingVoterDetails = voterCredentialEntityRepository.findExistingVoterCredentialDetails(profileToIssueVotingDetailsTo, competition);

        if (existingVoterDetails.isPresent()) {
            throw new UnableToCreateVotingDetailsException("You have already requested for your voting details for this competition. Please check your email.");
        }

        VoterCredentialEntity voterCredential = new VoterCredentialEntity();
        voterCredential.setVoter(profileToIssueVotingDetailsTo);
        voterCredential.setCompetition(competition);
        voterCredential.setVoterSecret(RandomAlphanumericString.randomString(12));

        voterCredential = voterCredentialEntityRepository.saveAndFlush(voterCredential);

        competition.getVoterCredentials().add(voterCredential);
        competition = competitionEntityRepository.saveAndFlush(competition);

        emailService.sendExistingUserVotingDetailsEmail(profileToIssueVotingDetailsTo, voterCredential, competition);
    }

    @Transactional
    @Override
    public void castVoteForCompetitionProject(Long competitionId, Long projectId, String voterSecret) throws ProjectNotFoundException {

        CompetitionEntity competition = competitionEntityRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionNotFoundException("Competition with id " + competitionId + " does not exist"));

        ProjectEntity projectToVoteFor = projectEntityRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " does not exist"));

        VoterCredentialEntity voterCredential = voterCredentialEntityRepository.findByVoterSecret(voterSecret)
                .orElseThrow(() -> new VoterCredentialException("Provided Voter Secret does not exist"));

        if (voterCredential.getCompetition().getCompetitionId() != competitionId) {
            throw new VoterCredentialException("Unable to use this Voter Secret for this competition, "
                    + "make sure you are using the right secret for the right competition.");
        }

        if (voterCredential.getIsUsed()) {
            throw new VoterCredentialException("You have already used this Voter Secret once.");
        }

        LocalDateTime timeNow = LocalDateTime.now();

        if (timeNow.isBefore(competition.getStartDate()) || timeNow.isAfter(competition.getEndDate())) {
            throw new UnableToVoteForProjectException("Voting period has not opened yet, please check the competition dates.");
        }

        projectToVoteFor.setCompetitionVotes(projectToVoteFor.getCompetitionVotes() + 1);
        voterCredential.setIsUsed(Boolean.TRUE);
        projectEntityRepository.saveAndFlush(projectToVoteFor);
        voterCredentialEntityRepository.saveAndFlush(voterCredential);
    }
}
