package com.beez.beez.users.dto;

import com.beez.beez.users.repository.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class CustomUserDetails implements UserDetails {


  private final Users user;
  private List<GrantedAuthority> authorities;

  public CustomUserDetails(Users user, List<GrantedAuthority> authorities) {
    this.user = user;
    this.authorities = (authorities != null) ? authorities : new ArrayList<>();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getId(); // 사원번호(ID)
  }

  @Override
  public boolean isEnabled() {
    // 엔티티의 상태값(H1)을 체크
    return "H1".equals(user.getStatus());
  }

  // 아래는 기본적으로 true 반환
  @Override
  public boolean isAccountNonLocked() { return true; }
  @Override
  public boolean isAccountNonExpired() { return true; }
  @Override
  public boolean isCredentialsNonExpired() { return true; }
}
