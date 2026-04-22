package com.beez.beez.gits.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GitRepoDto {

  private String id;
  private String projectId;
  private String repoUrl;
  private String localPath;
  private String repoName;
  private String createDate;
  private String repoType;
  private String repoTypeName;

  private String lastSyncDate; // 최신 커밋 일자
  private int totalCommitCount; // 전체 커밋 수
}
