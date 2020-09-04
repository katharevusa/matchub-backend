package com.is4103.matchub.service;

import com.is4103.matchub.vo.IndividualCreateVO;
import com.is4103.matchub.vo.OrganisationCreateVO;
import com.is4103.matchub.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserVO createIndividual(IndividualCreateVO vo);

    UserVO createOrganisation(OrganisationCreateVO vo);

    UserVO get(Long id);

    UserVO get(String username);

    void delete(Long id);

    Page<UserVO> search(String search, Pageable pageable);

}
