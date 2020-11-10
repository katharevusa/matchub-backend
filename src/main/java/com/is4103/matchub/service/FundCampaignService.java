/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.DonationEntity;
import com.is4103.matchub.entity.DonationOptionEntity;
import com.is4103.matchub.entity.FundCampaignEntity;
import com.is4103.matchub.exception.CreateFundCampaignException;
import com.is4103.matchub.exception.DeleteDonationOptionException;
import com.is4103.matchub.exception.DeleteFundCampaignException;
import com.is4103.matchub.exception.DonationOptionNotFoundException;
import com.is4103.matchub.exception.FundCampaignNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.UserNotFoundException;
import com.is4103.matchub.vo.CreateDonationOptionVO;
import com.is4103.matchub.vo.CreateFundCampaignVO;
import com.is4103.matchub.vo.UpdateDonationOptionVO;
import com.is4103.matchub.vo.UpdateFundCampaignVO;
import com.stripe.model.PaymentIntent;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author longluqian
 */
public interface FundCampaignService {

    public void createDonation(String payerEmail, PaymentIntent paymentIntent) throws UserNotFoundException, DonationOptionNotFoundException;

    public void deleteDonationOption(Long donationOptionId) throws DonationOptionNotFoundException, DeleteDonationOptionException;

    public DonationOptionEntity updateDonationOption(UpdateDonationOptionVO vo) throws DonationOptionNotFoundException;

    public DonationOptionEntity createDonationOption(CreateDonationOptionVO vo) throws FundCampaignNotFoundException;

    public void deleteFundCampaign(Long fundCampaignId) throws FundCampaignNotFoundException, DeleteFundCampaignException;

    public boolean deactivateFundCampaign(Long fundCampaignId) throws FundCampaignNotFoundException;

    public boolean activateFundCampaign(Long fundCampaignId) throws FundCampaignNotFoundException;

    public FundCampaignEntity createFundCampaign(CreateFundCampaignVO createFundCampaignVO) throws UserNotFoundException, CreateFundCampaignException, ProjectNotFoundException;

    public List<DonationEntity> getPastDonationsByUserId(Long userId);

    public List<DonationEntity> getDonationsByOptionId(Long optionId) throws DonationOptionNotFoundException;

    public List<FundCampaignEntity> getFundCampaignsByProjectId(Long projectId) throws ProjectNotFoundException;

    public List<FundCampaignEntity> getFundCampaignsByPayeeId(Long payeeId) throws UserNotFoundException;

    public FundCampaignEntity getFundCampaignByFundCampaignId(Long fundCampaignId) throws FundCampaignNotFoundException;

    public List<FundCampaignEntity> getAllFundCampaignEntity();

    public FundCampaignEntity updateFundCampaign(UpdateFundCampaignVO vo) throws FundCampaignNotFoundException;

    public Page<FundCampaignEntity> searchCampaign(String key, Pageable pageable);
}
