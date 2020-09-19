package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.ResourceCategoryEntityRepository;
import com.is4103.matchub.repository.ResourceEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import java.time.LocalDateTime;
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

    @Transactional
    public void init() {
        initSDG();
        initUsers();
        initResourceCategories();
        initResources();
        initProjects();
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
                        user1.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(4))));
                    } else {
                        account = accountEntityRepository.save(new OrganisationEntity(a + "@gmail.com", passwordEncoder.encode("password"), "NUS", "description", "address"));
                        account.getRoles().add(ProfileEntity.ROLE_USER);

                        //update the followers list 
                        OrganisationEntity user2 = (OrganisationEntity) account;
                        user2.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(4), Long.valueOf(5), Long.valueOf(6))));
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

        alexLow.setProjectFollowing(new HashSet<>(Arrays.asList(Long.valueOf(1))));

        alexLow.setCountryCode("+65");
        alexLow.setPhoneNumber("91234567");
        alexLow.setCountry("Singapore");
        alexLow.setCity("Singapore");
        alexLow.setProfilePhoto("https://localhost:8443/api/v1/files/init/alexlow.jpg");
        alexLow.setFollowing(new HashSet<>(Arrays.asList(Long.valueOf(2), Long.valueOf(3))));
        alexLow.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(8))));
        List<SDGEntity> sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(1)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(3)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(5)));
        alexLow.setSdgs(sdgs);
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

        ikjun.setProjectFollowing(new HashSet<>(Arrays.asList(Long.valueOf(1))));

        ikjun.setCountryCode("+82");
        ikjun.setPhoneNumber("011-465-9876");
        ikjun.setCountry("South Korea");
        ikjun.setCity("Seoul");
        ikjun.setProfilePhoto("https://localhost:8443/api/v1/files/init/ikjun.jpeg");
        ikjun.setFollowing(new HashSet<>(Arrays.asList(Long.valueOf(3))));
        ikjun.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(8))));

        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(3)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(4)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(5)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(11)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(14)));
        ikjun.setSdgs(sdgs);
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

        sophia.setProjectFollowing(new HashSet<>(Arrays.asList(Long.valueOf(2))));

        sophia.setCountryCode("+1");
        sophia.setPhoneNumber("604 598 5235");
        sophia.setCountry("Canada");
        sophia.setCity("Quebec");
        sophia.setProfilePhoto("https://localhost:8443/api/v1/files/init/sophia.jpg");
        sophia.setFollowing(new HashSet<>(Arrays.asList(Long.valueOf(3))));
        sophia.setFollowers(new HashSet<>(Arrays.asList(Long.valueOf(7))));
        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(5)));
        sophia.setSdgs(sdgs);
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
        genc.setEmployees(new HashSet<>(Arrays.asList(sophia.getAccountId())));
        genc.setCountryCode("+1");
        genc.setPhoneNumber("866 293 4483");
        genc.setCountry("Canada");
        genc.setCity("Toronto");
        genc.setProfilePhoto("https://localhost:8443/api/v1/files/init/genc.jpg");
        genc.setFollowing(new HashSet<>(Arrays.asList(sophia.getAccountId())));
        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(5)));
        genc.setSdgs(sdgs);
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
        areasOfExpertise = new HashSet<>(Arrays.asList("Fundraising Platform", "All-In-One Donor Management System", "Fundraising Software)"));
        networkForGood.setAreasOfExpertise(areasOfExpertise);
        networkForGood.setEmployees(new HashSet<>(Arrays.asList(alexLow.getAccountId())));
        networkForGood.setCountryCode("+1");
        networkForGood.setPhoneNumber("888 284 7978");
        networkForGood.setCountry("United States");
        networkForGood.setCity("Washington");
        networkForGood.setProfilePhoto("https://localhost:8443/api/v1/files/init/networkforgood.png");
        networkForGood.setFollowing(new HashSet<>(Arrays.asList(alexLow.getAccountId(), ikjun.getAccountId())));
        sdgs = new ArrayList<>();
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(1)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(2)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(4)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(8)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(10)));
        sdgs.add(sdgEntityRepository.findBySdgId(Long.valueOf(11)));
        networkForGood.setSdgs(sdgs);
        accountEntityRepository.save(networkForGood);
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
        ResourceCategoryEntity foodCategory = new ResourceCategoryEntity("Food", "All Food-related Resources", 1, 5, "kg");
        resourceCategoryService.createResourceCategory(foodCategory);

        ResourceCategoryEntity spaceCategory = new ResourceCategoryEntity("Space", "All Sapce-related Resources", 1, 5, "hour");
        resourceCategoryService.createResourceCategory(spaceCategory);

        ResourceCategoryEntity naturalResourceCategory = new ResourceCategoryEntity("Natural", "All Natural Resources", 1, 5, "kg");
        resourceCategoryService.createResourceCategory(naturalResourceCategory);

        ResourceCategoryEntity deviceCategory = new ResourceCategoryEntity("Device", "All Device-related Resources", 1, 10, "set");
        resourceCategoryService.createResourceCategory(deviceCategory);

        ResourceCategoryEntity transportationCategory = new ResourceCategoryEntity("Transportation", "All Transportation Resources", 1, 1, "hour");
        resourceCategoryService.createResourceCategory(transportationCategory);

        ResourceCategoryEntity educationCategory = new ResourceCategoryEntity("Eductaion", "All Education Resources", 1, 5, "set");
        resourceCategoryService.createResourceCategory(educationCategory);

    }

    public void initResources() {

        ResourceEntity bread = new ResourceEntity("Bread", "Free Bread", 10);
        bread.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_bread.jpg");
        bread.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_bread.jpg");
        bread.getPhotos().add("https://localhost:8443/api/v1/files/init/bread2.jpg");
        resourceService.createResource(bread, 1L, 2L);// category id, profileId

        ResourceEntity classroom = new ResourceEntity("Classroom", "Free classroom", LocalDateTime.parse("2021-06-05T11:50:55"), LocalDateTime.parse("2021-07-05T11:50:55"), 10);
        classroom.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_classroom.jpg");
        classroom.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_classroom.jpg");
        classroom.getPhotos().add("https://localhost:8443/api/v1/files/init/classroom.jpg");
        
        resourceService.createResource(classroom, 2L, 2L);

        ResourceEntity water = new ResourceEntity("Water", "10 Free Bottle Water", 10);
        water.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_water.jpg");
        water.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_water.jpg");
        water.getPhotos().add("https://localhost:8443/api/v1/files/init/water1.jpg");
        resourceService.createResource(water, 3L, 3L);

        ResourceEntity laptop = new ResourceEntity("Laptop", "10 Laptop for free rent", 10);
        laptop.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_laptop.jpg");
        laptop.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_laptop.jpg");
        laptop.getPhotos().add("https://localhost:8443/api/v1/files/init/laptop.jpg");
        resourceService.createResource(laptop, 4L, 4L);

        ResourceEntity bus = new ResourceEntity("Bus", "1 BUS free for rent for 1 day ", LocalDateTime.parse("2021-09-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10);
        bus.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_bus.jpg");
        bus.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_bus.jpg");
        bus.getPhotos().add("https://localhost:8443/api/v1/files/init/bus.jpg");
        resourceService.createResource(bus, 5L, 5L);

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

        ProjectEntity projectEntity1 = new ProjectEntity("Empowering Communities in Bangladesh", "To strengthen community resilience to prepare and respond to the risks associated with disasters and climate change by fostering economic empowerment, inclusive leadership and disaster preparedness.", "Bangladesh", LocalDateTime.now(), LocalDateTime.parse("2021-05-05T11:50:55"));
        projectEntity1.getSdgs().add(poverty);
        projectEntity1.getSdgs().add(zeroHunger);
        projectEntity1.setUpvotes(19);
        projectEntity1.setProjStatus(ProjectStatusEnum.ON_HOLD);
        projectEntity1.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project1.jpg");
        projectEntity1.getPhotos().add("https://localhost:8443/api/v1/files/init/project1.jpg");
        projectEntity1.getPhotos().add("https://localhost:8443/api/v1/files/init/Bangladesh.jpg");
        projectEntity1.getPhotos().add("https://localhost:8443/api/v1/files/init/Bangladesh1.jpg");
        projectEntity1.getPhotos().add("https://localhost:8443/api/v1/files/init/Bangladesh2.jpg");
        projectService.createProject(projectEntity1, 2L);

        ProjectEntity projectEntity2 = new ProjectEntity("Women's financial literacy, Malawi", "CARE will work with 20,000 women from 1,000 village savings and loans groups in Lilongwe, Dowa and Kasungu Districts, to overcome chronic hunger by expanding their farms or micro-businesses.", "Malawi", LocalDateTime.parse("2019-03-05T11:50:55"), LocalDateTime.parse("2019-06-05T11:50:55"));
        projectEntity2.getSdgs().add(genderEquality);
        projectEntity2.getSdgs().add(qualityEducation);
        projectEntity2.setUpvotes(19);
        projectEntity2.setProjStatus(ProjectStatusEnum.ON_HOLD);
        projectEntity2.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project2.jpg");
        projectEntity2.getPhotos().add("https://localhost:8443/api/v1/files/init/project2.jpg");
        projectEntity2.getPhotos().add("https://localhost:8443/api/v1/files/init/woman1.jpg");
        projectEntity2.getPhotos().add("https://localhost:8443/api/v1/files/init/woman2.jpg");
        projectEntity2.getPhotos().add("https://localhost:8443/api/v1/files/init/woman3.jpg");
        projectService.createProject(projectEntity2, 2L);

        ProjectEntity projectEntity3 = new ProjectEntity("Supporting rural families, Cambodia", "To support rural family.", "Cambodia", LocalDateTime.parse("2019-03-05T11:50:55"), LocalDateTime.parse("2019-06-05T11:50:55"));
        projectEntity3.getSdgs().add(poverty);
        projectEntity3.getSdgs().add(zeroHunger);
        projectEntity3.setUpvotes(19);
        projectEntity3.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project3.jpg");
        projectEntity3.getPhotos().add("https://localhost:8443/api/v1/files/init/project3.jpg");
        projectEntity3.getPhotos().add("https://localhost:8443/api/v1/files/init/rural.jpg");
        projectEntity3.getPhotos().add("https://localhost:8443/api/v1/files/init/rural2.jpg");
        projectService.createProject(projectEntity3, 2L);

        ProjectEntity projectEntity4 = new ProjectEntity("Building housing in Phnom Penh, Cambodia", "As an alternative to Schoolies, 18 Mosman High year 12 students are travelling to Cambodia to build houses for local Cambodians living in poverty.", "Cambodia", LocalDateTime.now(), LocalDateTime.parse("2021-06-05T11:50:55"));
        projectEntity4.getSdgs().add(genderEquality);
        projectEntity4.getSdgs().add(qualityEducation);
        projectEntity4.getSdgs().add(goodHealth);
        projectEntity4.setUpvotes(19);
        projectEntity4.setProjStatus(ProjectStatusEnum.ON_HOLD);
        projectEntity4.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project4.jpg");
        projectEntity4.getPhotos().add("https://localhost:8443/api/v1/files/init/project4.jpg");
        projectEntity4.getPhotos().add("https://localhost:8443/api/v1/files/init/building.jpg");
        projectEntity4.getPhotos().add("https://localhost:8443/api/v1/files/init/building2.jpg");
        projectEntity4.getPhotos().add("https://localhost:8443/api/v1/files/init/building3.jpg");
        
        
        projectService.createProject(projectEntity4, 3L);

        ProjectEntity projectEntity5 = new ProjectEntity("Promote inclusive access to water, sanitation and hygiene in Papua New Guinea", "The project aims to support improvement in the delivery of more inclusive, equitable and sustainable access to water, sanitation and hygiene (WASH) services ", "Cambodia", LocalDateTime.parse("2020-12-05T11:50:55"), LocalDateTime.parse("2021-03-05T11:50:55"));
        projectEntity5.getSdgs().add(cleanWater);
        projectEntity5.getSdgs().add(goodHealth);
        projectEntity5.setUpvotes(19);
        projectEntity5.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project5.png");
        projectEntity5.getPhotos().add("https://localhost:8443/api/v1/files/init/project5.png");
        projectEntity5.getPhotos().add("https://localhost:8443/api/v1/files/init/water.jpg");
        projectEntity5.getPhotos().add("https://localhost:8443/api/v1/files/init/water2.jpg");
        projectEntity5.getPhotos().add("https://localhost:8443/api/v1/files/init/water3.jpg");
        
        projectService.createProject(projectEntity5, 4L);

        ProjectEntity projectEntity6 = new ProjectEntity("Save endangered sea turtles in Panama", "This project will launch a sea turtle research and conservation program to protect endangered leatherback and hawksbill turtles that were found at Bocas del Drago, Panama.", "Panama", LocalDateTime.parse("2021-01-05T11:50:55"), LocalDateTime.parse("2025-06-05T11:50:55"));
        projectEntity6.getSdgs().add(climateAction);
        projectEntity6.getSdgs().add(sustainableCities);
        projectEntity6.setUpvotes(19);
        projectEntity6.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project6.jpg");
        projectEntity6.getPhotos().add("https://localhost:8443/api/v1/files/init/project6.jpg");
        projectEntity6.getPhotos().add("https://localhost:8443/api/v1/files/init/turtles.jpg");
        projectEntity6.getPhotos().add("https://localhost:8443/api/v1/files/init/turtles2.jpg");
        projectService.createProject(projectEntity6, 5L);

        ProjectEntity projectEntity7 = new ProjectEntity("Protect reefs through sustainable tourism in Indonesia", "To protect threatened coral reefs in Indonesia by uniting governments, NGOs and the diving and snorkelling industry to establish international environmental standards for marine tourism.", "Indonesia", LocalDateTime.now(), LocalDateTime.parse("2021-06-05T11:50:55"));
        projectEntity7.getSdgs().add(climateAction);
        projectEntity7.getSdgs().add(sustainableCities);
        projectEntity7.setUpvotes(19);
        projectEntity7.getSdgs().add(responsibleConsumption);
        projectEntity7.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project7.jpg");
        projectEntity7.getPhotos().add("https://localhost:8443/api/v1/files/init/project7.jpg");
        projectEntity7.getPhotos().add("https://localhost:8443/api/v1/files/init/reef.jpg");
        projectEntity7.getPhotos().add("https://localhost:8443/api/v1/files/init/reef2.jpg");
        projectEntity7.getPhotos().add("https://localhost:8443/api/v1/files/init/reef3.jpg");
               
        projectService.createProject(projectEntity7, 6L);

        ProjectEntity projectEntity8 = new ProjectEntity("Solar lamps for remote villages in the Peruvian Andes", "To supply a number of households in remote villages in the Andes with solar lamps and solar panels (that charge effectively with cloud cover).", "Peru", LocalDateTime.parse("2022-06-05T11:50:55"), LocalDateTime.parse("2030-06-05T11:50:55"));
        projectEntity8.getSdgs().add(genderEquality);
        projectEntity8.getSdgs().add(qualityEducation);
        projectEntity8.getSdgs().add(goodHealth);
        projectEntity8.setUpvotes(19);
        projectEntity8.setProjectProfilePic("https://localhost:8443/api/v1/files/init/project8.jpg");
        projectEntity8.getPhotos().add("https://localhost:8443/api/v1/files/init/project8.jpg");
        projectEntity8.getPhotos().add("https://localhost:8443/api/v1/files/init/solar.jpg");
        projectEntity8.getPhotos().add("https://localhost:8443/api/v1/files/init/solar2.jpg");
        projectService.createProject(projectEntity8, 6L);

    }

}
