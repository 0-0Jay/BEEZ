package com.beez.beez.users.repository;

import lombok.*;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails {


  private String id;          // 사원번호
  private String email;
  private String password;
  private String name;
  private String status;
  private LocalDateTime createdOn;

  // 역할 리스트
  private String role;

  // 권한 리스트
  @Builder.Default
  private List<GrantedAuthority> authorities = new ArrayList<>();

  // UserDetails 구현
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getUsername() { return this.id; }

//  @Override
//  public boolean isAccountNonExpired() { return true; }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

//  @Override
//  public boolean isCredentialsNonExpired() { return true; }
  @Override
  public boolean isEnabled() {
    // 활성화 상태(H1)일 때만 true를 반환
    return "H1".equals(this.status);
  }

}