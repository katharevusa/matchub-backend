/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.helper.StanfordLemmatizer;
import com.is4103.matchub.helper.StanfordPartOfSpeech;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.helper.MatchingScore;
import com.is4103.matchub.helper.MatchingScoreComparator;
import com.is4103.matchub.repository.OrganisationEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceCategoryEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author ngjin
 */
@Service
public class MatchingServiceImpl implements MatchingService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private ResourceEntityRepository resourceEntityRepository;

    @Autowired
    private ResourceCategoryEntityRepository resourceCategoryEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private OrganisationEntityRepository organisationEntityRepository;

    private StanfordLemmatizer stanfordLemmatizer = new StanfordLemmatizer();

    private StanfordPartOfSpeech stanfordPartOfSpeech = new StanfordPartOfSpeech();

    private static ILexicalDatabase db = new NictWordNet();

    private static RelatednessCalculator[] rcs = {
        new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
        new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
    };

    public static void runWS4J(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);

        long t0 = System.currentTimeMillis();
        System.out.println("START WS4J ALGO ***************- " + word1 + " & " + word2);

        for (RelatednessCalculator rc : rcs) {
            List<POS[]> posPairs = rc.getPOSPairs();
            double maxScore = -1D;

            for (POS[] posPair : posPairs) {
                List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(word1, posPair[0].toString());
                List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(word2, posPair[1].toString());

                for (Concept synset1 : synsets1) {
                    for (Concept synset2 : synsets2) {
                        Relatedness relatedness = rc.calcRelatednessOfSynset(synset1, synset2);
                        double score = relatedness.getScore();
                        if (score > maxScore) {
                            maxScore = score;
                        }
                    }
                }
            }

            if (maxScore == -1D) {
                maxScore = 0.0;
            }
            System.out.println(rc.getClass().getName() + "\t" + maxScore);
        }

//        for (RelatednessCalculator rc : rcs) {
//            double s = rc.calcRelatednessOfWords(word1, word2);
//            System.out.println(rc.getClass().getName() + "\t" + s);
//        }
        long t1 = System.currentTimeMillis();
        System.out.println("Done in " + (t1 - t0) + " msec.");
    }

    //using WuPalmer
    public static double calculateWupSimilarity(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);

        long t0 = System.currentTimeMillis();
        System.out.println("START WUP Similarity ALGO *************** - " + word1 + " & " + word2);

        List<POS[]> posPairs = rcs[3].getPOSPairs();
        double maxScore = -1D;

        for (POS[] posPair : posPairs) {
            List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(word1, posPair[0].toString());
            List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(word2, posPair[1].toString());

            for (Concept synset1 : synsets1) {
                for (Concept synset2 : synsets2) {
                    Relatedness relatedness = rcs[3].calcRelatednessOfSynset(synset1, synset2);
                    double score = relatedness.getScore();
                    if (score > maxScore) {
                        maxScore = score;
                    }
                }
            }
        }

        if (maxScore == -1D) {
            maxScore = 0.0;
        }

        System.out.println("sim('" + word1 + "', '" + word2 + "') =  " + maxScore);

//        double s = rcs[3].calcRelatednessOfWords(word1, word2);
//        System.out.println(rcs[3].getClass().getName() + "\t" + s);
        long t1 = System.currentTimeMillis();
        System.out.println("Done in " + (t1 - t0) + " msec.");

//        return s;
        return maxScore;
    }

    //using path
    public static double calculatePathSimilarity(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);

        long t0 = System.currentTimeMillis();
        System.out.println("START PATH Similarity ALGO *************** - " + word1 + " & " + word2);

        List<POS[]> posPairs = rcs[7].getPOSPairs();
        double maxScore = -1D;

        for (POS[] posPair : posPairs) {
            List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(word1, posPair[0].toString());
            List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(word2, posPair[1].toString());

            for (Concept synset1 : synsets1) {
                for (Concept synset2 : synsets2) {
                    Relatedness relatedness = rcs[7].calcRelatednessOfSynset(synset1, synset2);
                    double score = relatedness.getScore();
                    if (score > maxScore) {
                        maxScore = score;
                    }
                }
            }
        }

        if (maxScore == -1D) {
            maxScore = 0.0;
        }

        System.out.println("sim('" + word1 + "', '" + word2 + "') =  " + maxScore);

//        double s = rcs[7].calcRelatednessOfWords(word1, word2);
//        System.out.println(rcs[7].getClass().getName() + "\t" + s);
        long t1 = System.currentTimeMillis();
        System.out.println("Done in " + (t1 - t0) + " msec.");

//        return s;
        return maxScore;
    }

    //using res
    public static double calculateResSimilarity(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);

        long t0 = System.currentTimeMillis();
        System.out.println("START RES Similarity ALGO *************** - " + word1 + " & " + word2);

        List<POS[]> posPairs = rcs[4].getPOSPairs();
        double maxScore = -1D;

        for (POS[] posPair : posPairs) {
            List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(word1, posPair[0].toString());
            List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(word2, posPair[1].toString());

            for (Concept synset1 : synsets1) {
                for (Concept synset2 : synsets2) {
                    Relatedness relatedness = rcs[4].calcRelatednessOfSynset(synset1, synset2);
                    double score = relatedness.getScore();
                    if (score > maxScore) {
                        maxScore = score;
                    }
                }
            }
        }

        if (maxScore == -1D) {
            maxScore = 0.0;
        }

        System.out.println("sim('" + word1 + "', '" + word2 + "') =  " + maxScore);

//        double s = rcs[4].calcRelatednessOfWords(word1, word2);
//        System.out.println(rcs[4].getClass().getName() + "\t" + s);
        long t1 = System.currentTimeMillis();
        System.out.println("Done in " + (t1 - t0) + " msec.");

//        return s;
        return maxScore;
    }

    //************ calculates socre for wup, res, oath for the same word sense pair and returns the highest score 
    public static double calculateSimilarity(String word1, String word2) {

        WS4JConfiguration.getInstance().setMFS(true);

        long t0 = System.currentTimeMillis();
//        System.out.println("START WS4J ALGO ***************");
        List<POS[]> posPairs = rcs[3].getPOSPairs();

//        System.out.println("CHECK HERE*****");
//        List<POS[]> posPairs3 = rcs[3].getPOSPairs();
//        System.out.println("PosPair3 length: " + posPairs3.size());
//        List<POS[]> posPairs4 = rcs[4].getPOSPairs();
//        System.out.println("PosPair4 length: " + posPairs4.size());
//        List<POS[]> posPairs7 = rcs[7].getPOSPairs();
//        System.out.println("PosPair7 length: " + posPairs7.size());
        double maxScore = -1D;

        for (POS[] posPair : posPairs) {
            List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(word1, posPair[0].toString());
            List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(word2, posPair[1].toString());

            for (int i = 1; i <= synsets1.size(); i++) {
                for (int j = 1; j <= synsets2.size(); j++) {

                    // wup score for this synset1 of word1 to synset2 of word2
                    double wupScore = rcs[3].calcRelatednessOfSynset(synsets1.get(i - 1), synsets2.get(j - 1)).getScore();

                    // resnik score for this synset1 of word1 to synset2 of word2
                    double resnikScore = rcs[4].calcRelatednessOfSynset(synsets1.get(i - 1), synsets2.get(j - 1)).getScore();

                    // path score for this synset1 of word1 to synset2 of word2
                    double pathScore = rcs[7].calcRelatednessOfSynset(synsets1.get(i - 1), synsets2.get(j - 1)).getScore();

//                    System.out.println("\n" + rcs[3].getClass().getName() + ": " + word1 + "#" + i + " and " + word2 + "#" + j + ": " + wupScore);
//                    System.out.println(rcs[4].getClass().getName() + ": " + word1 + "#" + i + " and " + word2 + "#" + j + ": " + resnikScore);
//                    System.out.println(rcs[7].getClass().getName() + ": " + word1 + "#" + i + " and " + word2 + "#" + j + ": " + pathScore);
                    if (wupScore >= 0.6 && resnikScore >= 4 && pathScore >= (double) 1 / 9) {

                        System.out.println("\n *** Passed threshold check ***");
                        double score = wupScore + resnikScore + pathScore;

                        maxScore = Math.max(maxScore, score);

                        System.out.println(rcs[3].getClass().getName() + ": " + word1 + "#" + i + " and " + word2 + "#" + j + ": " + wupScore);
                        System.out.println(rcs[4].getClass().getName() + ": " + word1 + "#" + i + " and " + word2 + "#" + j + ": " + resnikScore);
                        System.out.println(rcs[7].getClass().getName() + ": " + word1 + "#" + i + " and " + word2 + "#" + j + ": " + pathScore);
                        System.out.println("*** current score from calculate for " + word1 + "#" + i + " and " + word2 + "#" + j + " is: " + score + " ***");
                        System.out.println("*** current max score is: " + maxScore + " ***");
                    }

                }
            }
        }

        if (maxScore == -1D) {
            maxScore = 0.0;
        }

        return maxScore;
    }

    private List<String> lemmatiseAndExtractNoun(String input) {

        //lemmatise the input 
        List<String> lemmatised = stanfordLemmatizer.lemmatize(input);

        String delim = " ";
        String lemmatisedString = String.join(delim, lemmatised);

        //extract nouns 
        List<String> extractNouns = stanfordPartOfSpeech.extractNoun(lemmatisedString);

        return extractNouns;
    }

    @Override
    public List<ResourceEntity> recommendSameCountryResources(Long projectId) throws ProjectNotFoundException {
        //find the project first
        ProjectEntity project = projectService.retrieveProjectById(projectId);
        System.out.println("Found project " + projectId);

        //Results List
        List<MatchingScore> recommendations = new ArrayList<>();

        //Get the List of String (Project Keywords)
        List<String> projectKeywords = project.getRelatedResources();
        //convert the List into a string 
        String delim = " ";
        String projectKeywordsString = String.join(delim, projectKeywords);

        //lemmatise and extract noun of project keywords 
        projectKeywords = lemmatiseAndExtractNoun(projectKeywordsString);

        //find the list of available resources in the same country 
        List<ResourceEntity> availableResources = resourceEntityRepository.getAllAvailableResourcesInCountry(project.getCountry(), project.getStartDate(), project.getEndDate());
        System.out.println("total avail resources in country " + project.getCountry() + ": " + availableResources.size());

        if (availableResources.size() == 0) {
            //return an empty recommendation
            System.out.println("No Resource in the same country");
            return new ArrayList<ResourceEntity>();
        }

        Boolean matched = false;

        for (int x = 0; x < availableResources.size() && !matched; x++) {
            ResourceEntity resource = availableResources.get(x);
            String resourceKeywordString = resource.getResourceName();

            //lemmatise and extract noun of resource 
            List<String> resourceKeywords = lemmatiseAndExtractNoun(resourceKeywordString);

            double previousScore = 0.0;
            //run ws4j algo for each resource keyword to each project keyword 
            for (int i = 0; i < projectKeywords.size(); i++) {
                for (int j = 0; j < resourceKeywords.size(); j++) {

                    // getting best synset score for word1 and word2. as a depiction, dog#1 and animal#2 
                    // will give the highest score we are interested in.
                    double scoreFromCalculate = calculateSimilarity(projectKeywords.get(i), resourceKeywords.get(j));

                    // this gives the highest score for word1 and word2 comparison and the highest synset1 and synset2 comparison.
                    // meaning if dog and animal runs, maybe dog#1 and animal#2 will give the highest. then we store it here since 
                    // these 2 could gave the highest so far. but there could be other keywords which are even better, so we keep
                    // checking all and always replace this to see which words of the keywords array will give the best score and best synset comparison.
                    previousScore = Math.max(previousScore, scoreFromCalculate);
                }
            }

            if (previousScore > 0) {
                recommendations.add(new MatchingScore(previousScore, resource));
                System.out.println("Added: " + resource.getResourceName());
            }
            matched = false;
        }

        return sortResourceRecommendations(recommendations);
    }

    @Override
    public List<ResourceEntity> recommendResources(Long projectId) throws ProjectNotFoundException {
        //find the project first
        ProjectEntity project = projectService.retrieveProjectById(projectId);
        System.out.println("Found project " + projectId);

        //Results List
        List<MatchingScore> recommendations = new ArrayList<>();

        //Get the List of String (Project Keywords)
        List<String> projectKeywords = project.getRelatedResources();
        //convert the List into a string 
        String delim = " ";
        String projectKeywordsString = String.join(delim, projectKeywords);

        //lemmatise and extract noun of project keywords 
        projectKeywords = lemmatiseAndExtractNoun(projectKeywordsString);

        //find the list of available resources 
        List<ResourceEntity> availableResources = resourceEntityRepository.getAllAvailableResourcesNotInCountry(project.getCountry(), project.getStartDate(), project.getEndDate());
        System.out.println("total avail resources: " + availableResources.size());

        Boolean matched = false;

        //loop through all the available resources
        for (int x = 0; x < availableResources.size() && !matched; x++) {
            ResourceEntity resource = availableResources.get(x);
            String resourceKeywordString = resource.getResourceName();

            //lemmatise and extract noun of resource 
            List<String> resourceKeywords = lemmatiseAndExtractNoun(resourceKeywordString);

            double previousScore = 0.0;
            //run ws4j algo for each resource keyword to each project keyword 
            for (int i = 0; i < projectKeywords.size(); i++) {
                for (int j = 0; j < resourceKeywords.size(); j++) {

                    // getting best synset score for word1 and word2. as a depiction, dog#1 and animal#2 
                    // will give the highest score we are interested in.
                    double scoreFromCalculate = calculateSimilarity(projectKeywords.get(i), resourceKeywords.get(j));

                    // this gives the highest score for word1 and word2 comparison and the highest synset1 and synset2 comparison.
                    // meaning if dog and animal runs, maybe dog#1 and animal#2 will give the highest. then we store it here since 
                    // these 2 could gave the highest so far. but there could be other keywords which are even better, so we keep
                    // checking all and always replace this to see which words of the keywords array will give the best score and best synset comparison.
                    previousScore = Math.max(previousScore, scoreFromCalculate);
                }
            }

            if (previousScore > 0) {
                recommendations.add(new MatchingScore(previousScore, resource));
                System.out.println("Added: " + resource.getResourceName());
            }
            matched = false;
        }

        return sortResourceRecommendations(recommendations);
    }

    @Override
    public List<ProjectEntity> recommendSameCountryProjects(Long resourceId) throws ResourceNotFoundException {
        //find the resource first
        ResourceEntity resource = resourceService.getResourceById(resourceId);
        System.out.println("Found resource");

        //Results List
        List<MatchingScore> recommendations = new ArrayList<>();

        //Get the resource name
        List<String> resourceKeywords = lemmatiseAndExtractNoun(resource.getResourceName());

        //find the list of active projects in country 
        if (resource.getCountry() != null) {
            List<ProjectEntity> activeProjects = projectEntityRepository.getAllActiveProjectsInCountry(resource.getCountry());
            System.out.println("total active projects in country (" + resource.getCountry() + "): " + activeProjects.size());

            Boolean matched = false;

            //loop through all active projects
            for (int x = 0; x < activeProjects.size() && !matched; x++) {
                ProjectEntity project = activeProjects.get(x);

                //Get the List of String (Project Keywords)
                List<String> projectKeywords = project.getRelatedResources();
                //convert the List into a string 
                String delim = " ";
                String projectKeywordsString = String.join(delim, projectKeywords);

                //lemmatise and extract noun of project keywords 
                projectKeywords = lemmatiseAndExtractNoun(projectKeywordsString);

                double previousScore = 0.0;
                //run ws4j algo for each resource keyword to each project keyword 
                for (int i = 0; i < projectKeywords.size(); i++) {
                    for (int j = 0; j < resourceKeywords.size(); j++) {

                        // getting best synset score for word1 and word2. as a depiction, dog#1 and animal#2 
                        // will give the highest score we are interested in.
                        double scoreFromCalculate = calculateSimilarity(projectKeywords.get(i), resourceKeywords.get(j));

                        // this gives the highest score for word1 and word2 comparison and the highest synset1 and synset2 comparison.
                        // meaning if dog and animal runs, maybe dog#1 and animal#2 will give the highest. then we store it here since 
                        // these 2 could gave the highest so far. but there could be other keywords which are even better, so we keep
                        // checking all and always replace this to see which words of the keywords array will give the best score and best synset comparison.
                        previousScore = Math.max(previousScore, scoreFromCalculate);
                    }
                }

                if (previousScore > 0) {
                    recommendations.add(new MatchingScore(previousScore, project));
                    System.out.println("Added: " + project.getProjectTitle());
                }
                matched = false;
            }

            return sortProjectRecommendations(recommendations);

        } else {//resource does not have country specified
            System.out.println("Resource does not have country specified");
            return new ArrayList<ProjectEntity>();
        }

    }

    @Override
    public List<ProjectEntity> recommendProjects(Long resourceId) throws ResourceNotFoundException {
        //find the resource first
        ResourceEntity resource = resourceService.getResourceById(resourceId);
        System.out.println("Found resource");

        //Results List
        List<MatchingScore> recommendations = new ArrayList<>();

        //Get the resource name
        List<String> resourceKeywords = lemmatiseAndExtractNoun(resource.getResourceName());

        List<ProjectEntity> activeProjects;

        if (resource.getCountry() != null) {
            activeProjects = projectEntityRepository.getAllActiveProjectsNotInCountry(resource.getCountry());
        } else {
            activeProjects = projectEntityRepository.getAllActiveProjects();
        }

        //find the list of active projects
        System.out.println("total active projects : " + activeProjects.size());

        Boolean matched = false;

        //loop through all active projects
        for (int x = 0; x < activeProjects.size() && !matched; x++) {
            ProjectEntity project = activeProjects.get(x);

            //Get the List of String (Project Keywords)
            List<String> projectKeywords = project.getRelatedResources();
            //convert the List into a string 
            String delim = " ";
            String projectKeywordsString = String.join(delim, projectKeywords);

            //lemmatise and extract noun of project keywords 
            projectKeywords = lemmatiseAndExtractNoun(projectKeywordsString);

            double previousScore = 0.0;
            //run ws4j algo for each resource keyword to each project keyword 
            for (int i = 0; i < projectKeywords.size(); i++) {
                for (int j = 0; j < resourceKeywords.size(); j++) {

                    // getting best synset score for word1 and word2. as a depiction, dog#1 and animal#2 
                    // will give the highest score we are interested in.
                    double scoreFromCalculate = calculateSimilarity(projectKeywords.get(i), resourceKeywords.get(j));

                    // this gives the highest score for word1 and word2 comparison and the highest synset1 and synset2 comparison.
                    // meaning if dog and animal runs, maybe dog#1 and animal#2 will give the highest. then we store it here since 
                    // these 2 could gave the highest so far. but there could be other keywords which are even better, so we keep
                    // checking all and always replace this to see which words of the keywords array will give the best score and best synset comparison.
                    previousScore = Math.max(previousScore, scoreFromCalculate);
                }
            }

            if (previousScore > 0) {
                recommendations.add(new MatchingScore(previousScore, project));
                System.out.println("Added: " + project.getProjectTitle());
            }
            matched = false;
        }

        return sortProjectRecommendations(recommendations);

    }

    @Override
    public Page<ProfileEntity> recommendProfiles(Long accountId, Pageable pageable) {
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        Page<ProfileEntity> recommendations;

        String country = profile.getCountry();
        Set<Long> followingIds = profile.getFollowing();
        List<ProjectEntity> projectsFollowing = profile.getProjectsFollowing();

        if (projectsFollowing.isEmpty()) {
            recommendations = profileEntityRepository.recommendProfiles(profile.getAccountId(), followingIds, country, pageable);

            if (followingIds.size() == 0) {
                recommendations = profileEntityRepository.recommendProfiles(profile.getAccountId(), country, pageable);
            }

        } else {
            //get profiles with same country + same project that profile is following 
            recommendations = profileEntityRepository.recommendProfiles(profile.getAccountId(), followingIds, country, projectsFollowing, pageable);

            if (followingIds.size() == 0) {
                recommendations = profileEntityRepository.recommendProfiles(profile.getAccountId(), country, projectsFollowing, pageable);
            }

        }

        return recommendations;
    }

    public Page<ProfileEntity> recommendIndividualProfiles(Long accountId, Pageable pageable) {
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        Page<ProfileEntity> recommendations;

        String country = profile.getCountry();
        Set<Long> followingIds = profile.getFollowing();
        List<ProjectEntity> projectsFollowing = profile.getProjectsFollowing();

        if (projectsFollowing.isEmpty()) {
            recommendations = profileEntityRepository.recommendIndividualProfiles(profile.getAccountId(), followingIds, country, pageable);

            if (followingIds.size() == 0) {
                recommendations = profileEntityRepository.recommendIndividualProfiles(profile.getAccountId(), country, pageable);
            }

        } else {
            //get profiles with same country + same project that profile is following 
            recommendations = profileEntityRepository.recommendIndividualProfiles(profile.getAccountId(), followingIds, country, projectsFollowing, pageable);

            if (followingIds.size() == 0) {
                recommendations = profileEntityRepository.recommendIndividualProfiles(profile.getAccountId(), country, projectsFollowing, pageable);
            }

        }

        return recommendations;
    }

    @Override
    public Page<ProfileEntity> recommendOrganisationProfiles(Long accountId, Pageable pageable) {
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        Page<ProfileEntity> recommendations;

        String country = profile.getCountry();
        Set<Long> followingIds = profile.getFollowing();
        List<ProjectEntity> projectsFollowing = profile.getProjectsFollowing();

        if (projectsFollowing.isEmpty()) {
            recommendations = profileEntityRepository.recommendOrganisationProfiles(profile.getAccountId(), followingIds, country, pageable);

            if (followingIds.size() == 0) {
                recommendations = profileEntityRepository.recommendOrganisationProfiles(profile.getAccountId(), country, pageable);
            }

        } else {
            //get profiles with same country + same project that profile is following 
            recommendations = profileEntityRepository.recommendOrganisationProfiles(profile.getAccountId(), followingIds, country, projectsFollowing, pageable);

            if (followingIds.size() == 0) {
                recommendations = profileEntityRepository.recommendOrganisationProfiles(profile.getAccountId(), country, projectsFollowing, pageable);
            }

        }

        return recommendations;
    }

    @Override
    public Page<ResourceEntity> recommendSameCountryResourcesAsPageable(Long projectId, Pageable pageable) throws ProjectNotFoundException {
        List<ResourceEntity> resultsList = this.recommendSameCountryResources(projectId);

        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > resultsList.size() ? resultsList.size() : (start + pageable.getPageSize());
        Page<ResourceEntity> recommendations = new PageImpl<ResourceEntity>(resultsList.subList(start.intValue(), end.intValue()), pageable, resultsList.size());

        return recommendations;
    }

    @Override
    public Page<ResourceEntity> recommendResourcesAsPageable(Long projectId, Pageable pageable) throws ProjectNotFoundException {
        List<ResourceEntity> resultsList = this.recommendResources(projectId);

        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > resultsList.size() ? resultsList.size() : (start + pageable.getPageSize());
        Page<ResourceEntity> recommendations = new PageImpl<ResourceEntity>(resultsList.subList(start.intValue(), end.intValue()), pageable, resultsList.size());

        return recommendations;
    }

    @Override
    public Page<ProjectEntity> recommendSameCountryProjectsAsPageable(Long resourceId, Pageable pageable) throws ResourceNotFoundException {
        List<ProjectEntity> resultsList = this.recommendSameCountryProjects(resourceId);

        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > resultsList.size() ? resultsList.size() : (start + pageable.getPageSize());
        Page<ProjectEntity> recommendations = new PageImpl<ProjectEntity>(resultsList.subList(start.intValue(), end.intValue()), pageable, resultsList.size());

        return recommendations;
    }

    @Override
    public Page<ProjectEntity> recommendProjectsAsPageable(Long resourceId, Pageable pageable) throws ResourceNotFoundException {
        List<ProjectEntity> resultsList = this.recommendProjects(resourceId);

        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > resultsList.size() ? resultsList.size() : (start + pageable.getPageSize());
        Page<ProjectEntity> recommendations = new PageImpl<ProjectEntity>(resultsList.subList(start.intValue(), end.intValue()), pageable, resultsList.size());

        return recommendations;
    }

    private List<ResourceEntity> sortResourceRecommendations(List<MatchingScore> results) {

        //Resource Results
        List<ResourceEntity> recommendations = new ArrayList<>();
        System.out.print("original keywords order: ");
        for (MatchingScore ms : results) {
            System.out.print(ms.getResource().getResourceName() + ", score: " + ms.getScore() + " ");
        }
        System.out.println();

        //custom sorting based on score 
        Collections.sort(results, new MatchingScoreComparator());

        if (results.size() == 1) {
            recommendations.add(results.get(0).getResource());
        } else if (results.size() <= 4) {
            for (int i = 0; i < results.size(); i++) {
                recommendations.add(results.get(i).getResource());
            }
        } else {
            for (int i = 0; i < 4; i++) {
                recommendations.add(results.get(i).getResource());
            }
        }

        System.out.println("Sorted recommendations order: ");
        for (ResourceEntity r : recommendations) {
            System.out.println(r.getResourceName() + " ");
        }

        return recommendations;
    }

    private List<ProjectEntity> sortProjectRecommendations(List<MatchingScore> results) {

        //Project Results
        List<ProjectEntity> recommendations = new ArrayList<>();
        System.out.print("original keywords order: ");
        for (MatchingScore ms : results) {
            System.out.print(ms.getProject().getProjectTitle() + ", score: " + ms.getScore() + " ");
        }
        System.out.println();

        //custom sorting based on score 
        Collections.sort(results, new MatchingScoreComparator());

        if (results.size() == 1) {
            recommendations.add(results.get(0).getProject());
        } else if (results.size() <= 4) {
            for (int i = 0; i < results.size(); i++) {
                recommendations.add(results.get(i).getProject());
            }
        } else {
            for (int i = 0; i < 4; i++) {
                recommendations.add(results.get(i).getProject());
            }
        }

        System.out.println("Sorted recommendations order: ");
        for (ProjectEntity p : recommendations) {
            System.out.println(p.getProjectTitle() + " ");
        }

        return recommendations;
    }

    private List<String> preprocessResourceKeywords(ResourceEntity resource) {

        List<String> resourceKeywords = new ArrayList<>();

        //Each Resource: Get the ResourceName & ResourceCategory, concat into a List of String
        String categoryName = resourceCategoryEntityRepository.findById(resource.getResourceCategoryId()).get().getResourceCategoryName();

        resourceKeywords.add(categoryName);

        String[] array = resource.getResourceName().split(" ");
        resourceKeywords.addAll(Arrays.asList(array));

        return resourceKeywords;
    }

    private List<String> preprocessProjectKeywords(ProjectEntity project) {

        List<String> projectKeywords = new ArrayList<>();

        for (String s : project.getRelatedResources()) {
            String[] array = s.split(" ");
            projectKeywords.addAll(Arrays.asList(array));
        }

        return projectKeywords;
    }

}
