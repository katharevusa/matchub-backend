/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.Wordnet;
import org.springframework.stereotype.Service;

/**
 *
 * @author ngjin
 */
@Service
public class MatchingServiceImpl implements MatchingService {

    public void runWS4J(String w1, String w2) {
        Wordnet.run(w1, w2);
    }
    
    
}
