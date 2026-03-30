package com.beez.beez.users.repository;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Users implements UserDetails {


  private String id;          // 사원번호
  private String email;
  private String password;
  private String name;
  private String status;
  private LocalDateTime createdOn;

  // 권한 리스트
  private List<String> roles = new ArrayList<>();

  // UserDetails 구현
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // 권한 리스트에 담긴 문자열들을 시큐리티 권한 객체로 변환
    if (this.roles == null || this.roles.isEmpty()) return Collections.emptyList();

    return this.roles.stream()
      .map(SimpleGrantedAuthority::new)
      .collect(Collectors.toList());
  }

  @Override
  public String getUsername() { return this.id; }

  public void setRoles(List<String> roles) {
    this.roles = (roles == null) ? new ArrayList<>() : new ArrayList<>(roles);
  }

//  @Override
//  public boolean isAccountNonExpired() { return true; }

  @Override
  public boolean isAccountNonLocked() {
    // 활성화 상태(H1)일 때만 true(잠기지 않음)를 반환
    return "H1".equals(this.status);
  }

//  @Override
//  public boolean isCredentialsNonExpired() { return true; }
//  @Override
//  public boolean isEnabled() { return true; }
}