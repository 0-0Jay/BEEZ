package com.beez.beez.users.service;

import com.beez.beez.permission.mapper.PermissionMapper;
import com.beez.beez.users.mapper.UsersMapper;
import com.beez.beez.users.repository.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UsersMapper usersMapper;
  private final PermissionMapper permissionMapper;

  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
//    System.out.println("ID 확인: " + id);
    // 사용자가 로그인 시 입력한 사원번호로 DB를 통해 찾음
    Optional<Users> usersOptional = usersMapper.findById(id);

    return usersOptional.map(users -> {
      List<String> permList = permissionMapper.findPermIdsByRoleId(users.getRole());
      List<GrantedAuthority> list = permList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

      // 역할도 담기
      if (users.getRole() != null) {
        list.add(new SimpleGrantedAuthority(users.getRole()));
      }

      users.setAuthorities(list);

      return users;
    }).orElseThrow(() -> new UsernameNotFoundException(("존재하지 않는 사원번호입니다: " + id)));
  }
}
