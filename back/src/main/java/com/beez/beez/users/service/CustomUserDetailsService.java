package com.beez.beez.users.service;

import com.beez.beez.users.mapper.UsersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UsersMapper usersMapper;

  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

    // 사용자가 로그인 시 입력한 사원번호로 DB를 통해 찾음
    return usersMapper.findById(id)
      .orElseThrow(() -> new UsernameNotFoundException(("존재하지 않는 사원번호입니다: " + id)));
  }
}
