package com.is4103.matchub.repository;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.ProjectEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface ProjectEntityRepository extends JpaRepository<ProjectEntity, Long> {

    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projectTitle LIKE %?1%",
            countQuery = "SELECT COUNT(pe) FROM ProjectEntity pe WHERE pe.projectTitle LIKE %?1%")
    Page<ProjectEntity> search(String search, Pageable pageable);
    
    @Query(value = " SELECT pe FROM ProjectEntity pe WHERE pe.projCreatorId = :profileId")
    List<ProjectEntity> getCreatedProjectByProfileId(Long profileId);
  
    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projectTitle LIKE %?1% OR pe.projectDescription LIKE %?1% OR pe.country LIKE %?1%",
            countQuery = "SELECT COUNT(pe) FROM ProjectEntity pe WHERE pe.projectTitle LIKE %?1% OR pe.projectDescription LIKE %?1% OR pe.country LIKE %?1%")
    Page<ProjectEntity> searchByKeywords(String search, Pageable pageable);
    
    @Query(value = "SELECT pe FROM ProjectEntity pe WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE ",
            countQuery = "SELECT COUNT(pe) FROM ProjectEntity pe WHERE pe.projStatus = com.is4103.matchub.enumeration.ProjectStatusEnum.ACTIVE")
    Page<ProjectEntity> getLaunchedProjects(Pageable pageable);
    
    
    
    
    
   
}