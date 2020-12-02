package com.is4103.matchub.controller;

import com.is4103.matchub.entity.CompetitionEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.entity.VoterCredentialEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.service.CompetitionService;
import com.is4103.matchub.service.SDGService;
import com.is4103.matchub.vo.CompetitionVO;
import com.is4103.matchub.vo.VoterVO;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
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
 * @author tjle2
 */
@RestController
@RequestMapping("/authenticated")
public class CompetitionController {

    @Autowired
    CompetitionService competitionService;

    @Autowired
    SDGService sdgService;

    @RequestMapping(method = RequestMethod.POST, value = "/createCompetition")
    public CompetitionEntity createCompetition(@Valid @RequestBody CompetitionVO competitionVO) {
        return competitionService.createCompetition(competitionVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateCompetition/uploadPhotos")
    public CompetitionEntity uploadPhotos(@RequestParam(value = "photos") MultipartFile[] photos, @RequestParam("competitionId") Long competitionId) {
        return competitionService.uploadPhotos(competitionId, photos);
    }

    // pass photo full path
    @RequestMapping(method = RequestMethod.DELETE, value = "/updateCompetition/deletePhotos")
    public CompetitionEntity deletePhotos(@RequestParam(value = "competitionId") Long competitionId, @RequestParam(value = "photosToDelete") String[] photosToDelete) throws IOException {
        return competitionService.deletePhotos(competitionId, photosToDelete);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateCompetition/uploadDocuments")
    public CompetitionEntity uploadDocuments(@RequestParam(value = "documents") MultipartFile[] documents, @RequestParam("competitionId") Long competitionId) {
        return competitionService.uploadDocuments(competitionId, documents);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/updateCompetition/deleteDocuments")
    public CompetitionEntity deleteDocuments(@RequestParam(value = "competitionId") Long competitionId, @RequestParam(value = "docsToDelete") String[] docsToDelete) throws IOException {
        return competitionService.deleteDocuments(competitionId, docsToDelete);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllCompetitions")
    public List<CompetitionEntity> getAllCompetitions() {
        return competitionService.retrieveAllCompetitions();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllActiveCompetitions")
    public List<CompetitionEntity> getAllActiveCompetitions() {
        return competitionService.retrieveAllActiveCompetitions();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllPastCompetitions")
    public List<CompetitionEntity> getAllPastCompetitions() {
        return competitionService.retrieveAllPastCompetitions();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getCompetitionById")
    public CompetitionEntity getCompetitionByCompetitionId(@RequestParam(value = "competitionId", required = true) Long competitionId) {
        return competitionService.retrieveCompetitionById(competitionId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateCompetition")
    public CompetitionEntity updateCompetition(@Valid @RequestBody CompetitionVO competitionVO, @RequestParam(value = "competitionId", required = true) Long competitionId) {
        return competitionService.updateCompetition(competitionVO, competitionId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteCompetition")
    public void deleteCompetition(@RequestParam(value = "competitionId", required = true) Long competitionId) {
        competitionService.deleteCompetition(competitionId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/joinCompetition")
    public ProjectEntity joinCompetition(@RequestParam(value = "competitionId", required = true) Long competitionId,
            @RequestParam(value = "projectId", required = true) Long projectId,
            @RequestParam(value = "accountId", required = true) Long accountId) throws ProjectNotFoundException {
        return competitionService.joinCompetition(competitionId, projectId, accountId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/activateCompetition")
    public CompetitionEntity activateCompetition(@RequestParam(value = "competitionId", required = true) Long competitionId) throws IOException, MessagingException {
        return competitionService.activateCompetition(competitionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getCompetitionProjects")
    public List<ProjectEntity> getCompetitionProjects(@RequestParam(value = "competitionId", required = true) Long competitionId) {
        return competitionService.retrieveProjectsByCompetitionId(competitionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getCompetitionProjectsBySdg")
    public List<ProjectEntity> getCompetitionProjectsBySdg(@RequestParam(value = "competitionId", required = true) Long competitionId,
            @RequestParam(value = "sdgId", required = true) Long sdgId,
            @RequestParam(value = "sdgTargetIds", required = true, defaultValue = "") long[] sdgTargetIds) {

        if (sdgTargetIds.length == 0) {
            SDGEntity sdg = sdgService.getSdgBySdgId(sdgId);

            long[] defaultSdgTargetIds = new long[sdg.getTargets().size()];

            for (int i = 0; i < sdg.getTargets().size(); i++) {
                defaultSdgTargetIds[i] = sdg.getTargets().get(i).getSdgTargetId();
            }

            sdgTargetIds = defaultSdgTargetIds;
        }

        return competitionService.retrieveProjectsByCompetitionIdAndSdgIdAndSdgTargets(competitionId, sdgId, sdgTargetIds);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/registerAsVoter")
    public VoterCredentialEntity registerAsVoter(@Valid @RequestBody VoterVO vo, @RequestParam(value = "competitionId", required = true) Long competitionId) throws IOException, MessagingException {
        return competitionService.registerAsVoter(vo, competitionId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createVotingDetailsForExistingUsers")
    public VoterCredentialEntity createVotingDetailsForExistingUsers(@RequestParam(value = "competitionId", required = true) Long competitionId,
            @RequestParam(value = "accountId", required = true) Long accountId) throws IOException, MessagingException {
        return competitionService.createVotingDetailsForExistingUsers(competitionId, accountId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/castVoteForCompetitionProject")
    public void castVoteForCompetitionProject(@RequestParam(value = "competitionId", required = true) Long competitionId,
            @RequestParam(value = "projectId", required = true) Long projectId,
            @RequestParam(value = "voterSecret", required = true) String voterSecret) throws ProjectNotFoundException {
        competitionService.castVoteForCompetitionProject(competitionId, projectId, voterSecret);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getCompetitionResults")
    public List<ProjectEntity> getCompetitionResults(@RequestParam(value = "competitionId", required = true) Long competitionId) throws ProjectNotFoundException {
        return competitionService.retrieveCompetitionResults(competitionId);
    }
}
