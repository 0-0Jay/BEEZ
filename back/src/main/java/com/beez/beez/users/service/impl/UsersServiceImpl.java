package com.beez.beez.users.service.impl;

import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserSearchRequest;
import com.beez.beez.users.mapper.UsersMapper;
import com.beez.beez.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class UsersServiceImpl implements UsersService {

  private final UsersMapper usersMapper;

  @Override
  public List<UserListResponse> findAllUsers(UserSearchRequest search) {
    return usersMapper.findAllUsers(search);
  }

}
