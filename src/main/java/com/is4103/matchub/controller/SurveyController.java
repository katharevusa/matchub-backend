/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

import com.is4103.matchub.entity.QuestionEntity;
import com.is4103.matchub.entity.QuestionOptionEntity;
import com.is4103.matchub.entity.SurveyEntity;
import com.is4103.matchub.exception.DeleteQuestionException;
import com.is4103.matchub.exception.DeleteQuestionOptionException;
import com.is4103.matchub.exception.DeleteSurveyException;
import com.is4103.matchub.exception.QuestionNotFoundException;
import com.is4103.matchub.exception.QuestionOptionNotFoundException;
import com.is4103.matchub.exception.SurveyNotFoundException;
import com.is4103.matchub.service.SurveyService;
import com.is4103.matchub.vo.CreateQuestionOptionVO;
import com.is4103.matchub.vo.CreateQuestionVO;
import com.is4103.matchub.vo.SurveyVO;
import com.is4103.matchub.vo.UpdateQuestionOptionVO;
import com.is4103.matchub.vo.UpdateQuestionVO;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author longluqian
 */
@RestController
@RequestMapping("/authenticated")
public class SurveyController {

    @Autowired
    SurveyService surveyService;

    @RequestMapping(method = RequestMethod.POST, value = "/createSurvey")
    public SurveyEntity createSurvey(@Valid @RequestBody SurveyVO vo) {
        return surveyService.createSurvey(vo);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/assignRespondents")
    public SurveyEntity assignRespondents(List<Long> respondentId, @RequestParam(value = "surveyId", required = true) Long surveyId) throws SurveyNotFoundException {
        return surveyService.assignRespondents(respondentId, surveyId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateSurvey")
    public SurveyEntity updateSurvey(@Valid @RequestBody SurveyVO vo) throws SurveyNotFoundException {
        return surveyService.updateSurvey(vo);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteSurvey")
    public void deleteSurvey(@RequestParam(value = "surveyId", required = true)Long surveyId) throws SurveyNotFoundException, DeleteSurveyException, QuestionNotFoundException, DeleteQuestionException, QuestionOptionNotFoundException, DeleteQuestionOptionException {
        surveyService.deleteSurvey(surveyId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/clearAllSurveyResponse")
    public void clearAllSurveyResponse(@RequestParam(value = "surveyId", required = true)Long surveyId) throws SurveyNotFoundException {
        surveyService.clearAllSurveyResponse(surveyId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteOneSurveyResponse")
    public void deleteOneSurveyResponse(@RequestParam(value = "surveyResponseId", required = true)Long surveyResponseId) {
        surveyService.deleteOneSurveyResponse(surveyResponseId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createQuestion")
    public QuestionEntity createQuestion(@Valid @RequestBody CreateQuestionVO vo) throws SurveyNotFoundException {
        return surveyService.createQuestion(vo);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateQuestionNextQuestion")
    public QuestionEntity updateQuestionNextQuestion(@RequestParam(value = "questionId", required = true)Long questionId,@RequestParam(value = "branchQuestionId", required = true) Long branchQuestionId) throws QuestionNotFoundException {
        return surveyService.updateQuestionNextQuestion(questionId, branchQuestionId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateQuestion")
    public QuestionEntity updateQuestion(@Valid @RequestBody UpdateQuestionVO questionVO) throws QuestionNotFoundException {
        return surveyService.updateQuestion(questionVO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createQuestionOption")
    public QuestionOptionEntity createQuestionOption(@Valid @RequestBody CreateQuestionOptionVO createQuestionOptionVO) throws QuestionNotFoundException {
        return surveyService.createQuestionOption(createQuestionOptionVO);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateQuestionOption")
    public QuestionOptionEntity updateQuestionOption(@Valid @RequestBody UpdateQuestionOptionVO updateQuestionOptionVO) throws QuestionOptionNotFoundException {
        return surveyService.updateQuestionOption(updateQuestionOptionVO);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteQuestionOptions")
    public void deleteQuestionOptions(@RequestParam(value = "questionId", required = true)Long questionId,@RequestParam(value = "questionOptionId", required = true) Long questionOptionId) throws QuestionOptionNotFoundException, DeleteQuestionOptionException, QuestionNotFoundException {
        surveyService.deleteQuestionOptions(questionId, questionOptionId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/branchQuestionOptions")
    public QuestionEntity branchQuestionOptions(@RequestParam(value = "questionOptionId", required = true)Long questionOptionId,@RequestParam(value = "branchedQuestionId", required = true) Long branchedQuestionId) throws QuestionOptionNotFoundException, QuestionNotFoundException {
        return surveyService.branchQuestionOptions(questionOptionId, branchedQuestionId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteQuestion")
    public void deleteQuestion(@RequestParam(value = "questionId", required = true)Long questionId) throws QuestionNotFoundException, DeleteQuestionException, QuestionOptionNotFoundException, DeleteQuestionOptionException {
        surveyService.deleteQuestion(questionId);

    }

}
