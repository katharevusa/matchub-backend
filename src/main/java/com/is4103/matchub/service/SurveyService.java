/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.QuestionEntity;
import com.is4103.matchub.entity.QuestionOptionEntity;
import com.is4103.matchub.entity.SurveyEntity;
import com.is4103.matchub.exception.DeleteQuestionException;
import com.is4103.matchub.exception.DeleteQuestionOptionException;
import com.is4103.matchub.exception.DeleteSurveyException;
import com.is4103.matchub.exception.QuestionNotFoundException;
import com.is4103.matchub.exception.QuestionOptionNotFoundException;
import com.is4103.matchub.exception.SurveyNotFoundException;
import com.is4103.matchub.vo.CreateQuestionOptionVO;
import com.is4103.matchub.vo.CreateQuestionVO;
import com.is4103.matchub.vo.SurveyVO;
import com.is4103.matchub.vo.UpdateQuestionOptionVO;
import com.is4103.matchub.vo.UpdateQuestionVO;
import java.util.List;

/**
 *
 * @author longluqian
 */
public interface SurveyService {

    public SurveyEntity createSurvey(SurveyVO vo);

    public SurveyEntity assignRespondents(List<Long> respondentId, Long surveyId) throws SurveyNotFoundException;

    public SurveyEntity updateSurvey(SurveyVO vo) throws SurveyNotFoundException;

    public void deleteSurvey(Long surveyId) throws SurveyNotFoundException, DeleteSurveyException, QuestionNotFoundException, DeleteQuestionException, QuestionOptionNotFoundException, DeleteQuestionOptionException;

    public void clearAllSurveyResponse(Long surveyId) throws SurveyNotFoundException;

    public void deleteOneSurveyResponse(Long surveyResponseId);

    public QuestionEntity createQuestion(CreateQuestionVO vo) throws SurveyNotFoundException, QuestionNotFoundException;

    public QuestionEntity updateQuestionNextQuestion(Long questionId, Long branchQuestionId) throws QuestionNotFoundException;

    public QuestionEntity updateQuestion(UpdateQuestionVO questionVO) throws QuestionNotFoundException;

    public QuestionOptionEntity createQuestionOption(CreateQuestionOptionVO createQuestionOptionVO) throws QuestionNotFoundException;

    public QuestionOptionEntity updateQuestionOption(UpdateQuestionOptionVO updateQuestionOptionVO) throws QuestionOptionNotFoundException;

    public void deleteQuestionOptions(Long questionId, Long questionOptionId) throws QuestionOptionNotFoundException, DeleteQuestionOptionException, QuestionNotFoundException;

    public QuestionEntity branchQuestionOptions(Long questionOptionId, Long branchedQuestionId) throws QuestionOptionNotFoundException, QuestionNotFoundException;

    public void deleteQuestion(Long questionId) throws QuestionNotFoundException, DeleteQuestionException, QuestionOptionNotFoundException, DeleteQuestionOptionException;
}
