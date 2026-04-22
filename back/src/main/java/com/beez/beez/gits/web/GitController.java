package com.beez.beez.gits.web;

import com.beez.beez.gits.dto.GitCommitRequest;
import com.beez.beez.gits.dto.GitRepoDto;
import com.beez.beez.gits.service.GitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gits")
@Slf4j
public class GitController {

  private final GitService gitService;

  // 저장소 등록 및 복제
  @PostMapping("/repo")
  public ResponseEntity<String> insertRepositories(@RequestBody GitRepoDto dto){
    try {
      gitService.insertRepository(dto);
      return ResponseEntity.ok("저장소 등록 성공");
    } catch (RuntimeException e) {
//      System.out.println(e.getMessage());
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
    }
  }

  // 일감 번호로 커밋 목록 조회
  @GetMapping("/commits/{taskId}")
  public ResponseEntity<List<GitCommitRequest>> findCommitsByTaskId(@PathVariable String taskId){
    List<GitCommitRequest> list = gitService.findCommitsByTaskId(taskId);
    return ResponseEntity.ok(list);
  }

  // 프로젝트별 저장소 조회
  @GetMapping("/list/{projectId}")
  public ResponseEntity<List<GitRepoDto>> getRepoList(@PathVariable String projectId) {
    try {
//      System.out.println("나 지금 호출됐어!!! 프로젝트 ID: " + projectId);
      List<GitRepoDto> list = gitService.findReposByProjectId(projectId);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
//      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 커밋 로그 최신화 - 동기화
  @PutMapping("/sync/{id}")
  public ResponseEntity<?> updateSyncCommits(@PathVariable String id) {
    try {
      // Git 저장소에서 최신 로그를 읽어와 업데이트
      int count = gitService.updateSyncCommits(id);
      return ResponseEntity.ok(count);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());

    }catch (Exception e) {
      return ResponseEntity.internalServerError().body("서버 내부 오류로 동기화에 실패했습니다.");
    }
  }

  // 저장소 삭제(해제)
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteRepo(@PathVariable String id) {
    try {
      // 1. 서비스 호출 (DB 삭제 + 폴더 삭제 시도)
      gitService.deleteRepository(id);

      // 2. 성공 메시지 반환
      return ResponseEntity.ok("저장소 연결이 성공적으로 해제되었습니다.");

    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("서버 오류로 인해 삭제에 실패했습니다.");
    }
  }

}
