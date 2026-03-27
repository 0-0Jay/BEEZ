package com.beez.beez.project.web;

import com.beez.beez.project.dto.ProjectCreateRequest;
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
  public ResponseEntity<List<ProjectListResponse>> findAllProjects() {
    return ResponseEntity.ok(projectService.findAllProjects());
  }
  
}
