/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.ResourceRequestEntity;
import com.is4103.matchub.exception.CreateResourceRequestException;
import com.is4103.matchub.exception.DeleteResourceRequestException;
import com.is4103.matchub.exception.ResourceRequestNotFoundException;
import com.is4103.matchub.exception.RespondToResourceRequestException;
import com.is4103.matchub.vo.ResourceRequestCreateVO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author longluqian
 */
public interface ResourceRequestService {

    public ResourceRequestEntity createResourceRequestProjectOwner(ResourceRequestCreateVO vo) throws CreateResourceRequestException;

    public ResourceRequestEntity createResourceRequestResourceOwner(Long projectId, Long requestorId, Long resourceId, Integer units) throws CreateResourceRequestException;

    public ResourceRequestEntity createResourceRequestResourceOwner(ResourceRequestCreateVO vo) throws CreateResourceRequestException;

    public void terminateResourceRequest(Long requestId, Long terminatorId) throws DeleteResourceRequestException;

    public ResourceRequestEntity getResourceRequestById(Long requestId) throws ResourceRequestNotFoundException;

    public Page<ResourceRequestEntity> getResourceRequestByRequestorId(Long requestorId, Pageable pageable);

    public Page<ResourceRequestEntity> getResourceRequestByResourceId(Long resourceId, Pageable pageable);

    public Page<ResourceRequestEntity> getResourceRequestByProjectId(Long projectId, Pageable pageable);

    public ResourceRequestEntity respondToResourceRequest(Long requestId, Long responderId, boolean response) throws RespondToResourceRequestException;

    public List<ResourceRequestEntity> getAllIncomingResourceDonationRequests(Long userId);

    public List<ResourceRequestEntity> getAllOutgoingResourceRequests(Long userId);

    public List<ResourceRequestEntity> getAllIncomingResourceRequests(Long userId);

    public List<ResourceRequestEntity> getAllOutgoingDonationRequests(Long userId);

}
