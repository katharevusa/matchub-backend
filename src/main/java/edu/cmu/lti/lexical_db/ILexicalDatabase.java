/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.lti.lexical_db;

import java.util.Collection;
import edu.cmu.lti.lexical_db.data.Concept;

/**
 *
 * @author ngjin
 */
public interface ILexicalDatabase {

    Concept getMostFrequentConcept(String word, String pos);

    Collection<Concept> getAllConcepts(String word, String pos);

    Collection<String> getHypernyms(String synset);

    Concept findSynsetBySynset(String synset);

    //to offset. 
    //TODO: synset.toString()?
    String conceptToString(String synset);

    Collection<String> getGloss(Concept synset, String linkString);

}
