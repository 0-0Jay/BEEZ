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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users{


  private String id;          // 사원번호
  private String email;
  private String password;
  private String name;
  private String status;
  private LocalDateTime createdOn;
  // 역할
  private String role;

}