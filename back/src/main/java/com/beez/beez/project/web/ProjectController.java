package com.beez.beez.project.web;

import com.beez.beez.project.dto.*;
import com.beez.beez.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {
  
  private final ProjectService projectService;
  
  //프로젝트 등록
  @PostMapping
  public String insertProject(@RequestBody ProjectCreateRequest dto) {
    return projectService.insertProject(dto);
  }
  
  //프로젝트 식별자 중복체크
  @GetMapping("/check/identifier")
  public ResponseEntity<Boolean> checkIdentifier(
    @RequestParam String identifier,
    @RequestParam(required = false) String projectId) {
    return ResponseEntity.ok(projectService.checkIdentifier(identifier, projectId));
  }

  //프로젝트 이름 중복체크
  @GetMapping("/check/title")
  public ResponseEntity<Boolean> checkTitle(
    @RequestParam String title,
    @RequestParam(required = false) String projectId) {
    return ResponseEntity.ok(projectService.checkTitle(title, projectId));
  }
  
  //프로젝트 목록 조회
  @GetMapping("/list")
  public ResponseEntity<List<ProjectListResponse>> selectProjectList(@ModelAttribute ProjectFilterRequest filter) {
    return ResponseEntity.ok(projectService.selectProjectList(filter));
  }
  
  //프로젝트 잠금보관
  @PutMapping("/lock/{id}")
  public ResponseEntity<Void> updateProjectLock(@PathVariable String id) {
    projectService.updateProjectLock(id);
    return ResponseEntity.ok().build();
  }
  
  //프로젝트 잠금보관 해제
  @PutMapping("/unlock/{id}")
  public ResponseEntity<Void> updateUnProjectLock(@PathVariable String id) {
    projectService.updateUnProjectLock(id);
    return ResponseEntity.ok().build();
  }
  
  //프로젝트 삭제
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteProject(@PathVariable String id) {
    projectService.deleteProject(id);
    return ResponseEntity.ok().build();
  }
  
  //프로젝트 단건 조회
  @GetMapping("/{id}")
  public ResponseEntity<ProjectInfoResponse> findById(@PathVariable String id) {
    ProjectInfoResponse response = projectService.findById(id);
    return ResponseEntity.ok(response);
  }
  
  //프로젝트 수정
  @PutMapping("/{id}")
  public ResponseEntity<ProjectInfoResponse> updateProject(@PathVariable String id, @RequestBody ProjectUpdateRequest dto) {
    ProjectInfoResponse response = projectService.updateProject(id, dto);
    return ResponseEntity.ok(response);
  }
  
  // 프로젝트 구성원 조회
  @GetMapping("/{projectId}/members")
  public ResponseEntity<ProjectMemberResponse> findProjectMember(@PathVariable String projectId) {
    return ResponseEntity.ok(projectService.findProjectMember(projectId));
  }
  
  //프로젝트 구성원 삭제
  @DeleteMapping("/member/{projectMemberId}")
  public ResponseEntity<Void> deleteProjectMember(@PathVariable String projectMemberId, @RequestParam String projectId) {
    projectService.deleteProjectMember(projectMemberId, projectId);
    return ResponseEntity.ok().build();
  }
  
  //프로젝트 구성원 수정
  @PutMapping("/member/{projectMemberId}")
  public ResponseEntity<Void> updateProjectMember(@PathVariable String projectMemberId, @RequestBody ProjectMemberUpdateRequest dto) {
    projectService.updateProjectMember(projectMemberId, dto);
    return ResponseEntity.ok().build();
  }
  
  //역할 조회
  @GetMapping("/roles")
  public ResponseEntity<List<RolesResponse>> findRoles() {
    return ResponseEntity.ok(projectService.findRoles());
  }
  
  //사용자 + 그룹 검색
  @GetMapping("/members/search")
  public ResponseEntity<MemberSearchResponseDto> searchMembers(
    @RequestParam String projectId,
    @RequestParam(required = false) String keyword) {
      return ResponseEntity.ok(projectService.searchMembers(projectId, keyword));
  }
  
  //프로젝트 구성원 추가
  @PostMapping("/members")
  public ResponseEntity<Void> insertProjectMember(@RequestBody ProjectMemberRequest dto) {
    projectService.insertProjectMember(dto);
    return ResponseEntity.ok().build();
  }

  //로드맵 목록 조회
  @GetMapping("/{projectId}/roadmap")
  public ResponseEntity<List<RoadmapListResponse>> findRoadmapList(
          @PathVariable String projectId,
          @ModelAttribute RoadmapFilterRequest filter) {
    filter.setProjectId(projectId);
    return ResponseEntity.ok(projectService.findRoadmapList(filter));
  }
  
}
