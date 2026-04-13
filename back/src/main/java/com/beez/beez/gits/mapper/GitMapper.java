package com.beez.beez.gits.mapper;

import com.beez.beez.gits.dto.GitCommitRequest;
import com.beez.beez.gits.dto.GitRepoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GitMapper {

  // PK 생성 함수 호출
  String getNextPk(@Param("tableName") String tableName);

  // 중복 체크
  int checkRepoExists(GitRepoDto dto);

  // 저장소 기본 정보 등록
  void insertRepository(GitRepoDto dto);

  // 커밋 로그 저장
  int updateCommitLog(GitCommitRequest dto);

  // 저장소 상세조회
  GitRepoDto findReposById(String id);

  // 특정 일감에 연결된 커밋 목록 조회
  List<GitCommitRequest> findCommitsByTaskId(String taskId);

  // 프로젝트별 저장소 조회
  List<GitRepoDto> findReposByProjectId(String taskId);

  void deleteCommitLogsByRepoId(String id);
  void deleteRepository(String id);
}
