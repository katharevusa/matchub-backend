/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.DonationEntity;
import com.is4103.matchub.entity.DonationOptionEntity;
import com.is4103.matchub.entity.FundCampaignEntity;
import com.is4103.matchub.entity.ProfileEntity;
import com.is4103.matchub.entity.ProjectEntity;
import com.is4103.matchub.exception.CreateFundCampaignException;
import com.is4103.matchub.exception.DeleteDonationOptionException;
import com.is4103.matchub.exception.DeleteFundCampaignException;
import com.is4103.matchub.exception.DonationOptionNotFoundException;
import com.is4103.matchub.exception.FundCampaignNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.repository.DonationEntityRepository;
import com.is4103.matchub.repository.DonationOptionEntityRepository;
import com.is4103.matchub.repository.FundCampaignEntityRepository;
import com.is4103.matchub.repository.ProfileEntityRepository;
import com.is4103.matchub.repository.ProjectEntityRepository;
import com.is4103.matchub.vo.CreateDonationOptionVO;
import com.is4103.matchub.vo.CreateFundCampaignVO;
import com.is4103.matchub.vo.UpdateDonationOptionVO;
import com.stripe.model.PaymentIntent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author longluqian
 */
@Service
public class FundCampaignServiceImpl implements FundCampaignService {

    // create Fund Campaign
    @Autowired
    FundCampaignEntityRepository fundCampaignEntityRepository;

    @Autowired
    DonationOptionEntityRepository donationOptionEntityRepository;

    @Autowired
    ProfileEntityRepository profileEntityRepository;

    @Autowired
    ProjectEntityRepository projectEntityRepository;

    @Autowired
    DonationEntityRepository donationEntityRepository;

    @Autowired
    StripeService stripeService;

    @Override
    public FundCampaignEntity createFundCampaign(CreateFundCampaignVO createFundCampaignVO) throws UserNotFoundException, CreateFundCampaignException, ProjectNotFoundException {
        FundCampaignEntity fundCampaignEntity = new FundCampaignEntity();
        createFundCampaignVO.createFundCampaign(fundCampaignEntity);
        ProfileEntity profile = profileEntityRepository.findById(fundCampaignEntity.getPayeeId())
                .orElseThrow(() -> new UserNotFoundException(createFundCampaignVO.getPayeeId()));
        if (profile.getStripeAccountUid()==null) {
            throw new CreateFundCampaignException("The payee has not set up stripe account");

        } else if (!profile.getStripeAccountChargesEnabled()) {
            throw new CreateFundCampaignException("The stripe account of this payee has not been activated yet");
        }

        ProjectEntity project = projectEntityRepository.findById(createFundCampaignVO.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Unable to find project"));

        fundCampaignEntity.setStripeAccountUid(profile.getStripeAccountUid());
        project.getFundsCampaign().add(fundCampaignEntity);
        fundCampaignEntity = fundCampaignEntityRepository.saveAndFlush(fundCampaignEntity);

        // set default donation option
        DonationOptionEntity donationOption = new DonationOptionEntity();
        donationOption.setAmount(BigDecimal.valueOf(10.00));
        donationOption.setOptionDescription("Default donation option");
        donationOption.setFundCampaign(fundCampaignEntity);

        donationOption = donationOptionEntityRepository.saveAndFlush(donationOption);

        fundCampaignEntity.getDonationOptions().add(donationOption);
        return fundCampaignEntityRepository.saveAndFlush(fundCampaignEntity);

    }

    @Override
    public boolean activateFundCampaign(Long fundCampaignId) throws FundCampaignNotFoundException {
        FundCampaignEntity fundCampaignEntity = fundCampaignEntityRepository.findById(fundCampaignId).orElseThrow(() -> new FundCampaignNotFoundException("Unable to find fund campaign"));
        fundCampaignEntity.setActivated(true);
        return fundCampaignEntityRepository.saveAndFlush(fundCampaignEntity).isActivated();
    }

    @Override
    public boolean deactivateFundCampaign(Long fundCampaignId) throws FundCampaignNotFoundException {
        FundCampaignEntity fundCampaignEntity = fundCampaignEntityRepository.findById(fundCampaignId).orElseThrow(() -> new FundCampaignNotFoundException("Unable to find fund campaign"));
        fundCampaignEntity.setActivated(false);
        return fundCampaignEntityRepository.saveAndFlush(fundCampaignEntity).isActivated();
    }

    @Override
    public void deleteFundCampaign(Long fundCampaignId) throws FundCampaignNotFoundException, DeleteFundCampaignException {
        FundCampaignEntity fundCampaignEntity = fundCampaignEntityRepository.findById(fundCampaignId).orElseThrow(() -> new FundCampaignNotFoundException("Unable to find fund campaign"));
        List<DonationOptionEntity> options = fundCampaignEntity.getDonationOptions();
        for (DonationOptionEntity d : options) {
            if (!d.getDonations().isEmpty()) {
                throw new DeleteFundCampaignException("This fund campaign already has some donations, can not be deleted");
            }
        }
        fundCampaignEntity.setDonationOptions(new ArrayList<>());
        donationOptionEntityRepository.deleteAll(options);
        fundCampaignEntityRepository.delete(fundCampaignEntity);
        donationEntityRepository.flush();
        fundCampaignEntityRepository.flush();

    }

    @Override
    public DonationOptionEntity createDonationOption(CreateDonationOptionVO vo) throws FundCampaignNotFoundException {
        DonationOptionEntity donation = new DonationOptionEntity();
        vo.createDonationOption(donation);

        FundCampaignEntity fundCampaignEntity = fundCampaignEntityRepository.findById(vo.getFundCampaignId()).orElseThrow(() -> new FundCampaignNotFoundException("Unable to find fund campaign"));
        fundCampaignEntity.getDonationOptions().add(donation);
        donation.setFundCampaign(fundCampaignEntity);
        return donationOptionEntityRepository.saveAndFlush(donation);
    }

    @Override
    public DonationOptionEntity updateDonationOption(UpdateDonationOptionVO vo) throws DonationOptionNotFoundException {
        DonationOptionEntity donationOption = donationOptionEntityRepository.findById(vo.getDonationOptionId()).orElseThrow(() -> new DonationOptionNotFoundException("Unable to find donation option"));
        vo.updateDonationOption(donationOption);
        return donationOptionEntityRepository.saveAndFlush(donationOption);

    }

    @Override
    public void deleteDonationOption(Long donationOptionId) throws DonationOptionNotFoundException, DeleteDonationOptionException {
        DonationOptionEntity donationOption = donationOptionEntityRepository.findById(donationOptionId).orElseThrow(() -> new DonationOptionNotFoundException("Unable to find donation option"));
        if (!donationOption.getDonations().isEmpty()) {
            throw new DeleteDonationOptionException("Unable to delete donation options because there are some donations associated with this");
        }

        donationOptionEntityRepository.delete(donationOption);

    }

    @Override
    public void createDonation(String payerEmail, PaymentIntent paymentIntent) throws UserNotFoundException, DonationOptionNotFoundException {
        DonationEntity donation = new DonationEntity();
        donation.setDonatedAmount(BigDecimal.valueOf(paymentIntent.getAmount()));
        donation.setDonationTime(LocalDateTime.now());
        ProfileEntity donator = profileEntityRepository.findByEmail(payerEmail).orElseThrow(() -> new UserNotFoundException(payerEmail));
        DonationOptionEntity donationOption = donationOptionEntityRepository.findById(Long.parseLong(paymentIntent.getMetadata().get("donation_option_id"))).orElseThrow(() -> new DonationOptionNotFoundException());
        // associate donation with donator

        donation.setDonator(donator);
        donator.getDonations().add(donation);
        // associate donation with donation options
        donation.setDonationOption(donationOption);
        donationOption.getDonations().add(donation);

        donationEntityRepository.save(donation);

    }
    
    @Override
    public List<DonationEntity> getPastDonationsByUserId(Long userId){
         ProfileEntity profile = profileEntityRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
         
         return profile.getDonations();
    }
    
    @Override
    public List<DonationEntity> getDonationsByOptionId(Long optionId)throws DonationOptionNotFoundException{
        DonationOptionEntity donationOption =  donationOptionEntityRepository.findById(optionId).orElseThrow(()-> new DonationOptionNotFoundException("Donation option not found"));
        return donationOption.getDonations();
    }
    
    @Override
    public List<FundCampaignEntity> getFundCampaignsByProjectId(Long projectId)throws ProjectNotFoundException{
        ProjectEntity project = projectEntityRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Unable to find project"));
        return project.getFundsCampaign();
    }
    
    @Override
    public List<FundCampaignEntity> getFundCampaignsByPayeeId(Long payeeId )throws UserNotFoundException{
        ProfileEntity profile = profileEntityRepository.findById(payeeId)
                .orElseThrow(() -> new UserNotFoundException(payeeId));
        
        List<FundCampaignEntity> fundCampaigns = fundCampaignEntityRepository.getFundCampaignsByPayeeId(payeeId);
        for(FundCampaignEntity f : fundCampaigns){
            f.getDonationOptions();
        }
        return fundCampaigns;
        
    }
    
    @Override
    public FundCampaignEntity getFundCampaignByFundCampaignId(Long fundCampaignId)throws  FundCampaignNotFoundException{
        FundCampaignEntity fundCampaignEntity = fundCampaignEntityRepository.findById(fundCampaignId).orElseThrow(() -> new FundCampaignNotFoundException() );
        return fundCampaignEntity;
    }
    

}
