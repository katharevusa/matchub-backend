package com.is4103.matchub.service;

import com.is4103.matchub.entity.AccountEntity;
import com.is4103.matchub.entity.IndividualEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.OrganisationEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.enumeration.GenderEnum;
import com.is4103.matchub.enumeration.ProjectStatusEnum;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.is4103.matchub.repository.AccountEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.repository.SDGEntityRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @Transactional
    public void init() {
        initSDG();
        initUsers();
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
                        ProjectEntity projectEntity = new ProjectEntity("Project 1 title", "Project 1 description", "Singapore", LocalDateTime.now(), LocalDateTime.parse("2018-05-05T11:50:55"));
                        projectService.createProject(projectEntity, account.getAccountId());

                    } else {
                        account = accountEntityRepository.save(new OrganisationEntity(a + "@gmail.com", passwordEncoder.encode("password"), "NUS", "description", "address"));
                        account.getRoles().add(ProfileEntity.ROLE_USER);
                        ProjectEntity projectEntity = new ProjectEntity("Project 2 title", "Project 2 description", "China", LocalDateTime.now(), LocalDateTime.parse("2019-06-05T11:50:55"));
                        projectService.createProject(projectEntity, account.getAccountId());

                    }
                    accountEntityRepository.save(account);
                });
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
}
