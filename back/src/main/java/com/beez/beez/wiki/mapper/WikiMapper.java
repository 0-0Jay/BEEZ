package com.beez.beez.wiki.mapper;

import com.beez.beez.wiki.dto.WikiRequest;
import com.beez.beez.wiki.dto.WikiVersionRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

// mapper.xml파일이랑 연결 - 메서드 이름 작성하면 됨
//sql호출 목적 - sql문은 mapper.xml파일에다가 쓰면 됨


@Mapper
public interface WikiMapper {
  //## 반환타입 메서드명 (매개변수타입 매개변수명)으로 작성
  
  //위키 글 작성
  void insertWiki(WikiRequest wikiRequest);
  
  //새로운 위키 버전(내용) 등록 - 버전 추가되면 번호 추가
  void insertWikiVersion(WikiVersionRequest wikiVersionRequest);
  
  //wiki 테이블에 최신 버전의 위키를 알려주기 위함/ 되돌리기 기능
  void updateWiki(WikiRequest wikiRequest);
  
  //하나의 위키(wikiId)의 과거 수정이력을 보기 위함 - 히스토리 기능
  List<WikiVersionRequest> findWikiVersionList(String wikiId);
  
  //프로젝트의 위키 목록 가져오기 - 어떤 프로젝트의 위키
  List<WikiRequest> findWikiByProjectId(String projectId);
  
  //특정 위키(wikiId)의 최신 버전 상세 가져오기 - 프로젝트의 어떤 위키 / 화면에 뿌려주기 위함
  WikiVersionRequest findLatestVersion(String wikiId);
  
}
