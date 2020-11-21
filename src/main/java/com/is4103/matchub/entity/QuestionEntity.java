/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.is4103.matchub.enumeration.QuestionTypeEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author longluqian
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false, length = 1000)
    @NotNull
    private String question;

    @Column(nullable = true)
    private Long nextQuestionId;

    @ElementCollection
    private Map<Long, Long> optionToQuestion;

    @Column(nullable = false)
    private Boolean hasBranching = false;

    @NotNull
    private QuestionTypeEnum questionType;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties({"questions"})
    private SurveyEntity survey;

    @OneToMany(mappedBy = "question")
    private List<QuestionOptionEntity> options = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    private List<QuestionResponseEntity> questionResponses = new ArrayList<>();

}
