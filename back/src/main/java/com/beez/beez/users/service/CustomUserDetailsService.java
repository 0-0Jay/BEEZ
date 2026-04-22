package com.beez.beez.users.service;

import com.beez.beez.permission.mapper.PermissionMapper;
import com.beez.beez.users.dto.CustomUserDetails;
import com.beez.beez.users.mapper.UsersMapper;
import com.beez.beez.users.repository.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    return usersMapper.findById(id).map(user -> {

      if (user.getRole() == null) {
        throw new IllegalStateException("사용자에 해당 역할이 없습니다.");
      }

      // 권한 및 역할 리스트 생성
      List<GrantedAuthority> authorities = new ArrayList<>();

      // 역할에 매핑된 상세 권한 조회
      List<String> permList = permissionMapper.findPermIdsByRoleId(user.getRole());
      permList.forEach(permId -> authorities.add(new SimpleGrantedAuthority(permId)));

      // 역할코드도 권한에 추가
      authorities.add(new SimpleGrantedAuthority(user.getRole()));

      return new CustomUserDetails(user, authorities);

    }).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사원번호입니다."));

  }
}
