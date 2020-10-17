/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.helper;

import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceEntity;
import lombok.Data;

/**
 *
 * @author ngjin
 */
@Data
public class MatchingScore {

    double score;

    ProjectEntity project;

    ResourceEntity resource;

    public MatchingScore() {
    }

    public MatchingScore(double score, ProjectEntity project) {
        this.score = score;
        this.project = project;
    }

    public MatchingScore(double score, ResourceEntity resource) {
        this.score = score;
        this.resource = resource;
    }

}
