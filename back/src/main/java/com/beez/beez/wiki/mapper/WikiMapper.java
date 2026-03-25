package com.beez.beez.wiki.mapper;

import com.beez.beez.wiki.dto.WikiRequest;
import com.beez.beez.wiki.dto.WikiVersionRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

// mapper.xml파일이랑 연결 - mybatis 인터페이스가 들어가는 곳
//sql호출 목적 - sql문은 mapper.xml파일에다가 쓰면 됨


@Mapper
public interface WikiMapper {
  //## 반환타입 메서드명 (매개변수타입 매개변수명)으로 작성
  
  //새로운 위키 항목 생성 - 글 작성
  void insertWiki(WikiRequest wikiRequest);
  
  //새로운 위키 버전(내용) 등록 - 버전 추가되면 번호 추가
  void insertWikiVersion(WikiVersionRequest wikiVersionRequest);
  
  //프로젝트의 위키 목록 가져오기 - 어떤 프로젝트의 위키
  List<WikiRequest> selectWikiList(String projectId);
  
  //특정 위키의 최신 버전 상세 가져오기 - 프로젝트의 어떤 위키
  WikiVersionRequest selectLatestVersion(String wikiId);
  
}
