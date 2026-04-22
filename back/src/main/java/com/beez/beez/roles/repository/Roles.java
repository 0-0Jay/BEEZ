package com.beez.beez.roles.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Roles {

  private String id;
  private String name;
  private String isAssignee;
}
