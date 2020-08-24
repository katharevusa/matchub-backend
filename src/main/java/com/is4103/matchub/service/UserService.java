package com.is4103.matchub.service;

import com.is4103.matchub.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserVO get(Long id);

    UserVO get(String username);

    void delete(Long id);

    Page<UserVO> search(String search, Pageable pageable);

}
