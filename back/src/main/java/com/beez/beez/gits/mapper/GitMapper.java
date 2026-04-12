package com.beez.beez.gits.mapper;

import com.beez.beez.gits.dto.GitCommitRequest;
import com.beez.beez.gits.dto.GitRepoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GitMapper {

  // PK 생성 함수 호출
  String getNextPk(@Param("tableName") String tableName);

  // 저장소 기본 정보 등록
  int insertRepository(GitRepoDto dto);

  // 커밋 로그 저장
  int updateCommitLog(GitCommitRequest dto);

  //  // 특정 일감에 연결된 커밋 목록 조회
  List<GitCommitRequest> selectCommitsByTaskId(String taskId);
}
