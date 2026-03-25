package com.beez.beez.project.web;

import com.beez.beez.project.dto.ProjectCreateRequest;
import com.beez.beez.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
  
  private final ProjectService projectService;
  
  @GetMapping
  public  String test() {
    return "test";
  }
  
  //프로젝트 등록
  @PostMapping
  public String insertProject(@RequestBody ProjectCreateRequest dto) {
    return projectService.insertProject(dto);
  }
}
