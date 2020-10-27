/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.OrganisationEntity;
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
import java.util.Optional;
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
//    public static double calculateSimilarity(String word1, String word2) {
//        
//        System.out.println("START WS4J ALGO *************** - " + word1 + " & " + word2);
//        
//        RelatednessCalculator wup = rcs[3];
//        List<POS[]> posPairs = wup.getPOSPairs();
//
//        double maxScore = -1D;
//        for (POS[] posPair : posPairs) {
//            List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(word1, posPair[0].toString());
//            List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(word2, posPair[1].toString());
//
//            for (Concept ss1 : synsets1) {
//                for (Concept ss2 : synsets2) {
//
//                    Relatedness relatedness = wup.calcRelatednessOfSynset(ss1, ss2);
//                    double score = relatedness.getScore();
//                    if (score > maxScore) {
//                        maxScore = score;
//                    }
//                    String p1 = ss1.getPos().toString();
//                    String p2 = ss2.getPos().toString();
//                }
//            }
//        }
//        if (maxScore == -1D) {
//            maxScore = 0.0;
//        }
//        System.out.println(wup.getClass().getName() + "\t" + maxScore);
//        return maxScore;
//    }

    @Override
    public List<ResourceEntity> recommendResources(Long projectId) throws ProjectNotFoundException {
        //find the project first
        ProjectEntity project = projectService.retrieveProjectById(projectId);
        System.out.println("Found project");

        //Results List
        List<MatchingScore> recommendations = new ArrayList<>();

        //Get the List of String (Project Keywords)
        List<String> projectKeywords = project.getRelatedResources();

        //find the list of available resources 
        List<ResourceEntity> availableResources = resourceEntityRepository.getAllAvailableResources();
        System.out.println("total avail resources: " + availableResources.size());

        Boolean matched = false;

        //loop through all the available resources
        for (int x = 0; x < availableResources.size() && !matched; x++) {
            ResourceEntity resource = availableResources.get(x);
            String resourceKeyword = resource.getResourceName();

            //run ws4j algo for each resource keyword to each project keyword 
            for (int i = 0; i < projectKeywords.size() && !matched; i++) {
                double wupscore = MatchingServiceImpl.calculateWupSimilarity(projectKeywords.get(i), resourceKeyword);
                double pathscore = MatchingServiceImpl.calculatePathSimilarity(projectKeywords.get(i), resourceKeyword);

                //threshold of 70%
                if (wupscore >= 0.70 || (pathscore != 0.0 && pathscore < 0.12)) {
                    matched = true;

                    double score = 0.0;

                    if (wupscore < 0.7) {
                        score = 1 - pathscore;
                    } else {
                        score = wupscore;
                    }

                    //check the Country 
                    if ((resource.getCountry() != null && project.getCountry() != null)
                            && resource.getCountry().equals(project.getCountry())) {
                        score += 1;
                    }
                    recommendations.add(new MatchingScore(score, resource));
                    System.out.println("Added: " + resource.getResourceName());
                }
            }
            matched = false;
        }

        return sortResourceRecommendations(recommendations);

    }

    // *************** preprocess word by word method ***************
//    @Override
//    public List<ResourceEntity> recommendResources(Long projectId) throws ProjectNotFoundException {
//        //find the project first
//        ProjectEntity project = projectService.retrieveProjectById(projectId);
//        System.out.println("Found project");
//
//        //Results List
//        List<MatchingScore> recommendations = new ArrayList<>();
//
//        //Get the preprocessed List of String (Project Keywords)
//        List<String> projectKeywords = preprocessProjectKeywords(project);
//        System.out.print("preprocess project keywords: ");
//        for (String s : projectKeywords) {
//            System.out.print(s + " ");
//        }
//        System.out.println();
//
//        //find the list of available resources 
//        List<ResourceEntity> availableResources = resourceEntityRepository.getAllAvailableResources();
//        System.out.println("total avail resources: " + availableResources.size());
//
//        Boolean matched = false;
//
//        //loop through all the available resources
//        for (int x = 0; x < availableResources.size() && !matched; x++) {
//            ResourceEntity resource = availableResources.get(x);
//
//            //Get the preprocessed List of String (Resource Keywords)
//            List<String> resourceKeywords = preprocessResourceKeywords(resource);
//            System.out.print("preprocess resource keywords: ");
//            for (String s : resourceKeywords) {
//                System.out.print(s + " ");
//            }
//            System.out.println();
//
//            //run ws4j algo word for word for each resource keyword to each project keyword 
//            for (int i = 0; i < projectKeywords.size() && !matched; i++) {
//                for (int j = 0; j < resourceKeywords.size() && !matched; j++) {
//                    double wupscore = MatchingServiceImpl.calculateWupSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
//                    double pathscore = MatchingServiceImpl.calculatePathSimilarity(projectKeywords.get(i), resourceKeywords.get(j));
//
//                    //threshold of 70%
//                    if (wupscore >= 0.70 || (pathscore != 0.0 && pathscore < 0.20)) {
//                        matched = true;
//                        
//                        double score = 0.0;
//                        
//                        if (wupscore < 0.7) {
//                            score = 1 - pathscore;
//                        } else {
//                            score = wupscore;
//                        }
//                        
//                        //check the Country 
//                        if ((resource.getCountry() != null && project.getCountry() != null)
//                                && resource.getCountry().equals(project.getCountry())) {
//                            score += 1;
//                        }
//                        recommendations.add(new MatchingScore(score, resource));
//                        System.out.println("Added: " + resource.getResourceName());
//                    }
//                }
//            }
//            matched = false;
//        }
//
//        return sortResourceRecommendations(recommendations);
//
//    }
    @Override
    public List<ProjectEntity> recommendProjects(Long resourceId) throws ResourceNotFoundException {
        //find the resource first
        ResourceEntity resource = resourceService.getResourceById(resourceId);
        System.out.println("Found resource");

        //Results List
        List<MatchingScore> recommendations = new ArrayList<>();

        //Get the resource name
        String resourceKeyword = resource.getResourceName();

        //find the list of active projects
        List<ProjectEntity> activeProjects = projectEntityRepository.getAllActiveProjects();
        System.out.println("total active projects : " + activeProjects.size());

        Boolean matched = false;

        //loop through all active projects
        for (int x = 0; x < activeProjects.size() && !matched; x++) {
            ProjectEntity project = activeProjects.get(x);

            //get project related resources
            List<String> projectKeywords = project.getRelatedResources();

            //run ws4j algo for each project keyword to each resource keyword 
            for (int i = 0; i < projectKeywords.size() && !matched; i++) {
                double wupscore = MatchingServiceImpl.calculateWupSimilarity(projectKeywords.get(i), resourceKeyword);
                double pathscore = MatchingServiceImpl.calculatePathSimilarity(projectKeywords.get(i), resourceKeyword);

                //threshold of 70%
                if (wupscore >= 0.70 || (pathscore != 0.0 && pathscore < 0.12)) {
                    matched = true;

                    double score = 0.0;

                    if (wupscore < 0.7) {
                        score = 1 - pathscore;
                    } else {
                        score = wupscore;
                    }

                    //check the Country 
                    if ((resource.getCountry() != null && project.getCountry() != null)
                            && resource.getCountry().equals(project.getCountry())) {
                        score += 1;
                    }
                    recommendations.add(new MatchingScore(score, project));
                    System.out.println("Added: " + project.getProjectTitle());
                }
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
    public Page<ProfileEntity> recommendIndividualProfiles(Long accountId, Pageable pageable) {
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        Page<ProfileEntity> recommendations;

        String country = profile.getCountry();
        Set<Long> followingIds = profile.getFollowing();
        List<ProjectEntity> projectsFollowing = profile.getProjectsFollowing();

        if (projectsFollowing.isEmpty()) {
            recommendations = profileEntityRepository.recommendIndividualProfiles(profile.getAccountId(), followingIds, country, pageable);
        } else {
            //get profiles with same country + same project that profile is following 
            recommendations = profileEntityRepository.recommendIndividualProfiles(profile.getAccountId(), followingIds, country, projectsFollowing, pageable);
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
        } else {
            //get profiles with same country + same project that profile is following 
            recommendations = profileEntityRepository.recommendOrganisationProfiles(profile.getAccountId(), followingIds, country, projectsFollowing, pageable);
        }

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
        }

        //return only a list of resources in the sorted order
        if (results.size() > 6) {
            for (int i = 0; i < 6; i++) {
                recommendations.add(results.get(i).getResource());
            }
            System.out.print("Sorted keywords order: ");
            for (ResourceEntity r : recommendations) {
                System.out.print(r.getResourceName() + " ");
            }
            System.out.println();
        }

        return recommendations;
    }

    private List<ProjectEntity> sortProjectRecommendations(List<MatchingScore> results) {

        //Resource Results
        List<ProjectEntity> recommendations = new ArrayList<>();
        System.out.print("original keywords order: ");
        for (MatchingScore ms : results) {
            System.out.print(ms.getProject().getProjectTitle() + ", score: " + ms.getScore() + " ");
        }
        System.out.println();

        //custom sorting based on score 
        Collections.sort(results, new MatchingScoreComparator());

        //return only a list of resources in the sorted order
        if (results.size() > 6) {
            for (int i = 0; i < 6; i++) {
                recommendations.add(results.get(i).getProject());
            }
            System.out.print("Sorted keywords order: ");
            for (ProjectEntity p : recommendations) {
                System.out.print(p.getProjectTitle() + " ");
            }
            System.out.println();
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
