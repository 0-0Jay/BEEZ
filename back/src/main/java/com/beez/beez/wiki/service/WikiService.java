package com.beez.beez.wiki.service;

import com.beez.beez.project.dto.ProjectCreateRequest;
import com.beez.beez.wiki.dto.WikiProjectRequest;
import com.beez.beez.wiki.dto.WikiRequest;
import com.beez.beez.wiki.dto.WikiVersionRequest;
import com.beez.beez.wiki.mapper.WikiMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//핵심 로직이 들어가는 파일 - (매퍼의 매서드들을 뭉쳐서 서비스의 형태로 사용)
// 어떤 기능이 필요한지 이름만 정의 (실제 구현 코드는 impl파일에서 작성)
public interface WikiService {
  
  //신규 위키 등록
  
  void insertWiki (WikiRequest wikiRequest, WikiVersionRequest wikiVersionRequest);
  
  //위키 수정 (새 버전 저장 + 포인터 갱신)
  void updateWikiContent(WikiVersionRequest wikiVersionRequest, WikiRequest wikiRequest);
  
  //프로젝트별 목록 조회
  List<WikiRequest> findWikiByProjectId(String projectId);
  
  //최신본문 상세 조회
  WikiVersionRequest findLatestVersion(String wikiId);
  
  //히스토리 조회
  List<WikiVersionRequest> findWikiVersionList(String wikiId);
  
  //프로젝트 정보 조회 (위키 헤더용)
  WikiProjectRequest getProjectInfo(String projectId);
  

}
