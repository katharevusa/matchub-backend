///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.is4103.matchub.vo;
//
//import lombok.Data;
//
///**
// *
// * @author longluqian
// */
//
//@Data
//public class ResourceVO {
//    
//     @Id
//    @Column(nullable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long resourceId;
//
//    @Column(nullable = false)
//    @NotNull
//    private String resourceName;
//
//    @Column(nullable = false)
//    @NotNull
//    private String resourceDescription;
//
//    @ElementCollection(fetch = FetchType.EAGER)
//    private Set<String> uploadedFiles = new HashSet<>();
//    
//    @NotNull
//    private boolean available = Boolean.TRUE;
//    
//    @Column(nullable = true, columnDefinition = "TIMESTAMP")
//    private LocalDateTime startTime;
//    
//    @Column(nullable = true, columnDefinition = "TIMESTAMP")
//    private LocalDateTime endTime;
//    
//    @OneToMany(mappedBy = "resource")
//    private List<ResourceRequestEntity> listOfRequests;
//    
//    
//    @Column(nullable = false)
//    @NotNull
//    private Long resourceCategoryId;
//    
//    @Column(nullable = false)
//    @NotNull
//    private Long resourceOwnerId;
//    
//    @NotNull
//    @Column(nullable = false)
//    private Integer units;
//    
//    @OrderColumn
//    @Column(nullable = true)
//    @ElementCollection(fetch = FetchType.LAZY, targetClass = String.class)
//    private List<String> photos = new ArrayList<>();
//    
//    @Column(nullable = false)
//    private Boolean spotlight = Boolean.FALSE;
//    
//    @Column(nullable = true, columnDefinition = "TIMESTAMP")
//    private LocalDateTime spotlightEndTime;
//    
//    @Column(nullable = true)
//    private Long matchedProjectId;
//    
//}
