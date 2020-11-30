/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.service;

import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.exception.SDGEntityNotFoundException;
import com.is4103.matchub.repository.SDGEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author ngjin
 */
@Service
public class SDGServiceImpl implements SDGService {

    @Autowired
    private SDGEntityRepository sdgEntityRepository;

    @Override
    public Page<SDGEntity> getAllSdgs(Pageable pageable) {
        return sdgEntityRepository.findAll(pageable);
    }

    @Override
    public SDGEntity getSdgBySdgId(Long sdgId) {
        return sdgEntityRepository.findById(sdgId)
                .orElseThrow(() -> new SDGEntityNotFoundException("SDG with id " + sdgId + " does not exist"));
    }
}
