package com.beez.beez.project.web;

import com.beez.beez.project.dto.ProjectCreateRequest;
import com.beez.beez.project.dto.ProjectFilterRequest;
import com.beez.beez.project.dto.ProjectListResponse;
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
  @PutMapping("/delete/{id}")
  public ResponseEntity<Void> updateProjectStatus(@PathVariable String id) {
    projectService.updateProjectStatus(id);
    return ResponseEntity.ok().build();
  }
  
}
