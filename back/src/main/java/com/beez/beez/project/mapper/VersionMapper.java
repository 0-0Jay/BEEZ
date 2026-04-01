package com.beez.beez.project.mapper;

import com.beez.beez.project.dto.VersionCreateRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VersionMapper {
  //버전생성
  void insertVersion(VersionCreateRequest dto);
  
  //버전 수정
  void updateVersion(VersionCreateRequest dto);
  
  //버전 삭제
  int deleteVersion(String id);
  
}
