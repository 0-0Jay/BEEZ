package com.beez.beez.gits.service.impl;

import com.beez.beez.gits.dto.GitCommitRequest;
import com.beez.beez.gits.dto.GitRepoDto;
import com.beez.beez.gits.mapper.GitMapper;
import com.beez.beez.gits.service.GitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GitServiceImpl implements GitService {

  private final GitMapper gitMapper;

  // 상대경로 설정
  private final String REPO_ROOT = "./git_repos";

  // 저장소 등록 및 복제
  @Override
  public String insertRepositories(GitRepoDto dto) throws Exception {
    return "";
  }

  // 커밋 동기화
  @Override
  public int updateSyncCommits(String repoId, String localPath) throws Exception {
    return 0;
  }

  // 일감 번호로 커밋 목록 조회
  @Override
  public List<GitCommitRequest> findCommitsByTaskId(String taskId) {
    return List.of();
  }
}
