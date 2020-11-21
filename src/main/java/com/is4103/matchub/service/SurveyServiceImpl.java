/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.QuestionEntity;
import com.is4103.matchub.entity.QuestionOptionEntity;
import com.is4103.matchub.entity.QuestionResponseEntity;
import com.is4103.matchub.entity.SurveyEntity;
import com.is4103.matchub.entity.SurveyResponseEntity;
import com.is4103.matchub.enumeration.QuestionTypeEnum;
import com.is4103.matchub.exception.DeleteQuestionException;
import com.is4103.matchub.exception.DeleteQuestionOptionException;
import com.is4103.matchub.exception.DeleteSurveyException;
import com.is4103.matchub.exception.QuestionNotFoundException;
import com.is4103.matchub.exception.QuestionOptionNotFoundException;
import com.is4103.matchub.exception.SurveyNotFoundException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.QuestionEntityRepository;
import com.is4103.matchub.repository.QuestionOptionEntityRepository;
import com.is4103.matchub.repository.QuestionResponseEntityRepository;
import com.is4103.matchub.repository.SurveyEntityRepository;
import com.is4103.matchub.repository.SurveyResponseEntityRepository;
import com.is4103.matchub.vo.CreateQuestionOptionVO;
import com.is4103.matchub.vo.CreateQuestionVO;
import com.is4103.matchub.vo.SurveyVO;
import com.is4103.matchub.vo.UpdateQuestionOptionVO;
import com.is4103.matchub.vo.UpdateQuestionVO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class SurveyServiceImpl implements SurveyService {

    @Autowired
    private QuestionEntityRepository questionEntityRepository;

    @Autowired
    private QuestionOptionEntityRepository questionOptionEntityRepository;

    @Autowired
    private QuestionResponseEntityRepository questionResponseEntityRepository;

    @Autowired
    private SurveyEntityRepository surveyEntityRepository;

    @Autowired
    private SurveyResponseEntityRepository surveyResponseEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

//    Create Survey
    @Override
    public SurveyEntity createSurvey(SurveyVO vo) {
        SurveyEntity surveyEntity = new SurveyEntity();
        vo.createSurvey(surveyEntity);
        return surveyEntityRepository.saveAndFlush(surveyEntity);
    }

    @Override
    public SurveyEntity assignRespondents(List<Long> respondentId, Long surveyId) throws SurveyNotFoundException {
        SurveyEntity survey = surveyEntityRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException());
        survey.setRecievers(new ArrayList<>());

        for (Long id : respondentId) {
            ProfileEntity user = profileEntityRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            survey.getRecievers().add(user);
            user.getSurveys().add(survey);
        }
        profileEntityRepository.flush();
        return surveyEntityRepository.saveAndFlush(survey);
    }

    //update survey basic information
    @Override
    public SurveyEntity updateSurvey(SurveyVO vo) throws SurveyNotFoundException {
        SurveyEntity survey = surveyEntityRepository.findById(vo.getSurveyId()).orElseThrow(() -> new SurveyNotFoundException());
        vo.updateSurvey(survey);

        return surveyEntityRepository.saveAndFlush(survey);
    }

    @Override
    public void deleteSurvey(Long surveyId)throws SurveyNotFoundException,DeleteSurveyException, QuestionNotFoundException, DeleteQuestionException, QuestionOptionNotFoundException,DeleteQuestionOptionException{
        SurveyEntity survey = surveyEntityRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException());
        if(!survey.getSurveyResponses().isEmpty()){
            throw new DeleteSurveyException("Unable to delete the survey as someone has already responded");
        }
        
        for(QuestionEntity q : survey.getQuestions()){
            deleteQuestion(q.getQuestionId());
        }
        
        for(ProfileEntity p : survey.getRecievers()){
            p.getSurveys().remove(survey);
        }
        survey.setRecievers(new ArrayList<ProfileEntity>());
        
        surveyEntityRepository.delete(survey);
        
    }
//    
    @Override
    public void clearAllSurveyResponse(Long surveyId)throws SurveyNotFoundException{
        SurveyEntity survey = surveyEntityRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException());
        List<SurveyResponseEntity> responses = survey.getSurveyResponses();
        for(SurveyResponseEntity sr : responses){
            deleteOneSurveyResponse(sr.getSurveyResponseId());
        }
         surveyEntityRepository.flush();
    }
    
    
    @Override
    public void deleteOneSurveyResponse(Long surveyResponseId){
        SurveyResponseEntity surveyResponse = surveyResponseEntityRepository.findById(surveyResponseId).get();
        // association between survey and survey response
        SurveyEntity survey = surveyResponse.getSurvey();
        survey.getSurveyResponses().remove(surveyResponse);
        surveyResponse.setSurvey(null);
        
        // delete all question responses
        List<QuestionResponseEntity> questionResponseEntities = surveyResponse.getQuestionResponses();
        for(QuestionResponseEntity qr : questionResponseEntities){
            qr.setSelectedOptions(new ArrayList<>());
            QuestionEntity question = qr.getQuestion();
            question.getQuestionResponses().remove(qr);
            qr.setQuestion(null);
        }
       
        surveyResponse.setQuestionResponses(new ArrayList<>());
        questionResponseEntityRepository.deleteAll(questionResponseEntities);
        surveyResponseEntityRepository.flush();
        
        // remove association between survey response and user
        ProfileEntity profileEntity = surveyResponse.getRespondent();
        profileEntity.getSurveyResponses().remove(surveyResponse);
        surveyResponse.setRespondent(null);
        surveyResponseEntityRepository.delete(surveyResponse);
        
    }
    // default that  hasBranch is false
    @Override
    public QuestionEntity createQuestion(CreateQuestionVO vo) throws SurveyNotFoundException {
        SurveyEntity survey = surveyEntityRepository.findById(vo.getSurveyId()).orElseThrow(() -> new SurveyNotFoundException());
        QuestionEntity question = new QuestionEntity();
        vo.createQuestion(question);
        question.setSurvey(survey);
        question = questionEntityRepository.saveAndFlush(question);
        survey.getQuestions().add(question);

        if (question.getQuestionType() == QuestionTypeEnum.MULTIPLE_CHOICE_QUESTION || question.getQuestionType() == QuestionTypeEnum.MULTIPLE_RESPONSE_QUESTION) {
            // create two options
            QuestionOptionEntity option1 = new QuestionOptionEntity();
            option1.setOptionContent("Option 1");
            question.getOptions().add(option1);
            option1.setQuestion(question);
            questionOptionEntityRepository.saveAndFlush(option1);

            QuestionOptionEntity option2 = new QuestionOptionEntity();
            option2.setOptionContent("Option 2");
            question.getOptions().add(option2);
            option2.setQuestion(question);
            questionOptionEntityRepository.saveAndFlush(option2);

        }
        surveyEntityRepository.flush();
        return questionEntityRepository.saveAndFlush(question);

    }

    @Override
    public QuestionEntity updateQuestionNextQuestion(Long questionId, Long branchQuestionId) throws QuestionNotFoundException {
        QuestionEntity question = questionEntityRepository.findById(questionId).orElseThrow(() -> new QuestionNotFoundException("Unable to find question"));
        QuestionEntity branchQuestion = questionEntityRepository.findById(branchQuestionId).orElseThrow(() -> new QuestionNotFoundException("Unable to find question"));

        question.setNextQuestionId(questionId);
        return questionEntityRepository.saveAndFlush(question);
    }

    @Override
    public QuestionEntity updateQuestion(UpdateQuestionVO questionVO) throws QuestionNotFoundException {
        QuestionEntity question = questionEntityRepository.findById(questionVO.getQuestionId()).orElseThrow(() -> new QuestionNotFoundException("Unable to find question"));
        questionVO.updateQuestion(question);

        return questionEntityRepository.saveAndFlush(question);

    }

    @Override
    public QuestionOptionEntity createQuestionOption(CreateQuestionOptionVO createQuestionOptionVO) throws QuestionNotFoundException {
        QuestionEntity question = questionEntityRepository.findById(createQuestionOptionVO.getQuestionId()).orElseThrow(() -> new QuestionNotFoundException("Unable to find question"));
        QuestionOptionEntity questionOption = new QuestionOptionEntity();
        questionOption.setOptionContent(createQuestionOptionVO.getOptionContent());
        questionOption.setQuestion(question);
        question.getOptions().add(questionOption);

        questionEntityRepository.flush();
        return questionOptionEntityRepository.saveAndFlush(questionOption);
    }

    @Override
    public QuestionOptionEntity updateQuestionOption(UpdateQuestionOptionVO updateQuestionOptionVO) throws QuestionOptionNotFoundException {
        QuestionOptionEntity questionOptionEntity = questionOptionEntityRepository.findById(updateQuestionOptionVO.getQuestionOptionId()).orElseThrow(() -> new QuestionOptionNotFoundException());
        questionOptionEntity.setOptionContent(updateQuestionOptionVO.getOptionContent());

        return questionOptionEntityRepository.saveAndFlush(questionOptionEntity);
    }

    @Override
    public void deleteQuestionOptions(Long questionId, Long questionOptionId) throws QuestionOptionNotFoundException, DeleteQuestionOptionException, QuestionNotFoundException {
        QuestionOptionEntity questionOptionEntity = questionOptionEntityRepository.findById(questionOptionId).orElseThrow(() -> new QuestionOptionNotFoundException());
        QuestionEntity question = questionEntityRepository.findById(questionId).orElseThrow(() -> new QuestionNotFoundException("Unable to find question"));
        for (QuestionResponseEntity q : question.getQuestionResponses()) {
            if (q.getSelectedOptions().contains(questionOptionEntity)) {
                throw new DeleteQuestionOptionException("This option has been selected by some users, can not be deleted");
            }
        }
        question.getOptions().remove(questionOptionEntity);
        questionOptionEntity.setQuestion(null);
        questionOptionEntityRepository.delete(questionOptionEntity);
        questionEntityRepository.flush();
    }

    @Override
    public QuestionEntity branchQuestionOptions(Long questionOptionId, Long branchedQuestionId) throws QuestionOptionNotFoundException, QuestionNotFoundException {
        QuestionOptionEntity questionOptionEntity = questionOptionEntityRepository.findById(questionOptionId).orElseThrow(() -> new QuestionOptionNotFoundException());
        QuestionEntity branchedQuestion = questionEntityRepository.findById(branchedQuestionId).orElseThrow(() -> new QuestionNotFoundException("Unable to find question"));

        QuestionEntity question = questionOptionEntity.getQuestion();
        question.setHasBranching(Boolean.TRUE);
        question.getOptionToQuestion().put(questionOptionId, branchedQuestionId);

        return questionEntityRepository.saveAndFlush(question);
    }

    @Override
    public void deleteQuestion(Long questionId) throws QuestionNotFoundException,DeleteQuestionException,QuestionOptionNotFoundException, DeleteQuestionOptionException {
        QuestionEntity questionToBeDeleted = questionEntityRepository.findById(questionId).orElseThrow(() -> new QuestionNotFoundException("Unable to find question"));
        if(!questionToBeDeleted.getQuestionResponses().isEmpty()){
            throw new DeleteQuestionException();
        }
        
        for(QuestionOptionEntity q : questionToBeDeleted.getOptions()){
            deleteQuestionOptions(questionId, q.getQuestionOptionsId());
        }
        // set association between survey and question
        SurveyEntity survey = questionToBeDeleted.getSurvey();
        survey.getQuestions().remove(questionToBeDeleted);
        questionToBeDeleted.setSurvey(null);
        
        questionEntityRepository.delete(questionToBeDeleted);
        surveyEntityRepository.flush();
    }

//Set branching to question options   
//    View all questionnaire responses
}
