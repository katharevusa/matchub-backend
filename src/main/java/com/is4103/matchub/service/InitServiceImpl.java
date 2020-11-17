package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.entity.ResourceRequestEntity;
import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import com.is4103.matchub.enumeration.BadgeTypeEnum;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.enumeration.RequestStatusEnum;
import com.is4103.matchub.enumeration.RequestorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.BadgeEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceCategoryEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.ResourceRequestEntityRepository;
import com.is4103.matchub.repository.ReviewEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import com.is4103.matchub.vo.PostVO;
import com.is4103.matchub.vo.ResourceRequestCreateVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class InitServiceImpl implements InitService {

    @Autowired
    AccountEntityRepository accountEntityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ProjectEntityRepository projectEntityRepository;

    @Autowired
    ProjectService projectService;

    @Autowired
    SDGEntityRepository sdgEntityRepository;

    @Autowired
    ResourceCategoryService resourceCategoryService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    ResourceEntityRepository resourceEntityRepository;

    @Autowired
    ProfileEntityRepository profileEntityRepository;

    @Autowired
    ResourceCategoryEntityRepository resourceCategoryEntityRepository;

    @Autowired
    ReviewEntityRepository reviewEntityRepository;

    @Autowired
    BadgeEntityRepository badgeEntityRepository;

    @Autowired
    FirebaseService firebaseService;

    @Autowired
    ResourceRequestEntityRepository resourceRequestEntityRepository;

    @Autowired
    PostService postService;

    @Autowired
    ResourceRequestService resourceRequestService;

    @Transactional
    public void init() {
        // testing:

        initSDG();
        initUsers();
        initResourceCategories();
        initResources();
        initProjects();

        //added for badge and review use case
        initCompletedProjects();

        //community badges
        initCommunityBadges();

        //join request 
        initJoinRequest();

        //init resource requests
        initResourceRequests();

        // init project follower 
        initProjectFollower();

        initPost();

//        firebaseService.getChannelDetails("s");
        // init kanbanboard for project 3
    }

    private void initUsers() {
        if (accountEntityRepository.count() > 0) {
            return;
        }
        Arrays.asList("admin,user1,user2".split(","))
                .forEach(a -> {
                    AccountEntity account;
                    if (a.equalsIgnoreCase("admin")) {
                        account = accountEntityRepository.save(new AccountEntity(a + "@gmail.com", passwordEncoder.encode("password")));
                        account.getRoles().add(ProfileEntity.ROLE_SUPERUSER);
                        account.getRoles().add(ProfileEntity.ROLE_SYSADMIN);
                        account.getRoles().add(ProfileEntity.ROLE_USER);
                    } else if (a.equalsIgnoreCase("user1")) {
                        account = accountEntityRepository.save(new IndividualEntity(a + "@gmail.com", passwordEncoder.encode("password"), "Phil", "Lim", GenderEnum.MALE));
                        account.getRoles().add(ProfileEntity.ROLE_USER);

                        //update the followers list 
                        IndividualEntity user1 = (IndividualEntity) account;
                        user1.setProfilePhoto("https://localhost:8443/api/v1/files/init/nus.jpg");
                        setNotifications(user1);
                        user1.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(4))));
                    } else {
                        account = accountEntityRepository.save(new OrganisationEntity(a + "@gmail.com", passwordEncoder.encode("password"), "NUS", "description", "address"));
                        account.getRoles().add(ProfileEntity.ROLE_USER);

                        //update the followers list 
                        OrganisationEntity user2 = (OrganisationEntity) account;
                        user2.setProfilePhoto("https://localhost:8443/api/v1/files/init/nus.jpg");
                        user2.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(4), Long.valueOf(5), Long.valueOf(6))));

                        setNotifications(user2);
                    }
                    accountEntityRepository.save(account);
                });

        /* INIT 3 INDIVIDUALS */
        IndividualEntity alexLow = new IndividualEntity("alexlow@gmail.com", passwordEncoder.encode("password"), "Alex", "Low", GenderEnum.MALE);
        //account attributes
        alexLow.setUuid(UUID.randomUUID());
        alexLow.setDisabled(Boolean.FALSE);
        alexLow.setIsVerified(Boolean.TRUE);
        alexLow.getRoles().add(ProfileEntity.ROLE_USER);
        alexLow.setJoinDate(LocalDateTime.now());
        //profile & individual attributes
        alexLow.setProfileDescription("Highly Passionate Individual with a love for contributing back to the society!");
        Set<String> skillsets = new HashSet<>(Arrays.asList("Good Communication Skills", "Leadership Skills", "Project Management Skills", "Professional Painter"));
        alexLow.setSkillSet(skillsets);

        alexLow.setCountryCode("+65");
        alexLow.setPhoneNumber("91234567");
        alexLow.setCountry("Singapore");
        alexLow.setCity("Singapore");
        alexLow.setProfilePhoto("https://localhost:8443/api/v1/files/init/alexlow.jpg");
        alexLow.setFollowing(new HashSet<>(Arrays.asList(Long.valueOf(2), Long.valueOf(3))));
        alexLow.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(8), Long.valueOf(9))));

        List<SDGEntity> sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(1)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(3)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(5)));
        alexLow.setSdgs(sdgs);
        setNotifications(alexLow);

        accountEntityRepository.save(alexLow);

        //2nd individual
        IndividualEntity ikjun = new IndividualEntity("ikjun@gmail.com", passwordEncoder.encode("password"), "Ik Jun", "Lee", GenderEnum.MALE);
        //account attributes
        ikjun.setUuid(UUID.randomUUID());
        ikjun.setDisabled(Boolean.FALSE);
        ikjun.setIsVerified(Boolean.TRUE);
        ikjun.getRoles().add(ProfileEntity.ROLE_USER);
        ikjun.setJoinDate(LocalDateTime.now());
        //profile & individual attributes
        ikjun.setProfileDescription("Highly Passionate Individual with a love for contributing back to the society!");
        skillsets = new HashSet<>(Arrays.asList("Singing", "Playing the Guitar", "Trilingual", "General Surgery (GS)", "Verified First Aider"));
        ikjun.setSkillSet(skillsets);

        ikjun.setCountryCode("+82");
        ikjun.setPhoneNumber("011-465-9876");
        ikjun.setCountry("Korea, Republic of South Korea");
        ikjun.setCity("Seoul");
        ikjun.setProfilePhoto("https://localhost:8443/api/v1/files/init/ikjun.jpeg");
        ikjun.setFollowing(new HashSet<>(Arrays.asList(Long.valueOf(3), Long.valueOf(9), Long.valueOf(10), Long.valueOf(11))));
        ikjun.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(8), Long.valueOf(9), Long.valueOf(10), Long.valueOf(11))));

        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(3)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(4)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(5)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(11)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(14)));
        ikjun.setSdgs(sdgs);

        setNotifications(ikjun);

        accountEntityRepository.save(ikjun);

        //3rd individual
        IndividualEntity sophia = new IndividualEntity("sophiasmith@gmail.com", passwordEncoder.encode("password"), "Sophia", "Smith", GenderEnum.FEMALE);
        //account attributes
        sophia.setUuid(UUID.randomUUID());
        sophia.setDisabled(Boolean.FALSE);
        sophia.setIsVerified(Boolean.TRUE);
        sophia.getRoles().add(ProfileEntity.ROLE_USER);
        sophia.setJoinDate(LocalDateTime.now());
        //profile & individual attributes
        sophia.setProfileDescription("Highly Passionate Individual with a love for contributing back to the society!");
        skillsets = new HashSet<>(Arrays.asList("Social Worker for Gender Equality"));
        sophia.setSkillSet(skillsets);

        sophia.setCountryCode("+1");
        sophia.setPhoneNumber("604 598 5235");
        sophia.setCountry("Canada");
        sophia.setCity("Quebec");
        sophia.setProfilePhoto("https://localhost:8443/api/v1/files/init/sophia.jpg");
        sophia.setFollowing(new HashSet<>(Arrays.asList(Long.valueOf(3))));
        sophia.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(7), Long.valueOf(11))));
        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(5)));
        sophia.setSdgs(sdgs);

        setNotifications(sophia);
        accountEntityRepository.save(sophia);

        /* INIT 2 ORGANISATIONS */
        String description = "GENC is a national network of over 150 diverse women leaders from across Canada.";
        OrganisationEntity genc = new OrganisationEntity("genc@gmail.com", passwordEncoder.encode("password"), "Gender Equality Network Canada (GENC)", description, "1920 Yonge St., Suite 302, Toronto, Ontario M4S 3E2");
        //account attributes
        genc.setUuid(UUID.randomUUID());
        genc.setDisabled(Boolean.FALSE);
        genc.setIsVerified(Boolean.TRUE);
        genc.getRoles().add(ProfileEntity.ROLE_USER);
        genc.setJoinDate(LocalDateTime.now());
        //profile & organisation attributes
        Set<String> areasOfExpertise = new HashSet<>(Arrays.asList("Work together to advocate for policy changes", "Inclusive Intersectional Leadership", "Collective Action to Advance Gender Equality"));
        genc.setAreasOfExpertise(areasOfExpertise);
        //set employees
        genc.setEmployees(new HashSet<>(Arrays.asList(sophia.getAccountId(), Long.valueOf(12))));
        //set kah
        genc.setKahs(new HashSet<>(Arrays.asList(sophia.getAccountId())));
        genc.setCountryCode("+1");
        genc.setPhoneNumber("866 293 4483");
        genc.setCountry("Canada");
        genc.setCity("Toronto");
        genc.setProfilePhoto("https://localhost:8443/api/v1/files/init/genc.jpg");
        genc.setFollowing(new HashSet<>(Arrays.asList(sophia.getAccountId(), Long.valueOf(9), Long.valueOf(11))));
        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(5)));
        genc.setSdgs(sdgs);
        setNotifications(genc);
        accountEntityRepository.saveAndFlush(genc);

        //2nd organisation
        description = "Network for Good is a hybrid organization—a nonprofit-owned for-profit.";
        OrganisationEntity networkForGood = new OrganisationEntity("networkforgood@gmail.com", passwordEncoder.encode("password"), "Network For Good", description, "1140 Connecticut Ave NW #700, Washington, DC 20036, United States");
        //account attributes
        networkForGood.setUuid(UUID.randomUUID());
        networkForGood.setDisabled(Boolean.FALSE);
        networkForGood.setIsVerified(Boolean.TRUE);
        networkForGood.getRoles().add(ProfileEntity.ROLE_USER);
        networkForGood.setJoinDate(LocalDateTime.now());
        //profile & organisation attributes
        areasOfExpertise = new HashSet<>(Arrays.asList("Fundraising Platform", "All-In-One Donor Management System", "Fundraising Software"));
        networkForGood.setAreasOfExpertise(areasOfExpertise);
        //set employees
        networkForGood.setEmployees(new HashSet<>(Arrays.asList(alexLow.getAccountId(), Long.valueOf(11))));
        //set KAH
        networkForGood.setKahs(new HashSet<>(Arrays.asList(alexLow.getAccountId())));
        networkForGood.setCountryCode("+1");
        networkForGood.setPhoneNumber("888 284 7978");
        networkForGood.setCountry("United States");
        networkForGood.setCity("Washington");
        networkForGood.setProfilePhoto("https://localhost:8443/api/v1/files/init/networkforgood.png");
        networkForGood.setFollowing(new HashSet<>(Arrays.asList(alexLow.getAccountId(), ikjun.getAccountId(), Long.valueOf(9), Long.valueOf(11))));
        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(1)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(2)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(4)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(8)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(10)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(11)));
        networkForGood.setSdgs(sdgs);
        setNotifications(networkForGood);
        accountEntityRepository.save(networkForGood);

        //4th individual
        IndividualEntity songhwa = new IndividualEntity("songhwa@gmail.com", passwordEncoder.encode("password"), "Song Hwa", "Chae", GenderEnum.FEMALE);
        //account attributes
        songhwa.setUuid(UUID.randomUUID());
        songhwa.setDisabled(Boolean.FALSE);
        songhwa.setIsVerified(Boolean.TRUE);
        songhwa.getRoles().add(ProfileEntity.ROLE_USER);
        songhwa.setJoinDate(LocalDateTime.now());
        //profile & individual attributes
        songhwa.setProfileDescription("Actively taking small steps to Save the earth");
        skillsets = new HashSet<>(Arrays.asList("Hiking", "Nature Lover", "Pet Lover", "Verified First Aider"));
        songhwa.setSkillSet(skillsets);

        songhwa.setCountryCode("+82");
        songhwa.setPhoneNumber("012-456-4321");
        songhwa.setCountry("Korea, Republic of South Korea");
        songhwa.setCity("Seoul");
        songhwa.setProfilePhoto("https://localhost:8443/api/v1/files/init/songhwa.jpg");
        songhwa.setFollowing(new HashSet<>(Arrays.asList(Long.valueOf(4), Long.valueOf(5), Long.valueOf(10))));
        songhwa.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(5), Long.valueOf(7), Long.valueOf(8), Long.valueOf(10))));

        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(6)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(7)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(13)));
        songhwa.setSdgs(sdgs);
        setNotifications(songhwa);
        accountEntityRepository.save(songhwa);

        //3rd organisation, accountId = 10
        description = "The Korean Federation for Environmental Movement (KFEM) is a civic environmental "
                + "organization that takes progressive actions to support core values of life, peace, "
                + "ecology, and bottom-up participation in harmony with the Mother Nature.";
        OrganisationEntity kfem = new OrganisationEntity("kfem@gmail.com", passwordEncoder.encode("password"), "Korean Federation for Environmental Movement", description, "23, Pirundae-ro, Jongno-gu, Seoul, Korea 110-806");
        //account attributes
        kfem.setUuid(UUID.randomUUID());
        kfem.setDisabled(Boolean.FALSE);
        kfem.setIsVerified(Boolean.TRUE);
        kfem.getRoles().add(ProfileEntity.ROLE_USER);
        kfem.setJoinDate(LocalDateTime.now());
        //profile & organisation attributes
        areasOfExpertise = new HashSet<>(Arrays.asList("Nuclear Energy", "Climate Change", "Ocean Protection"));
        kfem.setAreasOfExpertise(areasOfExpertise);
        //set employee
        kfem.setEmployees(new HashSet<>(Arrays.asList(ikjun.getAccountId(), songhwa.getAccountId())));
        //set kah
        kfem.setKahs(new HashSet<>(Arrays.asList(ikjun.getAccountId())));
        kfem.setCountryCode("+82");
        kfem.setPhoneNumber("02-735-7000");
        kfem.setCountry("Korea, Republic of South Korea");
        kfem.setCity("Seoul");
        kfem.setProfilePhoto("https://localhost:8443/api/v1/files/init/kfem.png");
        kfem.setFollowing(new HashSet<>(Arrays.asList(ikjun.getAccountId(), songhwa.getAccountId(), Long.valueOf(11))));
        kfem.setFollowers(new HashSet<>(Arrays.asList(ikjun.getAccountId(), songhwa.getAccountId(), Long.valueOf(11))));
        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(6)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(7)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(11)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(12)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(13)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(14)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(15)));
        kfem.setSdgs(sdgs);
        setNotifications(kfem);
        accountEntityRepository.save(kfem);

        //individual, accountId = 11
        IndividualEntity jeongha = new IndividualEntity("jeongha@gmail.com", passwordEncoder.encode("password"), "Jeong Ha", "Ahn", GenderEnum.FEMALE);
        //account attributes
        jeongha.setUuid(UUID.randomUUID());
        jeongha.setDisabled(Boolean.FALSE);
        jeongha.setIsVerified(Boolean.TRUE);
        jeongha.getRoles().add(ProfileEntity.ROLE_USER);
        jeongha.setJoinDate(LocalDateTime.now());
        //profile & individual attributes
        jeongha.setProfileDescription("Making the world a better place through creativity");
        skillsets = new HashSet<>(Arrays.asList("Painting", "Drawing"));
        jeongha.setSkillSet(skillsets);

        jeongha.setCountryCode("+82");
        jeongha.setPhoneNumber("022-179-4100");
        jeongha.setCountry("Korea, Republic of South Korea");
        jeongha.setCity("Seoul");
        jeongha.setProfilePhoto("https://localhost:8443/api/v1/files/init/jeongha.jpg");
        jeongha.setFollowing(new HashSet<>(Arrays.asList(Long.valueOf(4), Long.valueOf(5), Long.valueOf(10))));
        jeongha.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(5), Long.valueOf(7), Long.valueOf(8), Long.valueOf(10))));

        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(6)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(7)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(13)));
        jeongha.setSdgs(sdgs);
        setNotifications(jeongha);
        accountEntityRepository.save(jeongha);

        //individual, accountId = 12
        IndividualEntity billy = new IndividualEntity("billy@gmail.com", passwordEncoder.encode("password"), "Billy", "Chan", GenderEnum.MALE);
        //account attributes
        billy.setUuid(UUID.randomUUID());
        billy.setDisabled(Boolean.FALSE);
        billy.setIsVerified(Boolean.TRUE);
        billy.getRoles().add(ProfileEntity.ROLE_USER);
        billy.setJoinDate(LocalDateTime.now());
        //profile & individual attributes
        billy.setProfileDescription("Teaching Makes A Difference");
        skillsets = new HashSet<>(Arrays.asList("Teaching", "Economist"));
        billy.setSkillSet(skillsets);

        billy.setCountryCode("+65");
        billy.setPhoneNumber("90004321");
        billy.setCountry("Singapore");
        billy.setCity("Singapore");
        billy.setProfilePhoto("https://localhost:8443/api/v1/files/init/billy.png");
        billy.setFollowing(new HashSet<>(Arrays.asList(Long.valueOf(4), Long.valueOf(5), Long.valueOf(10))));
        billy.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(5), Long.valueOf(7), Long.valueOf(8), Long.valueOf(10))));

        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(1)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(2)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(4)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(5)));
        billy.setSdgs(sdgs);
        setNotifications(billy);
        accountEntityRepository.save(billy);
    }

    private void initSDG() {
        SDGEntity noPoverty = new SDGEntity("No Poverty", "End poverty in all its forms everywhere");
        sdgEntityRepository.save(noPoverty);

        SDGEntity zeroHunger = new SDGEntity("Zero Hunger", "End hunger, achieve food security and improved nutrition and promote sustainable agriculture");
        sdgEntityRepository.save(zeroHunger);

        SDGEntity goodHealth = new SDGEntity("Good Health and Well-being", "Ensure healthy lives and promote well-being for all at all ages");
        sdgEntityRepository.save(goodHealth);

        SDGEntity qualityEducation = new SDGEntity("Quality Education", "Ensure inclusive and equitable quality education and promote lifelong learning opportunities for all");
        sdgEntityRepository.save(qualityEducation);

        SDGEntity genderEquality = new SDGEntity("Gender Equality", "Achieve gender equality and empower all women and girls");
        sdgEntityRepository.save(genderEquality);

        SDGEntity cleanWater = new SDGEntity("Clean Water and Sanitation", "Ensure availability and sustainable management of water and sanitation for all");
        sdgEntityRepository.save(cleanWater);

        SDGEntity cleanEnergy = new SDGEntity("Affordable and Clean Energy", "Ensure access to affordable, reliable, sustainable and modern energy for all");
        sdgEntityRepository.save(cleanEnergy);

        SDGEntity economicGrowth = new SDGEntity("Decent Work and Economic Growth", "Promote sustained, inclusive and sustainable economic growth, full and productive employment and decent work for all");
        sdgEntityRepository.save(economicGrowth);

        SDGEntity industryInnovationInfrastructure = new SDGEntity("Industry, Innovation and Infrastructure", "Build resilient infrastructure, promote inclusive and sustainable industrialization and foster innovation");
        sdgEntityRepository.save(industryInnovationInfrastructure);

        SDGEntity reduceInequalities = new SDGEntity("Reduce Inequalities", "Reduce inequality within and among countries");
        sdgEntityRepository.save(reduceInequalities);

        SDGEntity sustainableCities = new SDGEntity("Sustainable Cities and Communities", "Make cities and human settlements inclusive, safe, resilient and sustainable");
        sdgEntityRepository.save(sustainableCities);

        SDGEntity responsibleConsumption = new SDGEntity("Responsible Consumption and Production", "Ensure sustainable consumption and production patterns");
        sdgEntityRepository.save(responsibleConsumption);

        SDGEntity climateAction = new SDGEntity("Climate Action", "Take urgent action to combat climate change and its impacts");
        sdgEntityRepository.save(climateAction);

        SDGEntity lifeBelowWater = new SDGEntity("Life Below Water", "Conserve and sustainably use the oceans, seas and marine resources for sustainable development");
        sdgEntityRepository.save(lifeBelowWater);

        SDGEntity lifeOnLand = new SDGEntity("Life On Land", "Protect, restore and promote sustainable use of terrestrial ecosystems, sustainably manage forests, combat desertification, and halt and reverse land degradation and halt biodiversity loss");
        sdgEntityRepository.save(lifeOnLand);

        SDGEntity peaceJustice = new SDGEntity("Peace, Justice and Strong Institutions", "Promote peaceful and inclusive societies for sustainable development, provide access to justice for all and build effective, accountable and inclusive institutions at all levels");
        sdgEntityRepository.save(peaceJustice);

        SDGEntity partnerships = new SDGEntity("Partnerships for the Goals", "Strengthen the means of implementation and revitalize the global partnership for sustainable development");
        sdgEntityRepository.save(partnerships);
    }

    public void initResourceCategories() {
        ResourceCategoryEntity foodCategory = new ResourceCategoryEntity("Food", "All Food-related Resources", 5, 1, "kg");
        resourceCategoryService.createResourceCategory(foodCategory);

        ResourceCategoryEntity spaceCategory = new ResourceCategoryEntity("Facilities", "All Resources that are Facilities", 5, 1, "hour");
        resourceCategoryService.createResourceCategory(spaceCategory);

        ResourceCategoryEntity naturalResourceCategory = new ResourceCategoryEntity("Natural", "All Natural Resources", 5, 1, "kg");
        resourceCategoryService.createResourceCategory(naturalResourceCategory);

        ResourceCategoryEntity deviceCategory = new ResourceCategoryEntity("Machinery and Equipment", "All Machinery and Equipment", 10, 1, "set");
        resourceCategoryService.createResourceCategory(deviceCategory);

        ResourceCategoryEntity transportationCategory = new ResourceCategoryEntity("Transportation", "All Transportation Resources", 1, 1, "hour");
        resourceCategoryService.createResourceCategory(transportationCategory);

        ResourceCategoryEntity educationCategory = new ResourceCategoryEntity("Education and Training", "All Education Resources", 5, 1, "set");
        resourceCategoryService.createResourceCategory(educationCategory);

        ResourceCategoryEntity clothCategory = new ResourceCategoryEntity("Clothes", "All clothes Resources", 5, 1, "piece");
        resourceCategoryService.createResourceCategory(clothCategory);

        ResourceCategoryEntity ipCategory = new ResourceCategoryEntity("Intellectual Property", "All IP-related, non-physical Resources (Trademarks, Patents, Copyrights)", 15, 1, "property");
        resourceCategoryService.createResourceCategory(ipCategory);

        ResourceCategoryEntity consumerGoodsCategory = new ResourceCategoryEntity("Consumer Goods", "All Consumer Goods", 15, 1, "unit");
        resourceCategoryService.createResourceCategory(consumerGoodsCategory);

    }

    public void initResources() {
//1
        ResourceEntity bread = new ResourceEntity("Bread", "Food Free Bread", LocalDateTime.parse("2021-06-05T11:50:55"), LocalDateTime.parse("2021-07-05T11:50:55"), 10);
        bread.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_bread.jpg");
        bread.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_bread.jpg");
        bread.getPhotos().add("https://localhost:8443/api/v1/files/init/bread2.jpg");
        bread.setCountry("Singapore");

        //spotlight resource1
        bread.setSpotlight(true);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusDays(1);
        bread.setSpotlightEndTime(endTime);
        resourceService.createResource(bread, 1L, 5L);// category id, profileId
//2
        ResourceEntity classroom = new ResourceEntity("Classroom", "Free classroom", LocalDateTime.parse("2021-06-05T11:50:55"), LocalDateTime.parse("2021-07-05T11:50:55"), 10);
        classroom.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_classroom.jpg");
        classroom.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_classroom.jpg");
        classroom.getPhotos().add("https://localhost:8443/api/v1/files/init/classroom.jpg");
        classroom.setCountry("Cambodia");

        //spotlight resource2
        classroom.setSpotlight(true);
        now = LocalDateTime.now();
        endTime = now.plusDays(1);
        classroom.setSpotlightEndTime(endTime);
        resourceService.createResource(classroom, 2L, 5L);
//3
        ResourceEntity water = new ResourceEntity("Water", "10 Free Bottle Water", LocalDateTime.parse("2021-06-05T11:50:55"), LocalDateTime.parse("2021-07-05T11:50:55"), 10);
        water.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_water.jpg");
        water.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_water.jpg");
        water.getPhotos().add("https://localhost:8443/api/v1/files/init/water1.jpg");
        water.setCountry("Cambodia");
        resourceService.createResource(water, 3L, 5L);
//4
        ResourceEntity laptop = new ResourceEntity("Laptop", "10 Laptop for free rent", LocalDateTime.parse("2021-06-05T11:50:55"), LocalDateTime.parse("2021-07-05T11:50:55"), 10);
        laptop.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_laptop.jpg");
        laptop.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_laptop.jpg");
        laptop.getPhotos().add("https://localhost:8443/api/v1/files/init/laptop.jpg");
        laptop.setCountry("Bangladesh");
        resourceService.createResource(laptop, 4L, 7L);
//5
        ResourceEntity bus = new ResourceEntity("Bus", "1 BUS free for rent for 1 day ", LocalDateTime.parse("2021-09-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10);
        bus.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_bus.jpg");
        bus.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_bus.jpg");
        bus.getPhotos().add("https://localhost:8443/api/v1/files/init/bus.jpg");
        bus.setCountry("Singapore");
        resourceService.createResource(bus, 5L, 8L);
        //6      
        ResourceEntity lamp = new ResourceEntity("Lamp", "Would like to donate 100 lamps for free to help the needy ", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 100);
        lamp.setResourceProfilePic("https://localhost:8443/api/v1/files/init/lampResource.jpg");
        lamp.getPhotos().add("https://localhost:8443/api/v1/files/init/lampResource.jpg");
        lamp.setCountry("Peru");
        resourceService.createResource(lamp, 4L, 9L);
        //7     
//        ResourceEntity turtleFood = new ResourceEntity("Turtle Food", "Some free turtle food for free donation ", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 150);
//        turtleFood.setResourceProfilePic("https://localhost:8443/api/v1/files/init/turtleFood.jpg");
//        turtleFood.getPhotos().add("https://localhost:8443/api/v1/files/init/turtleFood.jpg");
//        turtleFood.setCountry("Panama");
//        resourceService.createResource(turtleFood, 1L, 9L);

        //8      
        ResourceEntity clothes = new ResourceEntity("Clothes", "Some free clothes donation ", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 150);
        clothes.setResourceProfilePic("https://localhost:8443/api/v1/files/init/clothes.jpg");
        clothes.getPhotos().add("https://localhost:8443/api/v1/files/init/clothes.jpg");
        clothes.setCountry("Australia");
        resourceService.createResource(clothes, 7L, 9L);

        //9   
        ResourceEntity dictionary = new ResourceEntity("English Language Dictionaries", "100 used english dictionaries", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 100);
        dictionary.setResourceProfilePic("https://localhost:8443/api/v1/files/init/dictionary.jpg");
        dictionary.getPhotos().add("https://localhost:8443/api/v1/files/init/dictionary.jpg");
        dictionary.setCountry("Cambodia");
        resourceService.createResource(dictionary, 6L, 4L);

        //10
        ResourceEntity charger = new ResourceEntity("Charger", "50 Available Laptop Chargers", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 50);
        charger.setResourceProfilePic("https://localhost:8443/api/v1/files/init/charger.jpg");
        charger.getPhotos().add("https://localhost:8443/api/v1/files/init/charger.jpg");
        charger.setCountry("Bangladesh");
        resourceService.createResource(charger, 4L, 9L);

        //11
        ResourceEntity cereal = new ResourceEntity("Cereal", "Healthy cereal for donation", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10);
        cereal.setResourceProfilePic("https://localhost:8443/api/v1/files/init/cereal.jpg");
        cereal.getPhotos().add("https://localhost:8443/api/v1/files/init/cereal.jpg");
        cereal.setCountry("Malawi");
        resourceService.createResource(cereal, 1L, 7L);

        //12
        ResourceEntity apple = new ResourceEntity("Apples", "150kg of fresh red Aaples", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 150);
        apple.setResourceProfilePic("https://localhost:8443/api/v1/files/init/apples.jpg");
        apple.getPhotos().add("https://localhost:8443/api/v1/files/init/apples.jpg");
        apple.setCountry("Malawi");
        resourceService.createResource(apple, 1L, 7L);

        //13
        ResourceEntity bedframe = new ResourceEntity("Donating Sturdy Used Bedframes", "50 bedframes", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 50);
        bedframe.setResourceProfilePic("https://localhost:8443/api/v1/files/init/bedframe.jpg");
        bedframe.getPhotos().add("https://localhost:8443/api/v1/files/init/bedframe.jpg");
        bedframe.setCountry("Cambodia");
        resourceService.createResource(bedframe, 2L, 4L);

        //14
        ResourceEntity door = new ResourceEntity("Offering Marble Doors", "50 Marble doors with doorknots", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 50);
        door.setResourceProfilePic("https://localhost:8443/api/v1/files/init/door.jpg");
        door.getPhotos().add("https://localhost:8443/api/v1/files/init/door.jpg");
        door.setCountry("Cambodia");
        resourceService.createResource(door, 2L, 4L);

        //15
        ResourceEntity detergent = new ResourceEntity("Giving away detergent", "75 bottles of detergent (1kg per bottle) safe for use", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 75);
        detergent.setResourceProfilePic("https://localhost:8443/api/v1/files/init/detergent.jpg");
        detergent.getPhotos().add("https://localhost:8443/api/v1/files/init/detergent.jpg");
        detergent.setCountry("Pakistan");
        resourceService.createResource(detergent, 3L, 6L);

        //16
        ResourceEntity dustbin = new ResourceEntity("Donating new dustbins", "60 new dustbins", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 60);
        dustbin.setResourceProfilePic("https://localhost:8443/api/v1/files/init/dustbin.jpg");
        dustbin.getPhotos().add("https://localhost:8443/api/v1/files/init/dustbin.jpg");
        dustbin.setCountry("Indonesia");
        resourceService.createResource(dustbin, 3L, 6L);

        //17
        ResourceEntity torchlight = new ResourceEntity("Giving away used torchlights", "25 torchlight with batteries", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 25);
        torchlight.setResourceProfilePic("https://localhost:8443/api/v1/files/init/torchlight.jpg");
        torchlight.getPhotos().add("https://localhost:8443/api/v1/files/init/torchlight.jpg");
        torchlight.setCountry("Peru");
        resourceService.createResource(torchlight, 4L, 10L);

        //18
        ResourceEntity pacifier = new ResourceEntity("Donating new baby pacifier", "200 brand new pacifiers", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 200);
        pacifier.setResourceProfilePic("https://localhost:8443/api/v1/files/init/pacifier.jpg");
        pacifier.getPhotos().add("https://localhost:8443/api/v1/files/init/pacifier.jpg");
        pacifier.setCountry("Indonesia");
        resourceService.createResource(pacifier, 4L, 5L);

        //19
        ResourceEntity vege = new ResourceEntity("Offering fresh green vegetables", "10kg of fresh vegetables that are self harvested", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10);
        vege.setResourceProfilePic("https://localhost:8443/api/v1/files/init/vegetable.jpg");
        vege.getPhotos().add("https://localhost:8443/api/v1/files/init/vegetable.jpg");
        vege.setCountry("Kenya");
        resourceService.createResource(vege, 1L, 6L);

        //20
        ResourceEntity glasses = new ResourceEntity("Donating Eyeglasses", "240 used glasses frame", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 240);
        glasses.setResourceProfilePic("https://localhost:8443/api/v1/files/init/glasses.jpg");
        glasses.getPhotos().add("https://localhost:8443/api/v1/files/init/glasses.jpg");
        glasses.setCountry("Australia");
        resourceService.createResource(glasses, 4L, 11L);

        //21
        ResourceEntity toiletpaper = new ResourceEntity("Giving away toilet paper", "200kg of toilet paper", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 200);
        toiletpaper.setResourceProfilePic("https://localhost:8443/api/v1/files/init/toiletpaper.jpg");
        toiletpaper.getPhotos().add("https://localhost:8443/api/v1/files/init/toiletpaper.jpg");
        toiletpaper.setCountry("Nepal");
        resourceService.createResource(toiletpaper, 3L, 5L);

        ResourceEntity pears = new ResourceEntity("Pears", "20kg of fresh pears", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 20);
        pears.setResourceProfilePic("https://localhost:8443/api/v1/files/init/pears.jpg");
        pears.getPhotos().add("https://localhost:8443/api/v1/files/init/pears.jpg");
        pears.setCountry("Malawi");
        resourceService.createResource(pears, 1L, 10L);

        ResourceEntity grapes = new ResourceEntity("Grapes", "20kg of fresh grapes", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 20);
        grapes.setResourceProfilePic("https://localhost:8443/api/v1/files/init/grapes.jpg");
        grapes.getPhotos().add("https://localhost:8443/api/v1/files/init/grapes.jpg");
        grapes.setCountry("Singapore");
        resourceService.createResource(grapes, 1L, 10L);

        ResourceEntity paintDye = new ResourceEntity("Paint Dye", "10kg of NIPPON colour dye of different colours each (White, whitewash, light blue, black, yellow)", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10);
        paintDye.setResourceProfilePic("https://localhost:8443/api/v1/files/init/paintdye.jpg");
        paintDye.getPhotos().add("https://localhost:8443/api/v1/files/init/paintdye.jpg");
        paintDye.setCountry("Malaysia");
        resourceService.createResource(paintDye, 4L, 4L);

        ResourceEntity bodyWash = new ResourceEntity("Body Wash", "100 bottles of body wash", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 100);
        bodyWash.setResourceProfilePic("https://localhost:8443/api/v1/files/init/bodywash.png");
        bodyWash.getPhotos().add("https://localhost:8443/api/v1/files/init/bodywash.png");
        bodyWash.setCountry("Malaysia");
        resourceService.createResource(bodyWash, 9L, 6L);

        ResourceEntity garbageBag = new ResourceEntity("Garbage Bags", "10,000 new garbage bags", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10000);
        garbageBag.setResourceProfilePic("https://localhost:8443/api/v1/files/init/garbageBag.jpg");
        garbageBag.getPhotos().add("https://localhost:8443/api/v1/files/init/garbageBag.jpg");
        garbageBag.setCountry("Thailand");
        resourceService.createResource(garbageBag, 4L, 7L);

        ResourceEntity headphone = new ResourceEntity("Headphones", "250 brand new SONY headphones", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 200);
        headphone.setResourceProfilePic("https://localhost:8443/api/v1/files/init/headphone.jpeg");
        headphone.getPhotos().add("https://localhost:8443/api/v1/files/init/headphone.jpeg");
        headphone.setCountry("Singapore");
        resourceService.createResource(headphone, 4L, 12L);

        ResourceEntity computerMouse = new ResourceEntity("Computer Mouse", "170 used wired computer mouse, can be easily connected with computer/laptops via USB", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 170);
        computerMouse.setResourceProfilePic("https://localhost:8443/api/v1/files/init/computerMouse.jpg");
        computerMouse.getPhotos().add("https://localhost:8443/api/v1/files/init/computerMouse.jpg");
        computerMouse.setCountry("Singapore");
        resourceService.createResource(computerMouse, 4L, 12L);

        ResourceEntity keyboard = new ResourceEntity("Keyboard", "188 used Keyboards, can be easily connected with computer/laptops via USB or Bluetooth", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 188);
        keyboard.setResourceProfilePic("https://localhost:8443/api/v1/files/init/keyboard.jpg");
        keyboard.getPhotos().add("https://localhost:8443/api/v1/files/init/keyboard.jpg");
        keyboard.setCountry("Singapore");
        resourceService.createResource(keyboard, 4L, 12L);

        ResourceEntity floorPlan = new ResourceEntity("Floor Plans", "Floors Plans available, contact me for more details", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 28);
        floorPlan.setResourceProfilePic("https://localhost:8443/api/v1/files/init/floorplan.png");
        floorPlan.getPhotos().add("https://localhost:8443/api/v1/files/init/floorplan.png");
        floorPlan.setCountry("Cambodia");
        resourceService.createResource(floorPlan, 8L, 10L);

        ResourceEntity shovel = new ResourceEntity("Shovel", "Shovel available, contact me for more details", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 28);
        shovel.setResourceProfilePic("https://localhost:8443/api/v1/files/init/shovel.jpg");
        shovel.getPhotos().add("https://localhost:8443/api/v1/files/init/shovel.jpg");
        shovel.setCountry("Cambodia");
        resourceService.createResource(shovel, 4L, 10L);

        ResourceEntity spade = new ResourceEntity("Gardening Spade", "Gardening Spade available", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 28);
        spade.setResourceProfilePic("https://localhost:8443/api/v1/files/init/spade.jpg");
        spade.getPhotos().add("https://localhost:8443/api/v1/files/init/spade.jpg");
        spade.setCountry("Singapore");
        resourceService.createResource(spade, 4L, 10L);

        ResourceEntity sofa = new ResourceEntity("Sofa", "5 sets of used sofa available for donation", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 1000);
        sofa.setResourceProfilePic("https://localhost:8443/api/v1/files/init/sofa.jpg");
        sofa.getPhotos().add("https://localhost:8443/api/v1/files/init/sofa.jpg");
        sofa.setCountry("Singapore");
        resourceService.createResource(sofa, 9L, 7L);

        ResourceEntity chair = new ResourceEntity("Chair", "500 Brand New Chairs", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 500);
        chair.setResourceProfilePic("https://localhost:8443/api/v1/files/init/chair.jpg");
        chair.getPhotos().add("https://localhost:8443/api/v1/files/init/chair.jpg");
        chair.setCountry("Singapore");
        resourceService.createResource(chair, 9L, 7L);

        //22
        //*********** for rep points testing 
//        ResourceEntity testing = new ResourceEntity("testing", "testing", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 200);
//        testing.setResourceProfilePic("https://img.jakpost.net/c/2020/09/08/2020_09_08_103888_1599536848._large.jpg");
//        testing.getPhotos().add("https://img.jakpost.net/c/2020/09/08/2020_09_08_103888_1599536848._large.jpg");
//        testing.setCountry("Korea, Republic of South Korea");
//        testing.setAvailable(false);
//        testing.setMatchedProjectId(Long.valueOf(14));
//        resourceService.createResource(testing, 3L, 5L);
    }

    public void initProjects() {

        SDGEntity poverty = sdgEntityRepository.findBySdgName("No Poverty");
        SDGEntity zeroHunger = sdgEntityRepository.findBySdgName("Zero Hunger");
        SDGEntity goodHealth = sdgEntityRepository.findBySdgName("Good Health and Well-being");
        SDGEntity qualityEducation = sdgEntityRepository.findBySdgName("Quality Education");
        SDGEntity genderEquality = sdgEntityRepository.findBySdgName("Gender Equality");
        SDGEntity cleanWater = sdgEntityRepository.findBySdgName("Clean Water and Sanitation");
        SDGEntity sustainableCities = sdgEntityRepository.findBySdgName("Sustainable Cities and Communities");
        SDGEntity responsibleConsumption = sdgEntityRepository.findBySdgName("Responsible Consumption and Production");
        SDGEntity climateAction = sdgEntityRepository.findBySdgName("Climate Action");

        ProjectEntity projectEntity1 = new ProjectEntity("Empowering Communities in Bangladesh", "To strengthen community resilience to prepare and respond to the risks associated with disasters and climate change by fostering economic empowerment, inclusive leadership and disaster preparedness. We would thus require electronic devices for the individuals in order to be connected globally through the internet.", "Bangladesh", LocalDateTime.now(), LocalDateTime.parse("2021-05-05T11:50:55"));
        projectEntity1.getSdgs().add(poverty);
        projectEntity1.getSdgs().add(zeroHunger);
        projectEntity1.setUpvotes(21);

        projectEntity1.setProjectPoolPoints(121);

        //spotlight project1
        projectEntity1.setSpotlight(true);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusDays(1);
        projectEntity1.setSpotlightEndTime(endTime);

        //relatedResources
        List<String> relatedResources = new ArrayList<>();
        relatedResources.add("Phone");
        relatedResources.add("Tablet");
        relatedResources.add("Device");
        relatedResources.add("Computer");
        projectEntity1.setRelatedResources(relatedResources);

        projectEntity1.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity1.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project1.jpg");
        projectEntity1.getPhotos().add("https://localhost:8443/api/v1/files/init/project1.jpg");
        projectEntity1.getPhotos().add("https://localhost:8443/api/v1/files/init/Bangladesh.jpg");
        projectEntity1.getPhotos().add("https://localhost:8443/api/v1/files/init/Bangladesh1.jpg");
        projectEntity1.getPhotos().add("https://localhost:8443/api/v1/files/init/Bangladesh2.jpg");
        projectService.createProject(projectEntity1, 5L);
        /* create project badge */
        BadgeEntity projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Empowerment in Bangladesh", "https://localhost:8443/api/v1/files/badgeIcons/cities.png");
        projBadge.setProject(projectEntity1);
        badgeEntityRepository.save(projBadge);

        projectEntity1.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity1);
        /* end of project badge */

 /* add team member into this project */
        ProfileEntity songhwa = profileEntityRepository.findById(Long.valueOf(9)).get();
        projectEntity1.getTeamMembers().add(songhwa);
        songhwa.getProjectsJoined().add(projectEntity1);

        profileEntityRepository.save(songhwa);
        projectEntityRepository.save(projectEntity1);
        /* end of add team member */

        ProjectEntity projectEntity2 = new ProjectEntity("Women's financial literacy, Malawi", "CARE will work with 20,000 women from 1,000 village savings and loans groups in Lilongwe, Dowa and Kasungu Districts, to overcome chronic hunger by expanding their farms or micro-businesses. We hope to receive donations of various nutritious food like fruits.", "Malawi", LocalDateTime.parse("2019-03-05T11:50:55"), LocalDateTime.parse("2021-06-05T11:50:55"));
        projectEntity2.getSdgs().add(genderEquality);
        projectEntity2.getSdgs().add(qualityEducation);
        projectEntity2.setUpvotes(23);

        projectEntity2.setProjectPoolPoints(123);

        //spotlight project2
        projectEntity2.setSpotlight(true);
        now = LocalDateTime.now();
        endTime = now.plusDays(1);
        projectEntity2.setSpotlightEndTime(endTime);

        //relatedResources
        relatedResources = new ArrayList<>();
        relatedResources.add("Fruits");
        projectEntity2.setRelatedResources(relatedResources);

        projectEntity2.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity2.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project2.jpg");
        projectEntity2.getPhotos().add("https://localhost:8443/api/v1/files/init/project2.jpg");
        projectEntity2.getPhotos().add("https://localhost:8443/api/v1/files/init/woman1.jpg");
        projectEntity2.getPhotos().add("https://localhost:8443/api/v1/files/init/woman2.jpg");
        projectEntity2.getPhotos().add("https://localhost:8443/api/v1/files/init/woman3.jpg");
        projectService.createProject(projectEntity2, 5L);
        /* create project badge */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Fiancial Literacy Achieved", "https://localhost:8443/api/v1/files/badgeIcons/cities.png");
        projBadge.setProject(projectEntity2);
        badgeEntityRepository.save(projBadge);

        projectEntity2.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity2);
        /* end of project badge */

        ProjectEntity projectEntity3 = new ProjectEntity("Supporting rural families, Cambodia", "To support rural families. The young children require a condusive environment to study and learn, with basic tables and chairs.", "Cambodia", LocalDateTime.parse("2019-03-05T11:50:55"), LocalDateTime.parse("2021-06-05T11:50:55"));
        projectEntity3.getSdgs().add(poverty);
        projectEntity3.getSdgs().add(zeroHunger);
        projectEntity3.setUpvotes(25);

        projectEntity3.setProjectPoolPoints(125);

        //relatedResources
        relatedResources = new ArrayList<>();
        relatedResources.add("Water");
        relatedResources.add("Library");
        projectEntity3.setRelatedResources(relatedResources);

        projectEntity3.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity3.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project3.jpg");
        projectEntity3.getPhotos().add("https://localhost:8443/api/v1/files/init/project3.jpg");
        projectEntity3.getPhotos().add("https://localhost:8443/api/v1/files/init/rural.jpg");
        projectEntity3.getPhotos().add("https://localhost:8443/api/v1/files/init/rural2.jpg");
        projectService.createProject(projectEntity3, 9L);
        /* create project badge */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Support For Cambodia", "https://localhost:8443/api/v1/files/badgeIcons/construction.png");
        projBadge.setProject(projectEntity3);
        badgeEntityRepository.save(projBadge);

        projectEntity3.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity3);
        /* end of project badge */

        ProjectEntity projectEntity4 = new ProjectEntity("Building housing in Phnom Penh, Cambodia", "As an alternative to Schoolies, 18 Mosman High year 12 students are travelling to Cambodia to build houses for local Cambodians living in poverty. This project would thus require basic necessities such as toiletries. It would also be helpful if there are existing floorplans available as reference for the buildings construction.", "Cambodia", LocalDateTime.now(), LocalDateTime.parse("2021-06-05T11:50:55"));
        projectEntity4.getSdgs().add(genderEquality);
        projectEntity4.getSdgs().add(qualityEducation);
        projectEntity4.getSdgs().add(goodHealth);
        projectEntity4.setUpvotes(23);

        projectEntity4.setProjectPoolPoints(123);

        //relatedResources
        relatedResources = new ArrayList<>();
        relatedResources.add("Layout Architect");
        relatedResources.add("Shampoo");
        relatedResources.add("Paint");
        projectEntity4.setRelatedResources(relatedResources);

        projectEntity4.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity4.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project4.jpg");
        projectEntity4.getPhotos().add("https://localhost:8443/api/v1/files/init/project4.jpg");
        projectEntity4.getPhotos().add("https://localhost:8443/api/v1/files/init/building.jpg");
        projectEntity4.getPhotos().add("https://localhost:8443/api/v1/files/init/building2.jpg");
        projectEntity4.getPhotos().add("https://localhost:8443/api/v1/files/init/building3.jpg");

        projectService.createProject(projectEntity4, 9L);
        /* create project badge */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "House Builder", "https://localhost:8443/api/v1/files/badgeIcons/construction.png");
        projBadge.setProject(projectEntity4);
        badgeEntityRepository.save(projBadge);

        projectEntity4.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity4);
        /* end of project badge */

        ProjectEntity projectEntity5 = new ProjectEntity("Promote inclusive access to water, sanitation and hygiene in Pakistan", "The project aims to support improvement in the delivery of more inclusive, equitable and sustainable access to water, sanitation and hygiene (WASH) services. In order to achieve good hygiene, it would be helpful to receive donations of sanitizers or related resources. ", "Pakistan", LocalDateTime.parse("2020-12-05T11:50:55"), LocalDateTime.parse("2021-03-05T11:50:55"));
        projectEntity5.getSdgs().add(cleanWater);
        projectEntity5.getSdgs().add(goodHealth);
        projectEntity5.setUpvotes(25);

        projectEntity5.setProjectPoolPoints(125);

        //relatedResources
        relatedResources = new ArrayList<>();
        relatedResources.add("Soap");
        projectEntity5.setRelatedResources(relatedResources);

        projectEntity5.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity5.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project5.png");
        projectEntity5.getPhotos().add("https://localhost:8443/api/v1/files/init/project5.png");
        projectEntity5.getPhotos().add("https://localhost:8443/api/v1/files/init/water.jpg");
        projectEntity5.getPhotos().add("https://localhost:8443/api/v1/files/init/water2.jpg");
        projectEntity5.getPhotos().add("https://localhost:8443/api/v1/files/init/water3.jpg");

        projectService.createProject(projectEntity5, 9L);
        /* create project badge */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Papua New Guinea Reformed", "https://localhost:8443/api/v1/files/badgeIcons/environment.png");
        projBadge.setProject(projectEntity5);
        badgeEntityRepository.save(projBadge);

        projectEntity5.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity5);
        /* end of project badge */

//        ProjectEntity projectEntity6 = new ProjectEntity("Save endangered sea turtles in Panama", "This project will launch a sea turtle research and conservation program to protect endangered leatherback and hawksbill turtles that were found at Bocas del Drago, Panama.", "Panama", LocalDateTime.parse("2021-01-05T11:50:55"), LocalDateTime.parse("2025-06-05T11:50:55"));
//        projectEntity6.getSdgs().add(climateAction);
//        projectEntity6.getSdgs().add(sustainableCities);
//        projectEntity6.setUpvotes(50);
//
//        projectEntity6.setProjectPoolPoints(150);
//
//        //relatedResources
//        relatedResources = new ArrayList<>();
//        relatedResources.add("Turtle Food");
//        projectEntity6.setRelatedResources(relatedResources);
//
//        projectEntity6.setProjStatus(ProjectStatusEnum.ACTIVE);
//        projectEntity6.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project6.jpg");
//        projectEntity6.getPhotos().add("https://localhost:8443/api/v1/files/init/project6.jpg");
//        projectEntity6.getPhotos().add("https://localhost:8443/api/v1/files/init/turtles.jpg");
//        projectEntity6.getPhotos().add("https://localhost:8443/api/v1/files/init/turtles2.jpg");
//        projectService.createProject(projectEntity6, 5L);
//
//        /* create project badge */
//        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Sea Turtle Saver", "https://localhost:8443/api/v1/files/badgeIcons/animal.png");
//        projBadge.setProject(projectEntity6);
//        badgeEntityRepository.save(projBadge);
//
//        projectEntity6.setProjectBadge(projBadge);
//        projectEntityRepository.save(projectEntity6);
//        /* end of project badge */
//
// /* add team member into this project */
//        ProfileEntity songhwa = profileEntityRepository.findById(Long.valueOf(9)).get();
//        projectEntity6.getTeamMembers().add(songhwa);
//        songhwa.getProjectsJoined().add(projectEntity6);
//
//        profileEntityRepository.save(songhwa);
//        projectEntityRepository.save(projectEntity6);
//        /* end of add team member */
        ProjectEntity projectEntity7 = new ProjectEntity("Protect reefs through sustainable tourism in Indonesia", "To protect threatened coral reefs in Indonesia by uniting governments, NGOs and the diving and snorkelling industry to establish international environmental standards for marine tourism. Thus, more trashbins should be placed in tourist spots to keep the waters clean. ", "Indonesia", LocalDateTime.now(), LocalDateTime.parse("2021-06-05T11:50:55"));
        projectEntity7.getSdgs().add(climateAction);
        projectEntity7.getSdgs().add(sustainableCities);
        projectEntity7.setUpvotes(30);

        projectEntity7.setProjectPoolPoints(130);

        //relatedResources
        relatedResources = new ArrayList<>();
        relatedResources.add("Trash Bin");
        projectEntity7.setRelatedResources(relatedResources);

        projectEntity7.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity7.getSdgs().add(responsibleConsumption);
        projectEntity7.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project7.jpg");
        projectEntity7.getPhotos().add("https://localhost:8443/api/v1/files/init/project7.jpg");
        projectEntity7.getPhotos().add("https://localhost:8443/api/v1/files/init/reef.jpg");
        projectEntity7.getPhotos().add("https://localhost:8443/api/v1/files/init/reef2.jpg");
        projectEntity7.getPhotos().add("https://localhost:8443/api/v1/files/init/reef3.jpg");

        projectService.createProject(projectEntity7, 7L);
        /* create project badge */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Reefs Protecter", "https://localhost:8443/api/v1/files/badgeIcons/animal.png");
        projBadge.setProject(projectEntity7);
        badgeEntityRepository.save(projBadge);

        projectEntity7.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity7);
        /* end of project badge */

        ProjectEntity projectEntity8 = new ProjectEntity("Solar lamps for remote villages in the Peruvian Andes", "To supply a number of households in remote villages in the Andes with solar lamps and solar panels (that charge effectively with cloud cover).", "Peru", LocalDateTime.parse("2022-06-05T11:50:55"), LocalDateTime.parse("2030-06-05T11:50:55"));
        projectEntity8.getSdgs().add(genderEquality);
        projectEntity8.getSdgs().add(qualityEducation);
        projectEntity8.getSdgs().add(goodHealth);
        projectEntity8.setUpvotes(23);

        projectEntity8.setProjectPoolPoints(123);

        //relatedResources
        relatedResources = new ArrayList<>();
        relatedResources.add("Solar Lamp");
        projectEntity8.setRelatedResources(relatedResources);

        projectEntity8.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity8.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project8.jpg");
        projectEntity8.getPhotos().add("https://localhost:8443/api/v1/files/init/project8.jpg");
        projectEntity8.getPhotos().add("https://localhost:8443/api/v1/files/init/solar.jpg");
        projectEntity8.getPhotos().add("https://localhost:8443/api/v1/files/init/solar2.jpg");
        projectService.createProject(projectEntity8, 7L);
        /* create project badge */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Light Up Peruvian Andes", "https://localhost:8443/api/v1/files/badgeIcons/construction.png");
        projBadge.setProject(projectEntity8);
        badgeEntityRepository.save(projBadge);

        projectEntity8.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity8);
        /* end of project badge */

        ProjectEntity projectEntity9 = new ProjectEntity("Support mother and child health in remote Indonesia", "This project will encourage exclusive breastfeeding practices in Nias, Indonesia to help minimize the damage caused by the malnutrition-infection cycle. Training and coaching, supported by localized education materials, should stimulate uptake of exclusive breastfeeding and save lives. Supply of milk is needed for this project.", "Indonesia", LocalDateTime.parse("2021-02-01T11:50:55"), LocalDateTime.parse("2026-02-01T11:50:55"));
        projectEntity9.getSdgs().add(poverty);
        projectEntity9.getSdgs().add(qualityEducation);
        projectEntity9.getSdgs().add(goodHealth);
        projectEntity9.setUpvotes(30);

        projectEntity9.setProjectPoolPoints(130);

        //relatedResources
        relatedResources = new ArrayList<>();
        relatedResources.add("Milk");
        projectEntity9.setRelatedResources(relatedResources);

        projectEntity9.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity9.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project9profile.jpg");
        projectEntity9.getPhotos().add("https://localhost:8443/api/v1/files/init/project9photo1.jpg");
        projectEntity9.getPhotos().add("https://localhost:8443/api/v1/files/init/project9photo2.jpg");
        projectEntity9.getPhotos().add("https://localhost:8443/api/v1/files/init/project9photo3.jpg");
        projectService.createProject(projectEntity9, 8L);
        /* create project badge */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Mother and Child Supporter", "https://localhost:8443/api/v1/files/badgeIcons/gender-equality.png");
        projBadge.setProject(projectEntity9);
        badgeEntityRepository.save(projBadge);

        projectEntity9.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity9);
        /* end of project badge */

        ProjectEntity projectEntity10 = new ProjectEntity("Help youth gain employment in Vanuatu", "This project will offer training opportunities for young women, school leavers and disadvantaged youth to improve their skills, confidence and networks, helping them to gain employment or start an enterprise. Donation of Electronic devices would be appreciated as it helps individuals learn to be tech savvy, and stay connected with the world.", "Vanuatu", LocalDateTime.parse("2020-12-01T11:50:55"), LocalDateTime.parse("2030-12-01T11:50:55"));
        projectEntity10.getSdgs().add(poverty);
        projectEntity10.getSdgs().add(qualityEducation);
        projectEntity10.setUpvotes(35);

        projectEntity10.setProjectPoolPoints(135);

        //relatedResources
        relatedResources = new ArrayList<>();
        relatedResources.add("Laptop");
        relatedResources.add("Earpiece");
        projectEntity10.setRelatedResources(relatedResources);

        projectEntity10.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity10.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project10profile.jpg");
        projectEntity10.getPhotos().add("https://localhost:8443/api/v1/files/init/project10photo1.jpg");
        projectEntity10.getPhotos().add("https://localhost:8443/api/v1/files/init/project10photo2.jpg");
        projectService.createProject(projectEntity10, 8L);
        /* create project badge */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Supported Youth Employment", "https://localhost:8443/api/v1/files/badgeIcons/partnerships.png");
        projBadge.setProject(projectEntity10);
        badgeEntityRepository.save(projBadge);

        projectEntity10.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity10);
        /* end of project badge */

        ProjectEntity projectEntity11 = new ProjectEntity("Protect endangered zebras in Kenya", "The Grevy's zebra (Equus grevyi) is one of Africa's most endangered large mammals. They are a separate species of zebra, distinct from the widely-recognized common zebra (or plains zebra) through their large, fluffy ears, white belly, and comparatively thinner black stripes. Once distributed across the horn of Africa, 92% of the remaining Grevy’s zebra are now only found in Kenya, with a few small isolated populations in Ethiopia. Donation of food stuff for the endangered zebras would be greated appreciated to ensure that the zebras are not malnutritioned.", "Kenya", LocalDateTime.parse("2019-12-01T11:50:55"), LocalDateTime.parse("2030-12-01T11:50:55"));
        projectEntity11.getSdgs().add(sustainableCities);
        projectEntity11.setUpvotes(35);

        projectEntity11.setProjectPoolPoints(135);

        //relatedResources
        relatedResources = new ArrayList<>();
        relatedResources.add("Grass");
        relatedResources.add("Greens");
        relatedResources.add("Fruits");
        projectEntity11.setRelatedResources(relatedResources);

        projectEntity11.setProjStatus(ProjectStatusEnum.ACTIVE);
        projectEntity11.setProjectProfilePic("https://localhost:8443/api/v1/files/init/zebra.jpeg");
        projectEntity11.getPhotos().add("https://localhost:8443/api/v1/files/init/zebra1.jpeg");
        projectEntity11.getPhotos().add("https://localhost:8443/api/v1/files/init/zebra.jpeg");
        projectService.createProject(projectEntity11, 11L);
        /* create project badge */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Zebra Protector", "https://localhost:8443/api/v1/files/badgeIcons/animal.png");
        projBadge.setProject(projectEntity11);
        badgeEntityRepository.save(projBadge);

        projectEntity11.setProjectBadge(projBadge);
        projectEntityRepository.save(projectEntity11);
        /* end of project badge */

//        ProjectEntity projectEntity12 = new ProjectEntity("Close the gap in eye health for Indigenous Australians", "For decades, Aboriginal and Torres Strait Lander people have experienced low health outcomes than non-indigenous Australians. Today, there’s still a ten-year gap in life expectancy. Poor eye health and a lack of easy access to services play a part in this.", "Australia", LocalDateTime.parse("2018-12-01T11:50:55"), LocalDateTime.parse("2030-12-01T11:50:55"));
//        projectEntity12.getSdgs().add(goodHealth);
//        projectEntity12.setUpvotes(35);
//
//        projectEntity12.setProjectPoolPoints(135);
//
//        //relatedResources
//        relatedResources = new ArrayList<>();
//        relatedResources.add("Spectacles");
//        projectEntity12.setRelatedResources(relatedResources);
//
//        projectEntity12.setProjStatus(ProjectStatusEnum.ACTIVE);
//        projectEntity12.setProjectProfilePic("https://localhost:8443/api/v1/files/init/eye.jpg");
//        projectEntity12.getPhotos().add("https://localhost:8443/api/v1/files/init/eye1.jpg");
//        projectEntity12.getPhotos().add("https://localhost:8443/api/v1/files/init/eye.jpg");
//        projectService.createProject(projectEntity12, 12L);
//        /* create project badge */
//        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Eye Care Advocator", "https://localhost:8443/api/v1/files/badgeIcons/healthcare.png");
//        projBadge.setProject(projectEntity12);
//        badgeEntityRepository.save(projBadge);
//
//        projectEntity12.setProjectBadge(projBadge);
//        projectEntityRepository.save(projectEntity12);
//        /* end of project badge */
//        ProjectEntity projectEntity13 = new ProjectEntity("Build School Toilets for Nepal", "Building School-Friendly Toilets for Girls in Nepal.", "Nepal", LocalDateTime.parse("2018-12-01T11:50:55"), LocalDateTime.parse("2030-12-01T11:50:55"));
//        projectEntity13.getSdgs().add(goodHealth);
//        projectEntity13.setUpvotes(35);
//
//        projectEntity13.setProjectPoolPoints(135);
//
//        //relatedResources
//        relatedResources = new ArrayList<>();
//        relatedResources.add("Disinfectant");
//        relatedResources.add("Cleanser");
//        projectEntity13.setRelatedResources(relatedResources);
//
//        projectEntity13.setProjStatus(ProjectStatusEnum.ACTIVE);
//        projectEntity13.setProjectProfilePic("https://localhost:8443/api/v1/files/init/toilet.jpg");
//        projectEntity13.getPhotos().add("https://localhost:8443/api/v1/files/init/toilet1.jpg");
//        projectEntity13.getPhotos().add("https://localhost:8443/api/v1/files/init/toilet.jpg");
//        projectService.createProject(projectEntity13, 5L);
//        /* create project badge */
//        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Toilet Builder", "https://localhost:8443/api/v1/files/badgeIcons/construction.png");
//        projBadge.setProject(projectEntity13);
//        badgeEntityRepository.save(projBadge);
//
//        projectEntity13.setProjectBadge(projBadge);
//        projectEntityRepository.save(projectEntity13);
//        /* end of project badge */
    }

    //for badge and review use cases
    public void initCompletedProjects() {

        SDGEntity genderEquality = sdgEntityRepository.findBySdgName("Gender Equality");
        SDGEntity cleanWater = sdgEntityRepository.findBySdgName("Clean Water and Sanitation");
        SDGEntity sustainablecities = sdgEntityRepository.findBySdgName("Sustainable Cities and Communities");
        SDGEntity lifeBelowWater = sdgEntityRepository.findBySdgName("Life Below Water");
        SDGEntity responsibleConsumption = sdgEntityRepository.findBySdgName("Responsible Consumption and Production");
        SDGEntity qualityEducation = sdgEntityRepository.findBySdgName("Quality Education");

        //create a completed project 
        /* start of completed project 1 */
        String projDesc = "One of Seoul KFEM’s projects is to clean up and protect the Hangang River, "
                + "which runs through South Korea’s capital city. The river is a vital source of water "
                + "for the 10 million people who live there, but years of neglect have dramatically "
                + "reduced the quality of the water. As part of the new partnership, Oris will be "
                + "supporting a series of clean-up days Seoul KFEM has scheduled. During these, "
                + "hundreds of local volunteers will work along the river to pick up litter, "
                + "plastic and other harmful pollutants. The events will also help raise awareness "
                + "of the importance of clean water in Seoul.";
        ProjectEntity completedProject = new ProjectEntity("Time To Clean Up Hangang River", projDesc, "South Korea", LocalDateTime.parse("2018-06-05T11:45:55"), LocalDateTime.parse("2020-02-26T10:25:55"));
        completedProject.setProjStatus(ProjectStatusEnum.COMPLETED);
        completedProject.setUpvotes(45);

        completedProject.setProjectPoolPoints(145);

        completedProject.getPhotos().add("https://localhost:8443/api/v1/files/init/timeToCleanUpHangang.jpg");
        completedProject.getPhotos().add("https://localhost:8443/api/v1/files/init/timeToCleanUpHangang1.jpg");
        completedProject.setProjectProfilePic("https://localhost:8443/api/v1/files/init/timeToCleanUpHangang.jpg");

        //associations
        completedProject.getSdgs().add(cleanWater);
        completedProject.getSdgs().add(sustainablecities);
        completedProject.getSdgs().add(lifeBelowWater);

        projectService.createProject(completedProject, 5L);
        //set team members 
        ProfileEntity songhwa = profileEntityRepository.findById(Long.valueOf(9)).get();
        completedProject.getTeamMembers().add(songhwa);
        songhwa.getProjectsJoined().add(completedProject);

        /* set reviews for project */
        ReviewEntity reviewForIkjun = new ReviewEntity(LocalDateTime.of(2020, Month.MARCH, 15, 9, 0), "Ik Jun was very helpful and played a great role in this project! He motivated the team to keep progressing when things got tough.", BigDecimal.valueOf(5));
        reviewForIkjun.setReviewer(songhwa);
        reviewForIkjun.setProject(completedProject);

        ProfileEntity ikjun = profileEntityRepository.findById(Long.valueOf(5)).get();
        reviewForIkjun.setReviewReceiver(ikjun);
        ikjun.getReviewsReceived().add(reviewForIkjun);
        reviewEntityRepository.save(reviewForIkjun);

        //do another review for songhwa
        ReviewEntity reviewForSonghwa = new ReviewEntity(LocalDateTime.of(2020, Month.MARCH, 17, 11, 15), "Songhwa brought positivity into the project, and took good care of the elderly during community river cleanup events.", BigDecimal.valueOf(5));
        reviewForSonghwa.setReviewer(ikjun);
        reviewForSonghwa.setProject(completedProject);

        reviewForSonghwa.setReviewReceiver(songhwa);
        songhwa.getReviewsReceived().add(reviewForSonghwa);
        reviewEntityRepository.save(reviewForSonghwa);

        /* create project badge for completed project */
        BadgeEntity projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Hangang Cleanup Contributor", "https://localhost:8443/api/v1/files/badgeIcons/environment.png");
        projBadge.setProject(completedProject);
        completedProject.setProjectBadge(projBadge);
        projectEntityRepository.save(completedProject);

        projBadge.getProfiles().add(ikjun);
        projBadge.getProfiles().add(songhwa);

        badgeEntityRepository.save(projBadge);

        ikjun.getBadges().add(projBadge);
        songhwa.getBadges().add(projBadge);

        profileEntityRepository.save(ikjun);
        profileEntityRepository.save(songhwa);

        //associate resources 
//        ResourceRequestEntity rr1 = new ResourceRequestEntity(Long.valueOf(5), Long.valueOf(22), completedProject.getProjectId(), 10, "Testing testing");
//        rr1.setRequestCreationTime(LocalDateTime.of(2020, Month.MARCH, 17, 11, 15));
//        rr1.setRequestorEnum(RequestorEnum.RESOURCE_OWNER);
//        rr1.setStatus(RequestStatusEnum.ACCEPTED);
//        resourceRequestEntityRepository.saveAndFlush(rr1);

        /* end of completed project 1 */

 /* start of completed project 2 */
        projDesc = "How can you play a part in making fashion sustainable? Surely, some of us must have"
                + " attempted the KonMarie’s method to start our journey on decluttering our jam-packed "
                + "closet. The meaning of decluttering is to get rid of clothes that do not spark joy out. "
                + "Thanks to KonMarie,  your closet’s now organised, clean, and left with the items you need. "
                + "What’s next? What do you do with the items which do not spark joy? Discard them? Often, "
                + "this scenario is common in Singapore. However, do you know that discarding is not the only "
                + "way to get rid of these pre-loved clothes? You can give them a new lease of life by recycling! Let's get started!";
        completedProject = new ProjectEntity("Recycle Clothes Movement", projDesc, "Singapore", LocalDateTime.parse("2018-07-07T11:45:55"), LocalDateTime.parse("2020-03-28T10:25:55"));
        completedProject.setProjStatus(ProjectStatusEnum.COMPLETED);
        completedProject.setUpvotes(55);

        completedProject.setProjectPoolPoints(155);

        completedProject.getPhotos().add("https://localhost:8443/api/v1/files/init/recycleClothes.jpg");
        completedProject.getPhotos().add("https://localhost:8443/api/v1/files/init/recycleClothes1.jpg");
        completedProject.setProjectProfilePic("https://localhost:8443/api/v1/files/init/recycleClothes.jpg");

        //associations
        completedProject.getSdgs().add(sustainablecities);
        completedProject.getSdgs().add(responsibleConsumption);

        projectService.createProject(completedProject, 5L);
        //set team members 
        completedProject.getTeamMembers().add(songhwa);
        songhwa.getProjectsJoined().add(completedProject);

        /* create project badge for completed project */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Sustainable Fashion", "https://localhost:8443/api/v1/files/badgeIcons/environment.png");
        projBadge.setProject(completedProject);
        completedProject.setProjectBadge(projBadge);
        projectEntityRepository.save(completedProject);

        projBadge.getProfiles().add(ikjun);
        projBadge.getProfiles().add(songhwa);

        badgeEntityRepository.save(projBadge);

        ikjun.getBadges().add(projBadge);
        songhwa.getBadges().add(projBadge);

        profileEntityRepository.save(ikjun);
        profileEntityRepository.save(songhwa);
        /* end of completed project 2 */

 /* start of completed project 4 */
        projDesc = "End all forms of violence and harmful practices against women and girls, "
                + "regardless of gender identity and sexual orientation";
        completedProject = new ProjectEntity("End violence Against Women", projDesc, "India", LocalDateTime.parse("2017-02-07T11:45:55"), LocalDateTime.parse("2019-12-28T10:25:55"));
        completedProject.setProjStatus(ProjectStatusEnum.COMPLETED);
        completedProject.setUpvotes(36);

        completedProject.setProjectPoolPoints(136);

        completedProject.getPhotos().add("https://localhost:8443/api/v1/files/init/genderEqualityIndia.jpg");
        completedProject.getPhotos().add("https://localhost:8443/api/v1/files/init/genderEqualityIndia1.jpg");
        completedProject.setProjectProfilePic("https://localhost:8443/api/v1/files/init/genderEqualityIndia.jpg");

        //associations
        completedProject.getSdgs().add(genderEquality);

        projectService.createProject(completedProject, 5L);
        //set team members 
        completedProject.getTeamMembers().add(songhwa);
        songhwa.getProjectsJoined().add(completedProject);

        /* create project badge for completed project */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Equal Beings", "https://localhost:8443/api/v1/files/badgeIcons/gender-equality.png");
        projBadge.setProject(completedProject);
        completedProject.setProjectBadge(projBadge);
        projectEntityRepository.save(completedProject);

        projBadge.getProfiles().add(ikjun);
        projBadge.getProfiles().add(songhwa);

        badgeEntityRepository.save(projBadge);

        ikjun.getBadges().add(projBadge);
        songhwa.getBadges().add(projBadge);

        profileEntityRepository.save(ikjun);
        profileEntityRepository.save(songhwa);
        /* end of completed project 3 */

 /* start of completed project 4 */
        projDesc = "The Education for All Project's long-term development objective is to improve access to, "
                + "and benefit from basic and primary education for children, especially girls and children "
                + "from disadvantaged groups, and from literacy programs for poor adults. The project will "
                + "achieve three strategic goals: a) improving access to, and equity of basic and primary "
                + "education; b) enhancing educational quality and relevance; and, c) improving efficiency, "
                + "and institutional capacity of education service delivery. ";
        completedProject = new ProjectEntity("Education For All", projDesc, "Liberia", LocalDateTime.parse("2015-02-07T11:45:55"), LocalDateTime.parse("2018-11-20T10:25:55"));
        completedProject.setProjStatus(ProjectStatusEnum.COMPLETED);
        completedProject.setUpvotes(60);

        completedProject.setProjectPoolPoints(160);

        completedProject.getPhotos().add("https://localhost:8443/api/v1/files/init/educationForAll.jpg");
        completedProject.getPhotos().add("https://localhost:8443/api/v1/files/init/educationForAll1.jpg");
        completedProject.getPhotos().add("https://localhost:8443/api/v1/files/init/educationForAll2.jpg");
        completedProject.setProjectProfilePic("https://localhost:8443/api/v1/files/init/educationForAll.jpg");

        //associations
        completedProject.getSdgs().add(qualityEducation);

        projectService.createProject(completedProject, 5L);
        //set team members 
        completedProject.getTeamMembers().add(songhwa);
        songhwa.getProjectsJoined().add(completedProject);

        /* create project badge for completed project */
        projBadge = new BadgeEntity(BadgeTypeEnum.PROJECT_SPECIFIC, "Liberia Education Movement", "https://localhost:8443/api/v1/files/badgeIcons/education.png");
        projBadge.setProject(completedProject);
        completedProject.setProjectBadge(projBadge);
        projectEntityRepository.save(completedProject);

        projBadge.getProfiles().add(ikjun);
        projBadge.getProfiles().add(songhwa);

        badgeEntityRepository.save(projBadge);

        ikjun.getBadges().add(projBadge);
        songhwa.getBadges().add(projBadge);

        profileEntityRepository.save(ikjun);
        profileEntityRepository.save(songhwa);
        /* end of completed project 4 */

    }

    public void initCommunityBadges() {
        //create sysadmin badge
        BadgeEntity sysadmin = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "SYSADMIN", "https://localhost:8443/api/v1/files/communityBadges/sysadmin.png");
        badgeEntityRepository.save(sysadmin);

        //create leaderboard badges
        BadgeEntity top10 = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "TOP 10 IN LEADERBOARD", "https://localhost:8443/api/v1/files/communityBadges/top10.png");
        badgeEntityRepository.save(top10);

        BadgeEntity top50 = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "TOP 50 IN LEADERBOARD", "https://localhost:8443/api/v1/files/communityBadges/top50.png");
        badgeEntityRepository.save(top50);

        BadgeEntity top100 = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "TOP 100 IN LEADERBOARD", "https://localhost:8443/api/v1/files/communityBadges/top100.png");
        badgeEntityRepository.save(top100);

        //create project contributor badges
        BadgeEntity fiveProjects = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "5 PROJECT CONTRIBUTIONS", "https://localhost:8443/api/v1/files/communityBadges/5projects.png");
        badgeEntityRepository.save(fiveProjects);

        BadgeEntity tenProjects = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "10 PROJECT CONTRIBUTIONS", "https://localhost:8443/api/v1/files/communityBadges/10projects.png");
        badgeEntityRepository.save(tenProjects);

        BadgeEntity fiftyProjects = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "50 PROJECT CONTRIBUTIONS", "https://localhost:8443/api/v1/files/communityBadges/50projects.png");
        badgeEntityRepository.save(fiftyProjects);

        //create long service badges
        BadgeEntity oneYear = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "1 YEAR WITH MATCHUB", "https://localhost:8443/api/v1/files/communityBadges/1year.png");
        badgeEntityRepository.save(oneYear);

        BadgeEntity twoYears = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "2 YEARS WITH MATCHUB", "https://localhost:8443/api/v1/files/communityBadges/2years.png");
        badgeEntityRepository.save(twoYears);

        BadgeEntity fiveYears = new BadgeEntity(BadgeTypeEnum.COMMUNITY, "5 YEARS WITH MATCHUB", "https://localhost:8443/api/v1/files/communityBadges/5years.png");
        badgeEntityRepository.save(fiveYears);
    }

    private void initJoinRequest() {
        try {
            projectService.createJoinRequest(3L, 4L);
            projectService.createJoinRequest(3L, 6L);
        } catch (Exception e) {
            System.err.println("Error in init join request");
        }

        try {
            projectService.createJoinRequest(1L, 11L);
            projectService.createJoinRequest(1L, 12L);
            projectService.createJoinRequest(1L, 7L);
        } catch (Exception e) {
            System.err.println("Error in init join request");
        }

    }

    private void initResourceRequests() {

        try {
            resourceRequestService.createResourceRequestResourceOwner(1L, 9L, 6L, 12);
        } catch (Exception e) {
            System.err.println("Error in init resource request for projectId 1: Lamp");
        }

        try {
            resourceRequestService.createResourceRequestResourceOwner(1L, 11L, 19L, 10);
        } catch (Exception e) {
            System.err.println("Error in init resource request for projectId 1: Eyeglasses");
        }

    }

    private void initProjectFollower() {
        ProjectEntity project1 = projectEntityRepository.findById(1L).get();
        ProjectEntity project2 = projectEntityRepository.findById(2L).get();
        ProjectEntity project3 = projectEntityRepository.findById(3L).get();
        ProjectEntity project4 = projectEntityRepository.findById(4L).get();
        ProjectEntity project5 = projectEntityRepository.findById(5L).get();

        ProfileEntity user2 = profileEntityRepository.findById(2L).get();
        ProfileEntity user3 = profileEntityRepository.findById(3L).get();
        ProfileEntity user4 = profileEntityRepository.findById(4L).get();
        ProfileEntity user5 = profileEntityRepository.findById(5L).get();
        ProfileEntity user7 = profileEntityRepository.findById(7L).get();

        // every user follows project 1
        project1.getProjectFollowers().add(user2);
        project1.getProjectFollowers().add(user3);
        project1.getProjectFollowers().add(user4);
        project1.getProjectFollowers().add(user5);
        project1.getProjectFollowers().add(user7);

        user2.getProjectsFollowing().add(project1);
        user3.getProjectsFollowing().add(project1);
        user4.getProjectsFollowing().add(project1);
        user5.getProjectsFollowing().add(project1);
        user7.getProjectsFollowing().add(project1);

        // every user follows project 2
        project2.getProjectFollowers().add(user2);
        project2.getProjectFollowers().add(user3);
        project2.getProjectFollowers().add(user4);
        project2.getProjectFollowers().add(user5);
        project2.getProjectFollowers().add(user7);

        user2.getProjectsFollowing().add(project2);
        user3.getProjectsFollowing().add(project2);
        user4.getProjectsFollowing().add(project2);
        user5.getProjectsFollowing().add(project2);
        user7.getProjectsFollowing().add(project2);

        // every user follows project 3
        project3.getProjectFollowers().add(user2);
        project3.getProjectFollowers().add(user3);
        project3.getProjectFollowers().add(user4);
        project3.getProjectFollowers().add(user5);
        project3.getProjectFollowers().add(user7);

        user2.getProjectsFollowing().add(project3);
        user3.getProjectsFollowing().add(project3);
        user4.getProjectsFollowing().add(project3);
        user5.getProjectsFollowing().add(project3);
        user7.getProjectsFollowing().add(project3);

        // every user follows project 4
        project4.getProjectFollowers().add(user2);
        project4.getProjectFollowers().add(user3);
        project4.getProjectFollowers().add(user4);
        project4.getProjectFollowers().add(user5);
        project4.getProjectFollowers().add(user7);

        user2.getProjectsFollowing().add(project4);
        user3.getProjectsFollowing().add(project4);
        user4.getProjectsFollowing().add(project4);
        user5.getProjectsFollowing().add(project4);
        user7.getProjectsFollowing().add(project4);

        // every user follows project 5
        project5.getProjectFollowers().add(user2);
        project5.getProjectFollowers().add(user3);
        project5.getProjectFollowers().add(user4);
        project5.getProjectFollowers().add(user5);
        project5.getProjectFollowers().add(user7);

        user2.getProjectsFollowing().add(project5);
        user3.getProjectsFollowing().add(project5);
        user4.getProjectsFollowing().add(project5);
        user5.getProjectsFollowing().add(project5);
        user7.getProjectsFollowing().add(project5);

    }

    private void initPost() {
        // user 2
        PostVO post1 = new PostVO();
        post1.setPostCreatorId(2L);
        post1.setContent("What a nice day!");
        postService.createPostDataInit(post1);

        PostVO post2 = new PostVO();
        post2.setPostCreatorId(2L);
        post2.setContent("Hi my dear friends, I am planning to launch a new project related to saving the earth from global warming! Hit me up if you are interested to join :D");
        postService.createPostDataInit(post2);

        // user 3
        PostVO post3 = new PostVO();
        post3.setPostCreatorId(3L);
        post3.setContent("Hi my dear friends, I am planning to launch a new project related to saving the earth from global warming! Hit me up if you are interested to join :D");
        postService.createPostDataInit(post3);

        PostVO post4 = new PostVO();
        post4.setPostCreatorId(3L);
        post4.setContent("Today marks my ten years as a green campaigner! Really proud of myself");
        postService.createPostDataInit(post4);

        // user 4
        PostVO post5 = new PostVO();
        post5.setPostCreatorId(4L);
        post5.setContent("Food is life and thus agriculture ain't Only a basic necessity but a survival technique that all has to adopt in order to be alive!\n" + "s");
        postService.createPostDataInit(post5);

        PostVO post6 = new PostVO();
        post6.setPostCreatorId(4L);
        post6.setContent("Food is life and thus agriculture ain't Only a basic necessity but a survival technique that all has to adopt in order to be alive!\n" + "s");
        postService.createPostDataInit(post6);

        // user 5
        PostVO post7 = new PostVO();
        post7.setPostCreatorId(5L);
        post7.setContent("Good morning folks! \n"
                + "Let's talk about #SDGs and contribute our quota. \n"
                + "Have a wonderful week. More win$ #WealthSecrets");
        postService.createPostDataInit(post7);

        PostVO post8 = new PostVO();
        post8.setPostCreatorId(5L);
        post8.setContent("Reducing the amount of “stuff” you consume has the greatest benefits for the planet. It’s best to avoid waste in the first place, so think more carefully about your purchases.Re-using items saves the natural resources and energy needed to manufacture new ones.");
        postService.createPostDataInit(post8);

        // user 6
        PostVO post9 = new PostVO();
        post9.setPostCreatorId(6L);
        post9.setContent("Saving our planet, lifting people out of poverty, advancing economic growth... these are one and the same fight. We must connect the dots between climate change, energy shortages, global health, food security and women's empowerment\" - Ban Ki-moon");
        postService.createPostDataInit(post9);

        PostVO post10 = new PostVO();
        post10.setPostCreatorId(6L);
        post10.setContent("Rural development is crucial for meeting the #SDGs.\n"
                + "\n"
                + "Our projects transform rural communities economically and socially, while promoting gender equality and inclusiveness.\n"
                + "\n"
                + "Investing in rural people is investing in a brighter future for everyone.");
        postService.createPostDataInit(post10);

        // user 7
        PostVO post11 = new PostVO();
        post11.setPostCreatorId(7L);
        post11.setContent("Food is life and thus agriculture ain't Only a basic necessity but a survival technique that all has to adopt in order to be alive!\n" + "s");
        postService.createPostDataInit(post11);

        PostVO post12 = new PostVO();
        post12.setPostCreatorId(7L);
        post12.setContent("Red square The days ahead will be very consequential for the state of the world. The resurgence of COVID-19 and the US Presidential Election are just two of the issues that will weigh heavily on our future. During these days, standing firm for the #SDGs is a good way to stay grounded.");
        postService.createPostDataInit(post12);
    }

    private void setNotifications(ProfileEntity profileEntity) {
        for (AnnouncementTypeEnum a : AnnouncementTypeEnum.values()) {
            profileEntity.getAnnouncementsSetting().put(a, Boolean.TRUE);
        }
    }

}
