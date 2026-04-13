package com.beez.beez.gits.service;

import com.beez.beez.gits.dto.GitCommitRequest;
import com.beez.beez.gits.dto.GitRepoDto;

import java.util.List;

public interface GitService {

  // 저장소 등록 및 복제
  String insertRepositories(GitRepoDto dto) throws Exception;

  // 커밋 동기화
  int updateSyncCommits(String repoId, String localPath) throws Exception;

  // 일감 번호로 커밋 목록 조회
  List<GitCommitRequest> findCommitsByTaskId(String taskId);

}
