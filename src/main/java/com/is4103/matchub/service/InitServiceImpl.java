package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.AnnouncementEntity;
import com.is4103.matchub.entity.BadgeEntity;
import com.is4103.matchub.entity.CompetitionEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.entity.ResourceEntity;
import com.is4103.matchub.entity.ReviewEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.entity.SDGTargetEntity;
import com.is4103.matchub.entity.SelectedTargetEntity;
import com.is4103.matchub.enumeration.AnnouncementTypeEnum;
import com.is4103.matchub.enumeration.BadgeTypeEnum;
import com.is4103.matchub.enumeration.CompetitionStatusEnum;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import com.is4103.matchub.enumeration.ResourceTypeEnum;
import com.is4103.matchub.exception.CreateAnnouncementException;
import com.is4103.matchub.exception.LikePostException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UserNotFoundException;
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
import com.is4103.matchub.repository.SDGTargetEntityRepository;
import com.is4103.matchub.repository.SelectedTargetEntityRepository;
import com.is4103.matchub.vo.AnnouncementVO;
import com.is4103.matchub.vo.CompetitionVO;
import com.is4103.matchub.vo.KanbanBoardVO;
import com.is4103.matchub.vo.PostVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.LongStream;

@Service
public class InitServiceImpl implements InitService {

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SDGEntityRepository sdgEntityRepository;

    @Autowired
    private ResourceCategoryService resourceCategoryService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceEntityRepository resourceEntityRepository;

    @Autowired
    private ProfileEntityRepository profileEntityRepository;

    @Autowired
    private ResourceCategoryEntityRepository resourceCategoryEntityRepository;

    @Autowired
    private ReviewEntityRepository reviewEntityRepository;

    @Autowired
    private BadgeEntityRepository badgeEntityRepository;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private ResourceRequestEntityRepository resourceRequestEntityRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private ResourceRequestService resourceRequestService;

    @Autowired
    private SDGTargetEntityRepository sDGTargetEntityRepository;

    @Autowired
    private SelectedTargetEntityRepository selectedTargetEntityRepository;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private KanbanBoardService kanbanBoardService;

    @Transactional
    public void init() {
        // testing:

        //init sdg and sdgTargets 
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

        // init project follower 
        initProjectFollower();

        initPost();

        //init resource requests
        initResourceRequests();
//        firebaseService.getChannelDetails("s");
        // init kanbanboard for project 3

        initCompetitions();

        initKanbanBoard();
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

        accountEntityRepository.saveAndFlush(alexLow);

        long[] sdgTargetIds = LongStream.rangeClosed(1, 7).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 1L, alexLow.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(16, 20).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 3L, alexLow.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(39, 45).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 5L, alexLow.getAccountId());

        setNotifications(alexLow);

        accountEntityRepository.save(alexLow);

        //2nd individual
        IndividualEntity ikjun = new IndividualEntity("ikjun@gmail.com", passwordEncoder.encode("password"), "Ik Jun", "Lee", GenderEnum.MALE);
        //account attributes
        ikjun.setUuid(UUID.randomUUID());
        ikjun.setDisabled(Boolean.FALSE);
        ikjun.setIsVerified(Boolean.TRUE);
        ikjun.getRoles().add(ProfileEntity.ROLE_USER);
        ikjun.getRoles().add(ProfileEntity.ROLE_SYSADMIN);
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

        accountEntityRepository.saveAndFlush(ikjun);

        sdgTargetIds = LongStream.rangeClosed(21, 27).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 3L, ikjun.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(29, 34).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 4L, ikjun.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(39, 45).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 5L, ikjun.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(91, 100).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 11L, ikjun.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(120, 126).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 14L, ikjun.getAccountId());

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

        accountEntityRepository.saveAndFlush(sophia);

        sdgTargetIds = LongStream.rangeClosed(44, 47).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 5L, sophia.getAccountId());

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

        accountEntityRepository.saveAndFlush(genc);

        sdgTargetIds = LongStream.rangeClosed(40, 47).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 5L, genc.getAccountId());

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

        accountEntityRepository.saveAndFlush(networkForGood);

        sdgTargetIds = LongStream.rangeClosed(1, 4).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 1L, networkForGood.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(10, 12).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 2L, networkForGood.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(30, 34).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 4L, networkForGood.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(66, 69).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 8L, networkForGood.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(82, 89).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 10L, networkForGood.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(91, 99).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 11L, networkForGood.getAccountId());

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

        accountEntityRepository.saveAndFlush(songhwa);

        sdgTargetIds = LongStream.rangeClosed(48, 54).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 6L, songhwa.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(56, 58).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 7L, songhwa.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(112, 114).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 13L, songhwa.getAccountId());

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

        accountEntityRepository.saveAndFlush(kfem);

        sdgTargetIds = LongStream.rangeClosed(48, 52).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 6L, kfem.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(56, 57).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 7L, kfem.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(92, 96).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 11L, kfem.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(105, 110).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 12L, kfem.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(113, 114).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 13L, kfem.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(119, 125).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 14L, kfem.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(129, 135).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 15L, kfem.getAccountId());

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

        accountEntityRepository.saveAndFlush(jeongha);

        sdgTargetIds = LongStream.rangeClosed(51, 55).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 6L, jeongha.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(57, 59).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 7L, jeongha.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(114, 116).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 13L, jeongha.getAccountId());

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

        accountEntityRepository.saveAndFlush(billy);

        sdgTargetIds = LongStream.rangeClosed(3, 5).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 1L, billy.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(9, 10).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 2L, billy.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(33, 35).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 4L, billy.getAccountId());

        sdgTargetIds = LongStream.rangeClosed(39, 42).toArray();
        associateSDGTargetsWithProfile(sdgTargetIds, 5L, billy.getAccountId());

        setNotifications(billy);
        accountEntityRepository.save(billy);
    }

    private void initSDG() {
        //sdg 1
        SDGEntity noPoverty = new SDGEntity("No Poverty", "End poverty in all its forms everywhere");
        sdgEntityRepository.save(noPoverty);

        //init sdg1 targets
        SDGTargetEntity target = new SDGTargetEntity("Target 1.1", "By 2030, eradicate extreme poverty for all people everywhere, currently measured as people living on less than $1.25 a day");
        sDGTargetEntityRepository.saveAndFlush(target);
        noPoverty.getTargets().add(target);

        target = new SDGTargetEntity("Target 1.2", "By 2030, reduce at least by half the proportion of men, women and children of all ages living in poverty in all its dimensions according to national definitions");
        sDGTargetEntityRepository.saveAndFlush(target);
        noPoverty.getTargets().add(target);

        target = new SDGTargetEntity("Target 1.3", "Implement nationally appropriate social protection systems and measures for all, including floors, and by 2030 achieve substantial coverage of the poor and the vulnerable");
        sDGTargetEntityRepository.saveAndFlush(target);
        noPoverty.getTargets().add(target);

        target = new SDGTargetEntity("Target 1.4", "By 2030, ensure that all men and women, in particular the poor and the vulnerable, have equal rights to economic resources, as well as access to basic services, ownership and control over land and other forms of property, inheritance, natural resources, appropriate new technology and financial services, including microfinance");
        sDGTargetEntityRepository.saveAndFlush(target);
        noPoverty.getTargets().add(target);

        target = new SDGTargetEntity("Target 1.5", "By 2030, build the resilience of the poor and those in vulnerable situations and reduce their exposure and vulnerability to climate-related extreme events and other economic, social and environmental shocks and disasters");
        sDGTargetEntityRepository.saveAndFlush(target);
        noPoverty.getTargets().add(target);

        target = new SDGTargetEntity("Target 1.a", "Ensure significant mobilization of resources from a variety of sources, including through enhanced development cooperation, in order to provide adequate and predictable means for developing countries, in particular least developed countries, to implement programmes and policies to end poverty in all its dimensions");
        sDGTargetEntityRepository.saveAndFlush(target);
        noPoverty.getTargets().add(target);

        target = new SDGTargetEntity("Target 1.b", "Create sound policy frameworks at the national, regional and international levels, based on pro-poor and gender-sensitive development strategies, to support accelerated investment in poverty eradication actions");
        sDGTargetEntityRepository.saveAndFlush(target);
        noPoverty.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(noPoverty);

        //sdg 2
        SDGEntity zeroHunger = new SDGEntity("Zero Hunger", "End hunger, achieve food security and improved nutrition and promote sustainable agriculture");
        sdgEntityRepository.save(zeroHunger);

        //init sdg2 targets
        target = new SDGTargetEntity("Target 2.1", "By 2030, end hunger and ensure access by all people, in particular the poor and people in vulnerable situations, including infants, to safe, nutritious and sufficient food all year round");
        sDGTargetEntityRepository.saveAndFlush(target);
        zeroHunger.getTargets().add(target);

        target = new SDGTargetEntity("Target 2.2", "By 2030, end all forms of malnutrition, including achieving, by 2025, the internationally agreed targets on stunting and wasting in children under 5 years of age, and address the nutritional needs of adolescent girls, pregnant and lactating women and older persons");
        sDGTargetEntityRepository.saveAndFlush(target);
        zeroHunger.getTargets().add(target);

        target = new SDGTargetEntity("Target 2.3", "By 2030, double the agricultural productivity and incomes of small-scale food producers, in particular women, indigenous peoples, family farmers, pastoralists and fishers, including through secure and equal access to land, other productive resources and inputs, knowledge, financial services, markets and opportunities for value addition and non-farm employment");
        sDGTargetEntityRepository.saveAndFlush(target);
        zeroHunger.getTargets().add(target);

        target = new SDGTargetEntity("Target 2.4", "By 2030, ensure sustainable food production systems and implement resilient agricultural practices that increase productivity and production, that help maintain ecosystems, that strengthen capacity for adaptation to climate change, extreme weather, drought, flooding and other disasters and that progressively improve land and soil quality");
        sDGTargetEntityRepository.saveAndFlush(target);
        zeroHunger.getTargets().add(target);

        target = new SDGTargetEntity("Target 2.5", "By 2020, maintain the genetic diversity of seeds, cultivated plants and farmed and domesticated animals and their related wild species, including through soundly managed and diversified seed and plant banks at the national, regional and international levels, and promote access to and fair and equitable sharing of benefits arising from the utilization of genetic resources and associated traditional knowledge, as internationally agreed");
        sDGTargetEntityRepository.saveAndFlush(target);
        zeroHunger.getTargets().add(target);

        target = new SDGTargetEntity("Target 2.a", "Increase investment, including through enhanced international cooperation, in rural infrastructure, agricultural research and extension services, technology development and plant and livestock gene banks in order to enhance agricultural productive capacity in developing countries, in particular least developed countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        zeroHunger.getTargets().add(target);

        target = new SDGTargetEntity("Target 2.b", "Correct and prevent trade restrictions and distortions in world agricultural markets, including through the parallel elimination of all forms of agricultural export subsidies and all export measures with equivalent effect, in accordance with the mandate of the Doha Development Round");
        sDGTargetEntityRepository.saveAndFlush(target);
        zeroHunger.getTargets().add(target);

        target = new SDGTargetEntity("Target 2.c", "Adopt measures to ensure the proper functioning of food commodity markets and their derivatives and facilitate timely access to market information, including on food reserves, in order to help limit extreme food price volatility");
        sDGTargetEntityRepository.saveAndFlush(target);
        zeroHunger.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(zeroHunger);

        //sdg 3
        SDGEntity goodHealth = new SDGEntity("Good Health and Well-being", "Ensure healthy lives and promote well-being for all at all ages");
        sdgEntityRepository.save(goodHealth);

        //init sdg3 targets
        target = new SDGTargetEntity("Target 3.1", "By 2030, reduce the global maternal mortality ratio to less than 70 per 100,000 live births");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.2", "By 2030, end preventable deaths of newborns and children under 5 years of age, with all countries aiming to reduce neonatal mortality to at least as low as 12 per 1,000 live births and under-5 mortality to at least as low as 25 per 1,000 live births");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.3", "By 2030, end the epidemics of AIDS, tuberculosis, malaria and neglected tropical diseases and combat hepatitis, water-borne diseases and other communicable diseases");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.4", "By 2030, reduce by one third premature mortality from non-communicable diseases through prevention and treatment and promote mental health and well-being");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.5", "Strengthen the prevention and treatment of substance abuse, including narcotic drug abuse and harmful use of alcohol");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.6", "By 2020, halve the number of global deaths and injuries from road traffic accidents");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.7", "By 2030, ensure universal access to sexual and reproductive health-care services, including for family planning, information and education, and the integration of reproductive health into national strategies and programmes");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.8", "Achieve universal health coverage, including financial risk protection, access to quality essential health-care services and access to safe, effective, quality and affordable essential medicines and vaccines for all");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.9", "By 2030, substantially reduce the number of deaths and illnesses from hazardous chemicals and air, water and soil pollution and contamination");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.a", "Strengthen the implementation of the World Health Organization Framework Convention on Tobacco Control in all countries, as appropriate");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.b", "Support the research and development of vaccines and medicines for the communicable and non-communicable diseases that primarily affect developing countries, provide access to affordable essential medicines and vaccines, in accordance with the Doha Declaration on the TRIPS Agreement and Public Health, which affirms the right of developing countries to use to the full the provisions in the Agreement on Trade-Related Aspects of Intellectual Property Rights regarding flexibilities to protect public health, and, in particular, provide access to medicines for all");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.c", "Substantially increase health financing and the recruitment, development, training and retention of the health workforce in developing countries, especially in least developed countries and small island developing States");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        target = new SDGTargetEntity("Target 3.d", "Strengthen the capacity of all countries, in particular developing countries, for early warning, risk reduction and management of national and global health risks");
        sDGTargetEntityRepository.saveAndFlush(target);
        goodHealth.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(goodHealth);

        //sdg 4
        SDGEntity qualityEducation = new SDGEntity("Quality Education", "Ensure inclusive and equitable quality education and promote lifelong learning opportunities for all");
        sdgEntityRepository.save(qualityEducation);

        target = new SDGTargetEntity("Target 4.1", "By 2030, ensure that all girls and boys complete free, equitable and quality primary and secondary education leading to relevant and effective learning outcomes");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        target = new SDGTargetEntity("Target 4.2", "By 2030, ensure that all girls and boys have access to quality early childhood development, care and pre-primary education so that they are ready for primary education");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        target = new SDGTargetEntity("Target 4.3", "By 2030, ensure equal access for all women and men to affordable and quality technical, vocational and tertiary education, including university");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        target = new SDGTargetEntity("Target 4.4", "By 2030, substantially increase the number of youth and adults who have relevant skills, including technical and vocational skills, for employment, decent jobs and entrepreneurship");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        target = new SDGTargetEntity("Target 4.5", "By 2030, eliminate gender disparities in education and ensure equal access to all levels of education and vocational training for the vulnerable, including persons with disabilities, indigenous peoples and children in vulnerable situations");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        target = new SDGTargetEntity("Target 4.6", "By 2030, ensure that all youth and a substantial proportion of adults, both men and women, achieve literacy and numeracy");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        target = new SDGTargetEntity("Target 4.7", "By 2030, ensure that all learners acquire the knowledge and skills needed to promote sustainable development, including, among others, through education for sustainable development and sustainable lifestyles, human rights, gender equality, promotion of a culture of peace and non-violence, global citizenship and appreciation of cultural diversity and of culture’s contribution to sustainable development");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        target = new SDGTargetEntity("Target 4.a", "Build and upgrade education facilities that are child, disability and gender sensitive and provide safe, non-violent, inclusive and effective learning environments for all");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        target = new SDGTargetEntity("Target 4.b", "By 2020, substantially expand globally the number of scholarships available to developing countries, in particular least developed countries, small island developing States and African countries, for enrolment in higher education, including vocational training and information and communications technology, technical, engineering and scientific programmes, in developed countries and other developing countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        target = new SDGTargetEntity("Target 4.c", "By 2030, substantially increase the supply of qualified teachers, including through international cooperation for teacher training in developing countries, especially least developed countries and small island developing States");
        sDGTargetEntityRepository.saveAndFlush(target);
        qualityEducation.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(qualityEducation);

        //sdg 5
        SDGEntity genderEquality = new SDGEntity("Gender Equality", "Achieve gender equality and empower all women and girls");
        sdgEntityRepository.save(genderEquality);

        //init sdg5 targets
        target = new SDGTargetEntity("Target 5.1", "End all forms of discrimination against all women and girls everywhere");
        sDGTargetEntityRepository.saveAndFlush(target);
        genderEquality.getTargets().add(target);

        target = new SDGTargetEntity("Target 5.2", "Eliminate all forms of violence against all women and girls in the public and private spheres, including trafficking and sexual and other types of exploitation");
        sDGTargetEntityRepository.saveAndFlush(target);
        genderEquality.getTargets().add(target);

        target = new SDGTargetEntity("Target 5.3", "Eliminate all harmful practices, such as child, early and forced marriage and female genital mutilation");
        sDGTargetEntityRepository.saveAndFlush(target);
        genderEquality.getTargets().add(target);

        target = new SDGTargetEntity("Target 5.4", "Recognize and value unpaid care and domestic work through the provision of public services, infrastructure and social protection policies and the promotion of shared responsibility within the household and the family as nationally appropriate");
        sDGTargetEntityRepository.saveAndFlush(target);
        genderEquality.getTargets().add(target);

        target = new SDGTargetEntity("Target 5.5", "Ensure women’s full and effective participation and equal opportunities for leadership at all levels of decision-making in political, economic and public life");
        sDGTargetEntityRepository.saveAndFlush(target);
        genderEquality.getTargets().add(target);

        target = new SDGTargetEntity("Target 5.6", "Ensure universal access to sexual and reproductive health and reproductive rights as agreed in accordance with the Programme of Action of the International Conference on Population and Development and the Beijing Platform for Action and the outcome documents of their review conferences");
        sDGTargetEntityRepository.saveAndFlush(target);
        genderEquality.getTargets().add(target);

        target = new SDGTargetEntity("Target 5.a", "Undertake reforms to give women equal rights to economic resources, as well as access to ownership and control over land and other forms of property, financial services, inheritance and natural resources, in accordance with national laws");
        sDGTargetEntityRepository.saveAndFlush(target);
        genderEquality.getTargets().add(target);

        target = new SDGTargetEntity("Target 5.b", "Enhance the use of enabling technology, in particular information and communications technology, to promote the empowerment of women");
        sDGTargetEntityRepository.saveAndFlush(target);
        genderEquality.getTargets().add(target);

        target = new SDGTargetEntity("Target 5.c", "Adopt and strengthen sound policies and enforceable legislation for the promotion of gender equality and the empowerment of all women and girls at all levels");
        sDGTargetEntityRepository.saveAndFlush(target);
        genderEquality.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(genderEquality);

        //sdg 6
        SDGEntity cleanWater = new SDGEntity("Clean Water and Sanitation", "Ensure availability and sustainable management of water and sanitation for all");
        sdgEntityRepository.save(cleanWater);

        //init sdg6 targets
        target = new SDGTargetEntity("Target 6.1", "By 2030, achieve universal and equitable access to safe and affordable drinking water for all");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 6.2", "By 2030, achieve access to adequate and equitable sanitation and hygiene for all and end open defecation, paying special attention to the needs of women and girls and those in vulnerable situations");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 6.3", "By 2030, improve water quality by reducing pollution, eliminating dumping and minimizing release of hazardous chemicals and materials, halving the proportion of untreated wastewater and substantially increasing recycling and safe reuse globally");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 6.4", "By 2030, substantially increase water-use efficiency across all sectors and ensure sustainable withdrawals and supply of freshwater to address water scarcity and substantially reduce the number of people suffering from water scarcity");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 6.5", "By 2030, implement integrated water resources management at all levels, including through transboundary cooperation as appropriate");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 6.6", "By 2020, protect and restore water-related ecosystems, including mountains, forests, wetlands, rivers, aquifers and lakes");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 6.a", "By 2030, expand international cooperation and capacity-building support to developing countries in water- and sanitation-related activities and programmes, including water harvesting, desalination, water efficiency, wastewater treatment, recycling and reuse technologies");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 6.b", "Support and strengthen the participation of local communities in improving water and sanitation management");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanWater.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(cleanWater);

        //sdg 7
        SDGEntity cleanEnergy = new SDGEntity("Affordable and Clean Energy", "Ensure access to affordable, reliable, sustainable and modern energy for all");
        sdgEntityRepository.save(cleanEnergy);

        //init sdg7 targets
        target = new SDGTargetEntity("Target 7.1", "By 2030, ensure universal access to affordable, reliable and modern energy services");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanEnergy.getTargets().add(target);

        target = new SDGTargetEntity("Target 7.2", "By 2030, increase substantially the share of renewable energy in the global energy mix");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanEnergy.getTargets().add(target);

        target = new SDGTargetEntity("Target 7.3", "By 2030, double the global rate of improvement in energy efficiency");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanEnergy.getTargets().add(target);

        target = new SDGTargetEntity("Target 7.a", "By 2030, enhance international cooperation to facilitate access to clean energy research and technology, including renewable energy, energy efficiency and advanced and cleaner fossil-fuel technology, and promote investment in energy infrastructure and clean energy technology");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanEnergy.getTargets().add(target);

        target = new SDGTargetEntity("Target 7.b", "By 2030, expand infrastructure and upgrade technology for supplying modern and sustainable energy services for all in developing countries, in particular least developed countries, small island developing States, and land-locked developing countries, in accordance with their respective programmes of support");
        sDGTargetEntityRepository.saveAndFlush(target);
        cleanEnergy.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(cleanEnergy);

        //sdg 8
        SDGEntity economicGrowth = new SDGEntity("Decent Work and Economic Growth", "Promote sustained, inclusive and sustainable economic growth, full and productive employment and decent work for all");
        sdgEntityRepository.save(economicGrowth);

        //init sdg8 targets
        target = new SDGTargetEntity("Target 8.1", "Sustain per capita economic growth in accordance with national circumstances and, in particular, at least 7 per cent gross domestic product growth per annum in the least developed countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.2", "Achieve higher levels of economic productivity through diversification, technological upgrading and innovation, including through a focus on high-value added and labour-intensive sectors");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.3", "Promote development-oriented policies that support productive activities, decent job creation, entrepreneurship, creativity and innovation, and encourage the formalization and growth of micro-, small- and medium-sized enterprises, including through access to financial services");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.4", "Improve progressively, through 2030, global resource efficiency in consumption and production and endeavour to decouple economic growth from environmental degradation, in accordance with the 10-year framework of programmes on sustainable consumption and production, with developed countries taking the lead");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.5", "By 2030, achieve full and productive employment and decent work for all women and men, including for young people and persons with disabilities, and equal pay for work of equal value");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.6", "By 2020, substantially reduce the proportion of youth not in employment, education or training");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.7", "Take immediate and effective measures to eradicate forced labour, end modern slavery and human trafficking and secure the prohibition and elimination of the worst forms of child labour, including recruitment and use of child soldiers, and by 2025 end child labour in all its forms");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.8", "Protect labour rights and promote safe and secure working environments for all workers, including migrant workers, in particular women migrants, and those in precarious employment");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.9", "By 2030, devise and implement policies to promote sustainable tourism that creates jobs and promotes local culture and products");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.10", "Strengthen the capacity of domestic financial institutions to encourage and expand access to banking, insurance and financial services for all");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.a", "Increase Aid for Trade support for developing countries, in particular least developed countries, including through the Enhanced Integrated Framework for Trade-Related Technical Assistance to Least Developed Countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        target = new SDGTargetEntity("Target 8.b", "By 2020, develop and operationalize a global strategy for youth employment and implement the Global Jobs Pact of the International Labour Organization");
        sDGTargetEntityRepository.saveAndFlush(target);
        economicGrowth.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(economicGrowth);

        //sdg 9
        SDGEntity industryInnovationInfrastructure = new SDGEntity("Industry, Innovation and Infrastructure", "Build resilient infrastructure, promote inclusive and sustainable industrialization and foster innovation");
        sdgEntityRepository.save(industryInnovationInfrastructure);

        //init sdg9 targets
        target = new SDGTargetEntity("Target 9.1", "Develop quality, reliable, sustainable and resilient infrastructure, including regional and transborder infrastructure, to support economic development and human well-being, with a focus on affordable and equitable access for all");
        sDGTargetEntityRepository.saveAndFlush(target);
        industryInnovationInfrastructure.getTargets().add(target);

        target = new SDGTargetEntity("Target 9.2", "Promote inclusive and sustainable industrialization and, by 2030, significantly raise industry’s share of employment and gross domestic product, in line with national circumstances, and double its share in least developed countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        industryInnovationInfrastructure.getTargets().add(target);

        target = new SDGTargetEntity("Target 9.3", "Increase the access of small-scale industrial and other enterprises, in particular in developing countries, to financial services, including affordable credit, and their integration into value chains and markets");
        sDGTargetEntityRepository.saveAndFlush(target);
        industryInnovationInfrastructure.getTargets().add(target);

        target = new SDGTargetEntity("Target 9.4", "By 2030, upgrade infrastructure and retrofit industries to make them sustainable, with increased resource-use efficiency and greater adoption of clean and environmentally sound technologies and industrial processes, with all countries taking action in accordance with their respective capabilities");
        sDGTargetEntityRepository.saveAndFlush(target);
        industryInnovationInfrastructure.getTargets().add(target);

        target = new SDGTargetEntity("Target 9.5", "Enhance scientific research, upgrade the technological capabilities of industrial sectors in all countries, in particular developing countries, including, by 2030, encouraging innovation and substantially increasing the number of research and development workers per 1 million people and public and private research and development spending");
        sDGTargetEntityRepository.saveAndFlush(target);
        industryInnovationInfrastructure.getTargets().add(target);

        target = new SDGTargetEntity("Target 9.a", "Facilitate sustainable and resilient infrastructure development in developing countries through enhanced financial, technological and technical support to African countries, least developed countries, landlocked developing countries and small island developing States");
        sDGTargetEntityRepository.saveAndFlush(target);
        industryInnovationInfrastructure.getTargets().add(target);

        target = new SDGTargetEntity("Target 9.b", "Support domestic technology development, research and innovation in developing countries, including by ensuring a conducive policy environment for, inter alia, industrial diversification and value addition to commodities");
        sDGTargetEntityRepository.saveAndFlush(target);
        industryInnovationInfrastructure.getTargets().add(target);

        target = new SDGTargetEntity("Target 9.c", "Significantly increase access to information and communications technology and strive to provide universal and affordable access to the Internet in least developed countries by 2020");
        sDGTargetEntityRepository.saveAndFlush(target);
        industryInnovationInfrastructure.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(industryInnovationInfrastructure);

        //sdg 10
        SDGEntity reduceInequalities = new SDGEntity("Reduce Inequalities", "Reduce inequality within and among countries");
        sdgEntityRepository.save(reduceInequalities);

        //init sdg10 targets
        target = new SDGTargetEntity("Target 10.1", "By 2030, progressively achieve and sustain income growth of the bottom 40 per cent of the population at a rate higher than the national average");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        target = new SDGTargetEntity("Target 10.2", "By 2030, empower and promote the social, economic and political inclusion of all, irrespective of age, sex, disability, race, ethnicity, origin, religion or economic or other status");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        target = new SDGTargetEntity("Target 10.3", "Ensure equal opportunity and reduce inequalities of outcome, including by eliminating discriminatory laws, policies and practices and promoting appropriate legislation, policies and action in this regard");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        target = new SDGTargetEntity("Target 10.4", "Adopt policies, especially fiscal, wage and social protection policies, and progressively achieve greater equality");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        target = new SDGTargetEntity("Target 10.5", "Improve the regulation and monitoring of global financial markets and institutions and strengthen the implementation of such regulations");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        target = new SDGTargetEntity("Target 10.6", "Ensure enhanced representation and voice for developing countries in decision-making in global international economic and financial institutions in order to deliver more effective, credible, accountable and legitimate institutions");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        target = new SDGTargetEntity("Target 10.7", "Facilitate orderly, safe, regular and responsible migration and mobility of people, including through the implementation of planned and well-managed migration policies");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        target = new SDGTargetEntity("Target 10.a", "Implement the principle of special and differential treatment for developing countries, in particular least developed countries, in accordance with World Trade Organization agreements");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        target = new SDGTargetEntity("Target 10.b", "Encourage official development assistance and financial flows, including foreign direct investment, to States where the need is greatest, in particular least developed countries, African countries, small island developing States and landlocked developing countries, in accordance with their national plans and programmes");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        target = new SDGTargetEntity("Target 10.c", "By 2030, reduce to less than 3 per cent the transaction costs of migrant remittances and eliminate remittance corridors with costs higher than 5 per cent");
        sDGTargetEntityRepository.saveAndFlush(target);
        reduceInequalities.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(reduceInequalities);

        //sdg 11
        SDGEntity sustainableCities = new SDGEntity("Sustainable Cities and Communities", "Make cities and human settlements inclusive, safe, resilient and sustainable");
        sdgEntityRepository.save(sustainableCities);

        //init sdg11 targets
        target = new SDGTargetEntity("Target 11.1", "By 2030, ensure access for all to adequate, safe and affordable housing and basic services and upgrade slums");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        target = new SDGTargetEntity("Target 11.2", "By 2030, provide access to safe, affordable, accessible and sustainable transport systems for all, improving road safety, notably by expanding public transport, with special attention to the needs of those in vulnerable situations, women, children, persons with disabilities and older persons");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        target = new SDGTargetEntity("Target 11.3", "By 2030, enhance inclusive and sustainable urbanization and capacity for participatory, integrated and sustainable human settlement planning and management in all countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        target = new SDGTargetEntity("Target 11.4", "Strengthen efforts to protect and safeguard the world’s cultural and natural heritage");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        target = new SDGTargetEntity("Target 11.5", "By 2030, significantly reduce the number of deaths and the number of people affected and substantially decrease the direct economic losses relative to global gross domestic product caused by disasters, including water-related disasters, with a focus on protecting the poor and people in vulnerable situations");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        target = new SDGTargetEntity("Target 11.6", "By 2030, reduce the adverse per capita environmental impact of cities, including by paying special attention to air quality and municipal and other waste management");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        target = new SDGTargetEntity("Target 11.7", "By 2030, provide universal access to safe, inclusive and accessible, green and public spaces, in particular for women and children, older persons and persons with disabilities");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        target = new SDGTargetEntity("Target 11.a", "Support positive economic, social and environmental links between urban, per-urban and rural areas by strengthening national and regional development planning");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        target = new SDGTargetEntity("Target 11.b", "By 2020, substantially increase the number of cities and human settlements adopting and implementing integrated policies and plans towards inclusion, resource efficiency, mitigation and adaptation to climate change, resilience to disasters, and develop and implement, in line with the Sendai Framework for Disaster Risk Reduction 2015-2030, holistic disaster risk management at all levels");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        target = new SDGTargetEntity("Target 11.c", "Support least developed countries, including through financial and technical assistance, in building sustainable and resilient buildings utilizing local materials");
        sDGTargetEntityRepository.saveAndFlush(target);
        sustainableCities.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(sustainableCities);

        //sdg 12
        SDGEntity responsibleConsumption = new SDGEntity("Responsible Consumption and Production", "Ensure sustainable consumption and production patterns");
        sdgEntityRepository.save(responsibleConsumption);

        //init sdg12 targets
        target = new SDGTargetEntity("Target 12.1", "Implement the 10-year framework of programmes on sustainable consumption and production, all countries taking action, with developed countries taking the lead, taking into account the development and capabilities of developing countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.2", "By 2030, achieve the sustainable management and efficient use of natural resources");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.3", "By 2030, halve per capita global food waste at the retail and consumer levels and reduce food losses along production and supply chains, including post-harvest losses");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.4", "By 2020, achieve the environmentally sound management of chemicals and all wastes throughout their life cycle, in accordance with agreed international frameworks, and significantly reduce their release to air, water and soil in order to minimize their adverse impacts on human health and the environment");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.5", "By 2030, substantially reduce waste generation through prevention, reduction, recycling and reuse");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.6", "Encourage companies, especially large and transnational companies, to adopt sustainable practices and to integrate sustainability information into their reporting cycle");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.7", "Promote public procurement practices that are sustainable, in accordance with national policies and priorities");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.8", "By 2030, ensure that people everywhere have the relevant information and awareness for sustainable development and lifestyles in harmony with nature");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.a", "Support developing countries to strengthen their scientific and technological capacity to move towards more sustainable patterns of consumption and production");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.b", "Develop and implement tools to monitor sustainable development impacts for sustainable tourism that creates jobs and promotes local culture and products");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        target = new SDGTargetEntity("Target 12.c", "Rationalize inefficient fossil-fuel subsidies that encourage wasteful consumption by removing market distortions, in accordance with national circumstances, including by restructuring taxation and phasing out those harmful subsidies, where they exist, to reflect their environmental impacts, taking fully into account the specific needs and conditions of developing countries and minimizing the possible adverse impacts on their development in a manner that protects the poor and the affected communities");
        sDGTargetEntityRepository.saveAndFlush(target);
        responsibleConsumption.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(responsibleConsumption);

        //sdg 13
        SDGEntity climateAction = new SDGEntity("Climate Action", "Take urgent action to combat climate change and its impacts");
        sdgEntityRepository.save(climateAction);

        //init sdg13 targets
        target = new SDGTargetEntity("Target 13.1", "Strengthen resilience and adaptive capacity to climate-related hazards and natural disasters in all countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        climateAction.getTargets().add(target);

        target = new SDGTargetEntity("Target 13.2", "Integrate climate change measures into national policies, strategies and planning");
        sDGTargetEntityRepository.saveAndFlush(target);
        climateAction.getTargets().add(target);

        target = new SDGTargetEntity("Target 13.3", "Improve education, awareness-raising and human and institutional capacity on climate change mitigation, adaptation, impact reduction and early warning");
        sDGTargetEntityRepository.saveAndFlush(target);
        climateAction.getTargets().add(target);

        target = new SDGTargetEntity("Target 13.a", "Implement the commitment undertaken by developed-country parties to the United Nations Framework Convention on Climate Change to a goal of mobilizing jointly $100 billion annually by 2020 from all sources to address the needs of developing countries in the context of meaningful mitigation actions and transparency on implementation and fully operationalize the Green Climate Fund through its capitalization as soon as possible");
        sDGTargetEntityRepository.saveAndFlush(target);
        climateAction.getTargets().add(target);

        target = new SDGTargetEntity("Target 13.b", "Promote mechanisms for raising capacity for effective climate change-related planning and management in least developed countries and small island developing States, including focusing on women, youth and local and marginalized communities &lt;br&gt; &lt;br&gt;* Acknowledging that the United Nations Framework Convention on Climate Change is the primary international, &lt;br&gt;intergovernmental forum for negotiating the global response to climate change.");
        sDGTargetEntityRepository.saveAndFlush(target);
        climateAction.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(climateAction);

        //sdg 14
        SDGEntity lifeBelowWater = new SDGEntity("Life Below Water", "Conserve and sustainably use the oceans, seas and marine resources for sustainable development");
        sdgEntityRepository.save(lifeBelowWater);

        //init sdg14 targets
        target = new SDGTargetEntity("Target 14.1", "By 2025, prevent and significantly reduce marine pollution of all kinds, in particular from land-based activities, including marine debris and nutrient pollution");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 14.2", "By 2020, sustainably manage and protect marine and coastal ecosystems to avoid significant adverse impacts, including by strengthening their resilience, and take action for their restoration in order to achieve healthy and productive oceans");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 14.3", "Minimize and address the impacts of ocean acidification, including through enhanced scientific cooperation at all levels");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 14.4", "By 2020, effectively regulate harvesting and end overfishing, illegal, unreported and unregulated fishing and destructive fishing practices and implement science-based management plans, in order to restore fish stocks in the shortest time feasible, at least to levels that can produce maximum sustainable yield as determined by their biological characteristics");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 14.5", "By 2020, conserve at least 10 per cent of coastal and marine areas, consistent with national and international law and based on the best available scientific information");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 14.6", "By 2020, prohibit certain forms of fisheries subsidies which contribute to overcapacity and overfishing, eliminate subsidies that contribute to illegal, unreported and unregulated fishing and refrain from introducing new such subsidies, recognizing that appropriate and effective special and differential treatment for developing and least developed countries should be an integral part of the World Trade Organization fisheries subsidies negotiation");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 14.7", "By 2030, increase the economic benefits to Small Island developing States and least developed countries from the sustainable use of marine resources, including through sustainable management of fisheries, aquaculture and tourism");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 14.a", "Increase scientific knowledge, develop research capacity and transfer marine technology, taking into account the Intergovernmental Oceanographic Commission Criteria and Guidelines on the Transfer of Marine Technology, in order to improve ocean health and to enhance the contribution of marine biodiversity to the development of developing countries, in particular small island developing States and least developed countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 14.b", "Provide access for small-scale artisanal fishers to marine resources and markets");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        target = new SDGTargetEntity("Target 14.c", "Enhance the conservation and sustainable use of oceans and their resources by implementing international law as reflected in UNCLOS, which provides the legal framework for the conservation and sustainable use of oceans and their resources, as recalled in paragraph 158 of The Future We Want");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeBelowWater.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(lifeBelowWater);

        //sdg 15
        SDGEntity lifeOnLand = new SDGEntity("Life On Land", "Protect, restore and promote sustainable use of terrestrial ecosystems, sustainably manage forests, combat desertification, and halt and reverse land degradation and halt biodiversity loss");
        sdgEntityRepository.save(lifeOnLand);

        //init sdg15 targets
        target = new SDGTargetEntity("Target 15.1", "By 2020, ensure the conservation, restoration and sustainable use of terrestrial and inland freshwater ecosystems and their services, in particular forests, wetlands, mountains and drylands, in line with obligations under international agreements");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.2", "By 2020, promote the implementation of sustainable management of all types of forests, halt deforestation, restore degraded forests and substantially increase afforestation and reforestation globally");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.3", "By 2030, combat desertification, restore degraded land and soil, including land affected by desertification, drought and floods, and strive to achieve a land degradation-neutral world");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.4", "By 2030, ensure the conservation of mountain ecosystems, including their biodiversity, in order to enhance their capacity to provide benefits that are essential for sustainable development");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.5", "Take urgent and significant action to reduce the degradation of natural habitats, halt the loss of biodiversity and, by 2020, protect and prevent the extinction of threatened species");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.6", "Promote fair and equitable sharing of the benefits arising from the utilization of genetic resources and promote appropriate access to such resources, as internationally agreed");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.7", "Take urgent action to end poaching and trafficking of protected species of flora and fauna and address both demand and supply of illegal wildlife products");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.8", "By 2020, introduce measures to prevent the introduction and significantly reduce the impact of invasive alien species on land and water ecosystems and control or eradicate the priority species");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.9", "By 2020, integrate ecosystem and biodiversity values into national and local planning, development processes, poverty reduction strategies and accounts");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.a", "Mobilize and significantly increase financial resources from all sources to conserve and sustainably use biodiversity and ecosystems");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.b", "Mobilize significant resources from all sources and at all levels to finance sustainable forest management and provide adequate incentives to developing countries to advance such management, including for conservation and reforestation");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        target = new SDGTargetEntity("Target 15.c", "Enhance global support for efforts to combat poaching and trafficking of protected species, including by increasing the capacity of local communities to pursue sustainable livelihood opportunities");
        sDGTargetEntityRepository.saveAndFlush(target);
        lifeOnLand.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(lifeOnLand);

        //sdg 16
        SDGEntity peaceJustice = new SDGEntity("Peace, Justice and Strong Institutions", "Promote peaceful and inclusive societies for sustainable development, provide access to justice for all and build effective, accountable and inclusive institutions at all levels");
        sdgEntityRepository.save(peaceJustice);

        //init sdg16 targets
        target = new SDGTargetEntity("Target 16.1", "Significantly reduce all forms of violence and related death rates everywhere");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.2", "End abuse, exploitation, trafficking and all forms of violence against and torture of children");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.3", "Promote the rule of law at the national and international levels and ensure equal access to justice for all");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.4", "By 2030, significantly reduce illicit financial and arms flows, strengthen the recovery and return of stolen assets and combat all forms of organized crime");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.5", "Substantially reduce corruption and bribery in all their forms");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.6", "Develop effective, accountable and transparent institutions at all levels");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.7", "Ensure responsive, inclusive, participatory and representative decision-making at all levels");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.8", "Broaden and strengthen the participation of developing countries in the institutions of global governance");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.9", "By 2030, provide legal identity for all, including birth registration");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.10", "Ensure public access to information and protect fundamental freedoms, in accordance with national legislation and international agreements");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.a", "Strengthen relevant national institutions, including through international cooperation, for building capacity at all levels, in particular in developing countries, to prevent violence and combat terrorism and crime");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        target = new SDGTargetEntity("Target 16.b", "Promote and enforce non-discriminatory laws and policies for sustainable development");
        sDGTargetEntityRepository.saveAndFlush(target);
        peaceJustice.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(peaceJustice);

        //sdg 17
        SDGEntity partnerships = new SDGEntity("Partnerships for the Goals", "Strengthen the means of implementation and revitalize the global partnership for sustainable development");
        sdgEntityRepository.save(partnerships);

        //init sdg17 targets
        target = new SDGTargetEntity("Target 17.1", "Strengthen domestic resource mobilization, including through international support to developing countries, to improve domestic capacity for tax and other revenue collection");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.2", "Developed countries to implement fully their official development assistance commitments, including the commitment by many developed countries to achieve the target of 0.7 per cent of ODA/GNI to developing countries and 0.15 to 0.20 per cent of ODA/GNI to least developed countries; ODA providers are encouraged to consider setting a target to provide at least 0.20 per cent of ODA/GNI to least developed countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.3", "Mobilize additional financial resources for developing countries from multiple sources");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.4", "Assist developing countries in attaining long-term debt sustainability through coordinated policies aimed at fostering debt financing, debt relief and debt restructuring, as appropriate, and address the external debt of highly indebted poor countries to reduce debt distress");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.5", "Adopt and implement investment promotion regimes for least developed countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.6", "Enhance North-South, South-South and triangular regional and international cooperation on and access to science, technology and innovation and enhance knowledge sharing on mutually agreed terms, including through improved coordination among existing mechanisms, in particular at the United Nations level, and through a global technology facilitation mechanism");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.7", "Promote the development, transfer, dissemination and diffusion of environmentally sound technologies to developing countries on favourable terms, including on concessional and preferential terms, as mutually agreed");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.8", "Fully operationalize the technology bank and science, technology and innovation capacity-building mechanism for least developed countries by 2017 and enhance the use of enabling technology, in particular information and communications technology");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.9", "Enhance international support for implementing effective and targeted capacity-building in developing countries to support national plans to implement all the sustainable development goals, including through North-South, South-South and triangular cooperation");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.10", "Promote a universal, rules-based, open, non-discriminatory and equitable multilateral trading system under the World Trade Organization, including through the conclusion of negotiations under its Doha Development Agenda");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.11", "Significantly increase the exports of developing countries, in particular with a view to doubling the least developed countries’ share of global exports by 2020");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.12", "Realize timely implementation of duty-free and quota-free market access on a lasting basis for all least developed countries, consistent with World Trade Organization decisions, including by ensuring that preferential rules of origin applicable to imports from least developed countries are transparent and simple, and contribute to facilitating market access");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.13", "Enhance global macroeconomic stability, including through policy coordination and policy coherence");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.14", "Enhance policy coherence for sustainable development");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.15", "Respect each country’s policy space and leadership to establish and implement policies for poverty eradication and sustainable development");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.16", "Enhance the global partnership for sustainable development, complemented by multi-stakeholder partnerships that mobilize and share knowledge, expertise, technology and financial resources, to support the achievement of the sustainable development goals in all countries, in particular developing countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.17", "Encourage and promote effective public, public-private and civil society partnerships, building on the experience and resourcing strategies of partnerships");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.18", "By 2020, enhance capacity-building support to developing countries, including for least developed countries and small island developing States, to increase significantly the availability of high-quality, timely and reliable data disaggregated by income, gender, age, race, ethnicity, migratory status, disability, geographic location and other characteristics relevant in national contexts");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        target = new SDGTargetEntity("Target 17.19", "By 2030, build on existing initiatives to develop measurements of progress on sustainable development that complement gross domestic product, and support statistical capacity-building in developing countries");
        sDGTargetEntityRepository.saveAndFlush(target);
        partnerships.getTargets().add(target);

        sdgEntityRepository.saveAndFlush(partnerships);
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
        bread.setResourceType(ResourceTypeEnum.FREE);

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
        classroom.setResourceType(ResourceTypeEnum.FREE);

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
        water.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(water, 3L, 5L);
//4
        ResourceEntity laptop = new ResourceEntity("Laptop", "10 Laptop for free rent", LocalDateTime.parse("2021-06-05T11:50:55"), LocalDateTime.parse("2021-07-05T11:50:55"), 10);
        laptop.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_laptop.jpg");
        laptop.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_laptop.jpg");
        laptop.getPhotos().add("https://localhost:8443/api/v1/files/init/laptop.jpg");
        laptop.setCountry("Bangladesh");
        laptop.setResourceType(ResourceTypeEnum.PAID);
        laptop.setPrice(BigDecimal.valueOf(1000.00));
        resourceService.createResource(laptop, 4L, 7L);
//5
        ResourceEntity bus = new ResourceEntity("Bus", "1 BUS free for rent for 1 day ", LocalDateTime.parse("2021-09-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10);
        bus.setResourceProfilePic("https://localhost:8443/api/v1/files/init/resource_bus.jpg");
        bus.getPhotos().add("https://localhost:8443/api/v1/files/init/resource_bus.jpg");
        bus.getPhotos().add("https://localhost:8443/api/v1/files/init/bus.jpg");
        bus.setCountry("Singapore");
        bus.setResourceType(ResourceTypeEnum.PAID);
        bus.setPrice(BigDecimal.valueOf(50.00));
        resourceService.createResource(bus, 5L, 8L);
        //6      
        ResourceEntity lamp = new ResourceEntity("Lamp", "Would like to donate 100 lamps for free to help the needy ", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 100);
        lamp.setResourceProfilePic("https://localhost:8443/api/v1/files/init/lampResource.jpg");
        lamp.getPhotos().add("https://localhost:8443/api/v1/files/init/lampResource.jpg");
        lamp.setCountry("Peru");
        lamp.setResourceType(ResourceTypeEnum.FREE);
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
        clothes.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(clothes, 7L, 9L);

        //9   
        ResourceEntity dictionary = new ResourceEntity("English Language Dictionaries", "100 used english dictionaries", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 100);
        dictionary.setResourceProfilePic("https://localhost:8443/api/v1/files/init/dictionary.jpg");
        dictionary.getPhotos().add("https://localhost:8443/api/v1/files/init/dictionary.jpg");
        dictionary.setCountry("Cambodia");
        dictionary.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(dictionary, 6L, 4L);

        //10
        ResourceEntity charger = new ResourceEntity("Charger", "50 Available Laptop Chargers", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 50);
        charger.setResourceProfilePic("https://localhost:8443/api/v1/files/init/charger.jpg");
        charger.getPhotos().add("https://localhost:8443/api/v1/files/init/charger.jpg");
        charger.setCountry("Bangladesh");
        charger.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(charger, 4L, 9L);

        //11
        ResourceEntity cereal = new ResourceEntity("Cereal", "Healthy cereal for donation", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10);
        cereal.setResourceProfilePic("https://localhost:8443/api/v1/files/init/cereal.jpg");
        cereal.getPhotos().add("https://localhost:8443/api/v1/files/init/cereal.jpg");
        cereal.setCountry("Malawi");
        cereal.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(cereal, 1L, 7L);

        //12
        ResourceEntity apple = new ResourceEntity("Apples", "150kg of fresh red Aaples", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 150);
        apple.setResourceProfilePic("https://localhost:8443/api/v1/files/init/apples.jpg");
        apple.getPhotos().add("https://localhost:8443/api/v1/files/init/apples.jpg");
        apple.setCountry("Malawi");
        apple.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(apple, 1L, 7L);

        //13
        ResourceEntity bedframe = new ResourceEntity("Donating Sturdy Used Bedframes", "50 bedframes", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 50);
        bedframe.setResourceProfilePic("https://localhost:8443/api/v1/files/init/bedframe.jpg");
        bedframe.getPhotos().add("https://localhost:8443/api/v1/files/init/bedframe.jpg");
        bedframe.setCountry("Cambodia");
        bedframe.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(bedframe, 2L, 4L);

        //14
        ResourceEntity door = new ResourceEntity("Offering Marble Doors", "50 Marble doors with doorknots", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 50);
        door.setResourceProfilePic("https://localhost:8443/api/v1/files/init/door.jpg");
        door.getPhotos().add("https://localhost:8443/api/v1/files/init/door.jpg");
        door.setCountry("Cambodia");
        door.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(door, 2L, 4L);

        //15
        ResourceEntity detergent = new ResourceEntity("Giving away detergent", "75 bottles of detergent (1kg per bottle) safe for use", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 75);
        detergent.setResourceProfilePic("https://localhost:8443/api/v1/files/init/detergent.jpg");
        detergent.getPhotos().add("https://localhost:8443/api/v1/files/init/detergent.jpg");
        detergent.setCountry("Pakistan");
        detergent.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(detergent, 3L, 6L);

        //16
        ResourceEntity dustbin = new ResourceEntity("Donating new dustbins", "60 new dustbins", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 60);
        dustbin.setResourceProfilePic("https://localhost:8443/api/v1/files/init/dustbin.jpg");
        dustbin.getPhotos().add("https://localhost:8443/api/v1/files/init/dustbin.jpg");
        dustbin.setCountry("Indonesia");
        dustbin.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(dustbin, 3L, 6L);

        //17
        ResourceEntity torchlight = new ResourceEntity("Giving away used torchlights", "25 torchlight with batteries", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 25);
        torchlight.setResourceProfilePic("https://localhost:8443/api/v1/files/init/torchlight.jpg");
        torchlight.getPhotos().add("https://localhost:8443/api/v1/files/init/torchlight.jpg");
        torchlight.setCountry("Peru");
        torchlight.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(torchlight, 4L, 10L);

        //18
        ResourceEntity pacifier = new ResourceEntity("Donating new baby pacifier", "200 brand new pacifiers", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 200);
        pacifier.setResourceProfilePic("https://localhost:8443/api/v1/files/init/pacifier.jpg");
        pacifier.getPhotos().add("https://localhost:8443/api/v1/files/init/pacifier.jpg");
        pacifier.setCountry("Indonesia");
        pacifier.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(pacifier, 4L, 5L);

        //19
        ResourceEntity vege = new ResourceEntity("Offering fresh green vegetables", "10kg of fresh vegetables that are self harvested", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10);
        vege.setResourceProfilePic("https://localhost:8443/api/v1/files/init/vegetable.jpg");
        vege.getPhotos().add("https://localhost:8443/api/v1/files/init/vegetable.jpg");
        vege.setCountry("Kenya");
        vege.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(vege, 1L, 6L);

        //20
        ResourceEntity glasses = new ResourceEntity("Donating Eyeglasses", "240 used glasses frame", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 240);
        glasses.setResourceProfilePic("https://localhost:8443/api/v1/files/init/glasses.jpg");
        glasses.getPhotos().add("https://localhost:8443/api/v1/files/init/glasses.jpg");
        glasses.setCountry("Australia");
        glasses.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(glasses, 4L, 11L);

        //21
        ResourceEntity toiletpaper = new ResourceEntity("Giving away toilet paper", "200kg of toilet paper", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 200);
        toiletpaper.setResourceProfilePic("https://localhost:8443/api/v1/files/init/toiletpaper.jpg");
        toiletpaper.getPhotos().add("https://localhost:8443/api/v1/files/init/toiletpaper.jpg");
        toiletpaper.setCountry("Nepal");
        toiletpaper.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(toiletpaper, 3L, 5L);

        ResourceEntity pears = new ResourceEntity("Pears", "20kg of fresh pears", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 20);
        pears.setResourceProfilePic("https://localhost:8443/api/v1/files/init/pears.jpg");
        pears.getPhotos().add("https://localhost:8443/api/v1/files/init/pears.jpg");
        pears.setCountry("Malawi");
        pears.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(pears, 1L, 10L);

        ResourceEntity grapes = new ResourceEntity("Grapes", "20kg of fresh grapes", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 20);
        grapes.setResourceProfilePic("https://localhost:8443/api/v1/files/init/grapes.jpg");
        grapes.getPhotos().add("https://localhost:8443/api/v1/files/init/grapes.jpg");
        grapes.setCountry("Singapore");
        grapes.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(grapes, 1L, 10L);

        ResourceEntity paintDye = new ResourceEntity("Paint Dye", "10kg of NIPPON colour dye of different colours each (White, whitewash, light blue, black, yellow)", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10);
        paintDye.setResourceProfilePic("https://localhost:8443/api/v1/files/init/paintdye.jpg");
        paintDye.getPhotos().add("https://localhost:8443/api/v1/files/init/paintdye.jpg");
        paintDye.setCountry("Malaysia");
        paintDye.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(paintDye, 4L, 4L);

        ResourceEntity bodyWash = new ResourceEntity("Body Wash", "100 bottles of body wash", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 100);
        bodyWash.setResourceProfilePic("https://localhost:8443/api/v1/files/init/bodywash.png");
        bodyWash.getPhotos().add("https://localhost:8443/api/v1/files/init/bodywash.png");
        bodyWash.setCountry("Malaysia");
        bodyWash.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(bodyWash, 9L, 6L);

        ResourceEntity garbageBag = new ResourceEntity("Garbage Bags", "10,000 new garbage bags", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 10000);
        garbageBag.setResourceProfilePic("https://localhost:8443/api/v1/files/init/garbageBag.jpg");
        garbageBag.getPhotos().add("https://localhost:8443/api/v1/files/init/garbageBag.jpg");
        garbageBag.setCountry("Thailand");
        garbageBag.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(garbageBag, 4L, 7L);

        ResourceEntity headphone = new ResourceEntity("Headphones", "250 brand new SONY headphones", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 200);
        headphone.setResourceProfilePic("https://localhost:8443/api/v1/files/init/headphone.jpeg");
        headphone.getPhotos().add("https://localhost:8443/api/v1/files/init/headphone.jpeg");
        headphone.setCountry("Singapore");
        headphone.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(headphone, 4L, 12L);

        ResourceEntity computerMouse = new ResourceEntity("Computer Mouse", "170 used wired computer mouse, can be easily connected with computer/laptops via USB", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 170);
        computerMouse.setResourceProfilePic("https://localhost:8443/api/v1/files/init/computerMouse.jpg");
        computerMouse.getPhotos().add("https://localhost:8443/api/v1/files/init/computerMouse.jpg");
        computerMouse.setCountry("Singapore");
        computerMouse.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(computerMouse, 4L, 12L);

        ResourceEntity keyboard = new ResourceEntity("Keyboard", "188 used Keyboards, can be easily connected with computer/laptops via USB or Bluetooth", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 188);
        keyboard.setResourceProfilePic("https://localhost:8443/api/v1/files/init/keyboard.jpg");
        keyboard.getPhotos().add("https://localhost:8443/api/v1/files/init/keyboard.jpg");
        keyboard.setCountry("Singapore");
        keyboard.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(keyboard, 4L, 12L);

        ResourceEntity floorPlan = new ResourceEntity("Floor Plans", "Floors Plans available, contact me for more details", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 28);
        floorPlan.setResourceProfilePic("https://localhost:8443/api/v1/files/init/floorplan.png");
        floorPlan.getPhotos().add("https://localhost:8443/api/v1/files/init/floorplan.png");
        floorPlan.setCountry("Cambodia");
        floorPlan.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(floorPlan, 8L, 10L);

        ResourceEntity shovel = new ResourceEntity("Shovel", "Shovel available, contact me for more details", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 28);
        shovel.setResourceProfilePic("https://localhost:8443/api/v1/files/init/shovel.jpg");
        shovel.getPhotos().add("https://localhost:8443/api/v1/files/init/shovel.jpg");
        shovel.setCountry("Cambodia");
        shovel.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(shovel, 4L, 10L);

        ResourceEntity spade = new ResourceEntity("Gardening Spade", "Gardening Spade available", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 28);
        spade.setResourceProfilePic("https://localhost:8443/api/v1/files/init/spade.jpg");
        spade.getPhotos().add("https://localhost:8443/api/v1/files/init/spade.jpg");
        spade.setCountry("Singapore");
        spade.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(spade, 4L, 10L);

        ResourceEntity sofa = new ResourceEntity("Sofa", "5 sets of used sofa available for donation", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 1000);
        sofa.setResourceProfilePic("https://localhost:8443/api/v1/files/init/sofa.jpg");
        sofa.getPhotos().add("https://localhost:8443/api/v1/files/init/sofa.jpg");
        sofa.setCountry("Singapore");
        sofa.setResourceType(ResourceTypeEnum.FREE);
        resourceService.createResource(sofa, 9L, 7L);

        ResourceEntity chair = new ResourceEntity("Chair", "500 Brand New Chairs", LocalDateTime.parse("2020-10-20T11:50:55"), LocalDateTime.parse("2021-09-21T11:50:55"), 500);
        chair.setResourceProfilePic("https://localhost:8443/api/v1/files/init/chair.jpg");
        chair.getPhotos().add("https://localhost:8443/api/v1/files/init/chair.jpg");
        chair.setCountry("Singapore");
        chair.setResourceType(ResourceTypeEnum.FREE);
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

        long[] sdgTargetIds = LongStream.rangeClosed(1, 5).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 1L, projectEntity1.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(10, 12).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 2L, projectEntity1.getProjectId());

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

        try {
            AnnouncementVO announcementVO = new AnnouncementVO();
            announcementVO.setContent("We are going to launch our project soon");
            announcementVO.setCreatorId(5L);
            announcementVO.setProjectId(1L);
            announcementVO.setTitle("title");
            AnnouncementEntity announcementEntity = announcementService.createProjectPublicAnnouncement(announcementVO);
        } catch (CreateAnnouncementException ex) {
            ex.printStackTrace();
        }

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

        sdgTargetIds = LongStream.rangeClosed(30, 35).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 4L, projectEntity2.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(41, 44).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 5L, projectEntity2.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(5, 7).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 1L, projectEntity3.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(11, 15).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 2L, projectEntity3.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(18, 21).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 3L, projectEntity4.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(29, 36).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 4L, projectEntity4.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(41, 47).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 5L, projectEntity4.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(17, 21).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 3L, projectEntity5.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(48, 51).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 6L, projectEntity5.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(112, 115).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 13L, projectEntity7.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(92, 95).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 11L, projectEntity7.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(18, 25).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 3L, projectEntity8.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(32, 37).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 4L, projectEntity8.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(39, 44).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 5L, projectEntity8.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(1, 3).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 1L, projectEntity9.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(16, 25).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 3L, projectEntity9.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(32, 35).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 4L, projectEntity9.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(1, 6).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 1L, projectEntity10.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(30, 36).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 4L, projectEntity10.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(96, 99).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 11L, projectEntity11.getProjectId());

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

        long[] sdgTargetIds = LongStream.rangeClosed(51, 55).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 6L, completedProject.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(94, 99).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 11L, completedProject.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(120, 125).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 14L, completedProject.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(91, 99).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 11L, completedProject.getProjectId());

        sdgTargetIds = LongStream.rangeClosed(101, 104).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 12L, completedProject.getProjectId());

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

 /* start of completed project 3 */
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

        sdgTargetIds = LongStream.rangeClosed(41, 46).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 5L, completedProject.getProjectId());

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

        sdgTargetIds = LongStream.rangeClosed(31, 34).toArray();
        associateSDGTargetsWithProject(sdgTargetIds, 4L, completedProject.getProjectId());

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
            e.printStackTrace();
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
        try {
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
        } catch (LikePostException ex) {
            ex.printStackTrace();
        }
    }

    private void setNotifications(ProfileEntity profileEntity) {
        announcementService.setNotifications(profileEntity);
    }

    private void associateSDGTargetsWithProfile(long[] sdgTargetIds, Long sdgId, Long accountId) {
        //find the sdg
        SDGEntity sdg = sdgEntityRepository.findBySdgId(sdgId);

        //find the list of sdgTargets instances
        List<SDGTargetEntity> sdgTargetlist = sDGTargetEntityRepository.findSDGTargetsByIds(sdgTargetIds);

        //find the profile
        ProfileEntity profile = profileEntityRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException(accountId));

        SelectedTargetEntity s = new SelectedTargetEntity();

        s.setProfile(profile);
        s.setSdg(sdg);
        s.getSdgTargets().addAll(sdgTargetlist);

        selectedTargetEntityRepository.saveAndFlush(s);

        //set bidirectional association
        profile.getSelectedTargets().add(s);
        profileEntityRepository.saveAndFlush(profile);
    }

    private void associateSDGTargetsWithProject(long[] sdgTargetIds, Long sdgId, Long projectId) {
        //find the sdg
        SDGEntity sdg = sdgEntityRepository.findBySdgId(sdgId);

        //find the list of sdgTargets instances
        List<SDGTargetEntity> sdgTargetlist = sDGTargetEntityRepository.findSDGTargetsByIds(sdgTargetIds);

        ProjectEntity project = null;
        //find the project
        try {
            project = projectService.retrieveProjectById(projectId);
        } catch (ProjectNotFoundException ex) {
            System.err.println("error in datainit: associateSDGTargetsWithProject() method");
        }

        SelectedTargetEntity s = new SelectedTargetEntity();

        s.setProject(project);
        s.setSdg(sdg);
        s.getSdgTargets().addAll(sdgTargetlist);

        selectedTargetEntityRepository.saveAndFlush(s);

        //set bidirectional association
        project.getSelectedTargets().add(s);
        projectEntityRepository.saveAndFlush(project);
    }

    public void initCompetitions() {
        try {
            CompetitionVO competitionVO = new CompetitionVO();
            competitionVO.setCompetitionDescription("Competition description");
            competitionVO.setCompetitionTitle("competitionTitle");
            competitionVO.setCompetitionStatus(CompetitionStatusEnum.ACTIVE);
            competitionVO.setStartDate(LocalDateTime.parse("2017-02-07T11:45:55"));
            competitionVO.setEndDate(LocalDateTime.parse("2018-02-07T11:45:55"));
            competitionVO.setPrizeMoney(BigDecimal.valueOf(1000.00));
            CompetitionEntity competition1 = competitionService.createCompetition(competitionVO);

            competitionService.joinCompetition(competition1.getCompetitionId(), 2L, 5L);
            competitionService.joinCompetition(competition1.getCompetitionId(), 3L, 9L);

            competitionVO.setCompetitionDescription("SHINE in ONE month");
            competitionVO.setCompetitionTitle("Best Project Competition");
            competitionVO.setCompetitionStatus(CompetitionStatusEnum.INACTIVE);
            competitionVO.setStartDate(LocalDateTime.parse("2021-02-07T11:45:55"));
            competitionVO.setEndDate(LocalDateTime.parse("2022-02-07T11:45:55"));
            competitionVO.setPrizeMoney(BigDecimal.valueOf(1000.00));
            CompetitionEntity competition2 = competitionService.createCompetition(competitionVO);
            competitionService.joinCompetition(competition2.getCompetitionId(), 6L, 7L);

            competitionVO.setCompetitionDescription("Are we saving the world or saving human beings?");
            competitionVO.setCompetitionTitle("Best Enviromental Project");
            competitionVO.setCompetitionStatus(CompetitionStatusEnum.PAST);
            competitionVO.setStartDate(LocalDateTime.parse("2019-02-07T11:45:55"));
            competitionVO.setEndDate(LocalDateTime.parse("2020-02-07T11:45:55"));
            competitionVO.setPrizeMoney(BigDecimal.valueOf(1000.00));
            CompetitionEntity competition3 = competitionService.createCompetition(competitionVO);
            competitionService.joinCompetition(competition3.getCompetitionId(), 9L, 8L);
        } catch (ProjectNotFoundException ex) {

            ex.printStackTrace();
        }
    }

    private void initKanbanBoard() {
        KanbanBoardVO vo = new KanbanBoardVO();
        vo.setChannelUid("123");
        vo.setProjectId(1L);
        kanbanBoardService.createKanbanBoard(vo);
    }
}
