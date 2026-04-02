package com.beez.beez.project.web;

import com.beez.beez.project.dto.VersionCreateRequest;
import com.beez.beez.project.dto.VersionFilterRequest;
import com.beez.beez.project.dto.VersionListResponse;
import com.beez.beez.project.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project/version")
public class VersionController {
  
  private final VersionService versionService;
  
  // 버전 생성
  @PostMapping
  public void insertVersion(@RequestBody VersionCreateRequest dto) {
    versionService.insertVersion(dto);
  }
  
  // 버전 수정
  @PutMapping("/{id}")
  public void updateVersion(@PathVariable String id, @RequestBody VersionCreateRequest dto) {
    dto.setId(id);
    versionService.updateVersion(dto);
  }
  
  // 버전 삭제
  @DeleteMapping("/delete/{id}")
  public void deleteVersion(@PathVariable String id,
                            @RequestBody VersionCreateRequest dto) {
    versionService.deleteVersion(id, dto.getProjectId(), dto.getName());
  }
  
  // 버전 목록조회(필터링)
  @GetMapping("/list")
  public ResponseEntity<List<VersionListResponse>> selectVersionList(@ModelAttribute VersionFilterRequest filter) {
    return ResponseEntity.ok(versionService.selectVersionList(filter));
  }
  
}
