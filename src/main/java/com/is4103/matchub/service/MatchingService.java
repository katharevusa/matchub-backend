/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import java.util.List;

/**
 *
 * @author ngjin
 */
public interface MatchingService {

    List<ResourceEntity> recommendResources(Long projectId) throws ProjectNotFoundException;
}
