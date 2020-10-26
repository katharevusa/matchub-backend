/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordPartOfSpeech {

    private StanfordCoreNLP pipeline;

    public StanfordPartOfSpeech() {
        Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        this.pipeline = new StanfordCoreNLP(properties);
    }

    public static void testing() {

        Properties properties = new Properties();

        properties.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        String input = "Karma of humans is AI";

        Annotation annotation = pipeline.process(input);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        List<String> output = new ArrayList<>();

        String regex = "([{pos:/NN|NNS|NNP/}])"; //extracting Nouns

        for (CoreMap sentence : sentences) {

            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

            TokenSequencePattern tspattern = TokenSequencePattern.compile(regex);

            TokenSequenceMatcher tsmatcher = tspattern.getMatcher(tokens);

            while (tsmatcher.find()) {

                output.add(tsmatcher.group());

            }

        }

        System.out.println("Input: " + input);

        System.out.println("Output: " + output);

    }

    public List<String> extractNoun(String input) {
        Annotation annotation = pipeline.process(input);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> output = new ArrayList<>();
        String regex = "([{pos:/NN|NNS|NNP/}])"; //extracting Nouns

        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            TokenSequencePattern tspattern = TokenSequencePattern.compile(regex);
            TokenSequenceMatcher tsmatcher = tspattern.getMatcher(tokens);
            while (tsmatcher.find()) {
                output.add(tsmatcher.group());
            }
        }

        System.out.println("Input: " + input);
        System.out.println("Output: " + output);

        return output;
    }

}
