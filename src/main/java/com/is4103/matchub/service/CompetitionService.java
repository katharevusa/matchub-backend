package com.is4103.matchub.service;

import com.is4103.matchub.entity.CompetitionEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.vo.CompetitionVO;
import com.is4103.matchub.vo.VoterVO;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author tjle2
 */
public interface CompetitionService {

    public CompetitionEntity createCompetition(CompetitionVO vo);

    public CompetitionEntity uploadPhotos(Long competitionId, MultipartFile[] photos);

    public CompetitionEntity deletePhotos(Long competitionId, String[] photoToDelete) throws IOException;

    public CompetitionEntity uploadDocuments(Long competitionId, MultipartFile[] documents);

    public CompetitionEntity deleteDocuments(Long competitionId, String[] docsToDelete) throws IOException;

    public List<CompetitionEntity> retrieveAllCompetitions();

    public List<CompetitionEntity> retrieveAllActiveCompetitions();

    public CompetitionEntity retrieveCompetitionById(Long competitionId);

    public CompetitionEntity updateCompetition(CompetitionVO vo, Long competitionId);

    public void deleteCompetition(Long competitionId);

    public ProjectEntity joinCompetition(Long competitionId, Long projectId) throws ProjectNotFoundException;

    public CompetitionEntity activateCompetition(Long competitionId) throws MessagingException, IOException;

    public List<ProjectEntity> retrieveProjectsByCompetitionId(Long competitionId);

    public List<ProjectEntity> retrieveProjectsByCompetitionIdAndSdgIdAndSdgTargets(Long competitionId, Long sdgId, long[] sdgTargetIds);

    public void registerAsVoter(VoterVO vo, Long competitionId) throws IOException, MessagingException;

    public void createVotingDetailsForExistingUsers(Long competitionId, Long accountId) throws IOException, MessagingException;

    public void castVoteForCompetitionProject(Long competitionId, Long projectId, String voterSecret) throws ProjectNotFoundException;

}
