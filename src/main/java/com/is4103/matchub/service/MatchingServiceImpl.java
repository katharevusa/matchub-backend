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
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
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
        System.out.println("START WS4J ALGO ***************");

        for (RelatednessCalculator rc : rcs) {
            double s = rc.calcRelatednessOfWords(word1, word2);
            System.out.println(rc.getClass().getName() + "\t" + s);
        }

        long t1 = System.currentTimeMillis();
        System.out.println("Done in " + (t1 - t0) + " msec.");
    }

    //using WuPalmer
    public static double calculateWupSimilarity(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);

        long t0 = System.currentTimeMillis();
        System.out.println("START WUP Similarity ALGO *************** - " + word1 + " & " + word2);

        double s = rcs[3].calcRelatednessOfWords(word1, word2);
        System.out.println(rcs[3].getClass().getName() + "\t" + s);

        long t1 = System.currentTimeMillis();
        System.out.println("Done in " + (t1 - t0) + " msec.");

        return s;
    }

    //using path
    public static double calculatePathSimilarity(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);

        long t0 = System.currentTimeMillis();
        System.out.println("START PATH Similarity ALGO *************** - " + word1 + " & " + word2);

        double s = rcs[7].calcRelatednessOfWords(word1, word2);
        System.out.println(rcs[7].getClass().getName() + "\t" + s);

        long t1 = System.currentTimeMillis();
        System.out.println("Done in " + (t1 - t0) + " msec.");

        return s;
    }

    //using res
    public static double calculateResSimilarity(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);

        long t0 = System.currentTimeMillis();
        System.out.println("START RES Similarity ALGO *************** - " + word1 + " & " + word2);

        double s = rcs[4].calcRelatednessOfWords(word1, word2);
        System.out.println(rcs[4].getClass().getName() + "\t" + s);

        long t1 = System.currentTimeMillis();
        System.out.println("Done in " + (t1 - t0) + " msec.");

        return s;
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
        List<ResourceEntity> availableResources = resourceEntityRepository.getAllAvailableResourcesInCountry(project.getCountry());
        System.out.println("total avail resources in country " + project.getCountry() + ": " + availableResources.size());

        Boolean matched = false;

        for (int x = 0; x < availableResources.size() && !matched; x++) {
            ResourceEntity resource = availableResources.get(x);
            String resourceKeywordString = resource.getResourceName();

            //lemmatise and extract noun of resource 
            List<String> resourceKeywords = lemmatiseAndExtractNoun(resourceKeywordString);

            double previousScore = 0.0;
            //run ws4j algo for each resource keyword to each project keyword 
            for (int i = 0; i < projectKeywords.size(); i++) {
                double wupscore = 0.0;
                double pathscore = 0.0;
                double resscore = 0.0;
                for (int j = 0; j < resourceKeywords.size(); j++) {

                    wupscore += MatchingServiceImpl.calculateWupSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
                    pathscore += MatchingServiceImpl.calculatePathSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
                    resscore += MatchingServiceImpl.calculateResSimilarity(projectKeywords.get(i), resourceKeywords.get(j));

                }
                wupscore /= resourceKeywords.size();
                pathscore /= resourceKeywords.size();
                resscore /= resourceKeywords.size();

                if (wupscore >= 0.3 && resscore >= 3 && pathscore >= 0.1) {

                    double score = wupscore + pathscore + resscore;
                    previousScore = Math.max(score, previousScore);

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

        //find the list of available resources in the same country 
        List<ResourceEntity> availableResources = resourceEntityRepository.getAllAvailableResources();
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
                double wupscore = 0.0;
                double pathscore = 0.0;
                double resscore = 0.0;
                for (int j = 0; j < resourceKeywords.size(); j++) {

                    wupscore += MatchingServiceImpl.calculateWupSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
                    pathscore += MatchingServiceImpl.calculatePathSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
                    resscore += MatchingServiceImpl.calculateResSimilarity(projectKeywords.get(i), resourceKeywords.get(j));

                }
                wupscore /= resourceKeywords.size();
                pathscore /= resourceKeywords.size();
                resscore /= resourceKeywords.size();

                if (wupscore >= 0.3 && resscore >= 3 && pathscore >= 0.1) {

                    double score = wupscore + pathscore + resscore;
                    previousScore = Math.max(score, previousScore);

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
                    double wupscore = 0.0;
                    double pathscore = 0.0;
                    double resscore = 0.0;
                    for (int j = 0; j < resourceKeywords.size(); j++) {

                        wupscore += MatchingServiceImpl.calculateWupSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
                        pathscore += MatchingServiceImpl.calculatePathSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
                        resscore += MatchingServiceImpl.calculateResSimilarity(projectKeywords.get(i), resourceKeywords.get(j));

                    }
                    wupscore /= resourceKeywords.size();
                    pathscore /= resourceKeywords.size();
                    resscore /= resourceKeywords.size();

                    if (wupscore >= 0.3 && resscore >= 3 && pathscore >= 0.1) {

                        double score = wupscore + pathscore + resscore;
                        previousScore = Math.max(score, previousScore);

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

        //find the list of active projects
        List<ProjectEntity> activeProjects = projectEntityRepository.getAllActiveProjects();
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
                double wupscore = 0.0;
                double pathscore = 0.0;
                double resscore = 0.0;
                for (int j = 0; j < resourceKeywords.size(); j++) {

                    wupscore += MatchingServiceImpl.calculateWupSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
                    pathscore += MatchingServiceImpl.calculatePathSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
                    resscore += MatchingServiceImpl.calculateResSimilarity(projectKeywords.get(i), resourceKeywords.get(j));

                }
                wupscore /= resourceKeywords.size();
                pathscore /= resourceKeywords.size();
                resscore /= resourceKeywords.size();

                if (wupscore >= 0.3 && resscore >= 3 && pathscore >= 0.1) {

                    double score = wupscore + pathscore + resscore;
                    previousScore = Math.max(score, previousScore);

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
        } else {
            //get profiles with same country + same project that profile is following 
            recommendations = profileEntityRepository.recommendProfiles(profile.getAccountId(), followingIds, country, projectsFollowing, pageable);
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
        } else if (results.size() <= 6) {
            for (int i = 0; i < results.size(); i++) {
                recommendations.add(results.get(i).getResource());
            }
        } else {
            for (int i = 0; i < 6; i++) {
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
        } else if (results.size() <= 6) {
            for (int i = 0; i < results.size(); i++) {
                recommendations.add(results.get(i).getProject());
            }
        } else {
            for (int i = 0; i < 6; i++) {
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
