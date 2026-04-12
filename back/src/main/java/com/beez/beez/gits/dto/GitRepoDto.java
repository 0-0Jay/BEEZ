package com.beez.beez.gits.dto;

import lombok.Data;

@Data
public class GitRepoDto {

  private String id;
  private String projectId;
  private String repoUrl;
  private String localPath;
}
