package com.beez.beez.project.mapper;

import com.beez.beez.project.dto.VersionCreateRequest;
import com.beez.beez.project.dto.VersionFilterRequest;
import com.beez.beez.project.dto.VersionListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VersionMapper {
  //버전생성
  void insertVersion(VersionCreateRequest dto);
  
  //버전 수정
  void updateVersion(VersionCreateRequest dto);
  
  //버전 삭제
  int deleteVersion(String id);
  
  //버전 목록조회(필터링)
  List<VersionListResponse> selectVersionList(VersionFilterRequest filter);
  
}
