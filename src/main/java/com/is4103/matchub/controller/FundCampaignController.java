/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.controller;

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
import com.is4103.matchub.service.FundCampaignService;
import com.is4103.matchub.vo.CreateDonationOptionVO;
import com.is4103.matchub.vo.CreateFundCampaignVO;
import com.is4103.matchub.vo.UpdateDonationOptionVO;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author longluqian
 */
@RestController
@RequestMapping("/authenticated")
public class FundCampaignController {

    @Autowired
    FundCampaignService fundCampaignService;

    @RequestMapping(method = RequestMethod.POST, value = "/createFundCampaign")
    public FundCampaignEntity createFundCampaign(@Valid @RequestBody CreateFundCampaignVO createFundCampaignVO) throws UserNotFoundException, CreateFundCampaignException, ProjectNotFoundException {
        return fundCampaignService.createFundCampaign(createFundCampaignVO);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteFundCampaign")
    public void deleteFundCampaign(@RequestParam(value = "fundCampaignId", required = true) Long fundCampaignId) throws FundCampaignNotFoundException, DeleteFundCampaignException {
        fundCampaignService.deleteFundCampaign(fundCampaignId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/deactivateFundCampaign")
    public boolean deactivateFundCampaign(@RequestParam(value = "fundCampaignId", required = true) Long fundCampaignId) throws FundCampaignNotFoundException {
        return fundCampaignService.deactivateFundCampaign(fundCampaignId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/activateFundCampaign")
    public boolean activateFundCampaign(@RequestParam(value = "fundCampaignId", required = true) Long fundCampaignId) throws FundCampaignNotFoundException {
        return fundCampaignService.activateFundCampaign(fundCampaignId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createDonationOption")
    public DonationOptionEntity createDonationOption(@Valid @RequestBody CreateDonationOptionVO vo) throws FundCampaignNotFoundException {
        return fundCampaignService.createDonationOption(vo);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteDonationOption")
    public void deleteDonationOption(@RequestParam(value = "donationOptionId", required = true) Long donationOptionId) throws DonationOptionNotFoundException, DeleteDonationOptionException {
        fundCampaignService.deleteDonationOption(donationOptionId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateDonationOption")
    public DonationOptionEntity updateDonationOption(@Valid @RequestBody UpdateDonationOptionVO vo) throws DonationOptionNotFoundException {
        return fundCampaignService.updateDonationOption(vo);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPastDonationsByUserId")
    public List<DonationEntity> getPastDonationsByUserId(@RequestParam(value = "userId", required = true) Long userId) {
        return fundCampaignService.getPastDonationsByUserId(userId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getDonationsByOptionId")
    public List<DonationEntity> getDonationsByOptionId(@RequestParam(value = "optionId", required = true) Long optionId) throws DonationOptionNotFoundException {
        return fundCampaignService.getDonationsByOptionId(optionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getFundCampaignsByProjectId")
    public List<FundCampaignEntity> getFundCampaignsByProjectId(@RequestParam(value = "projectId", required = true) Long projectId) throws ProjectNotFoundException {
        return fundCampaignService.getFundCampaignsByProjectId(projectId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getFundCampaignsByPayeeId")
    public List<FundCampaignEntity> getFundCampaignsByPayeeId(@RequestParam(value = "payeeId", required = true) Long payeeId) throws UserNotFoundException {
        return fundCampaignService.getFundCampaignsByPayeeId(payeeId);
    }
    @RequestMapping(method = RequestMethod.GET, value = "/getFundCampaignByFundCampaignId")
    public FundCampaignEntity getFundCampaignByFundCampaignId(@RequestParam(value = "fundCampaignId", required = true)Long fundCampaignId)throws  FundCampaignNotFoundException{
        return fundCampaignService.getFundCampaignByFundCampaignId(fundCampaignId);
    }
    
     
    @RequestMapping(method = RequestMethod.GET, value = "/getAllFundCampaignEntity")
    public List<FundCampaignEntity> getAllFundCampaignEntity(){
        return fundCampaignService.getAllFundCampaignEntity();
    }

}
