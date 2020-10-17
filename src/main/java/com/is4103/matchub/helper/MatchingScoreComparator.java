/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.helper;

import java.util.Comparator;

/**
 *
 * @author ngjin
 */
public class MatchingScoreComparator implements Comparator<MatchingScore> {

    //sort based on descending score order
    public int compare(MatchingScore x, MatchingScore y) {
        int a = (int) (100 * x.score);
        int b = (int) (100 * y.score);

        //descending order
        return b - a;
    }

}
