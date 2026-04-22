package com.beez.beez.project.web;

import com.beez.beez.project.dto.VersionCreateRequest;
import com.beez.beez.project.dto.VersionFilterRequest;
import com.beez.beez.project.dto.VersionListResponse;
import com.beez.beez.project.service.VersionService;
import com.beez.beez.task.dto.CommonCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<?> insertVersion(@RequestBody VersionCreateRequest dto) {
    try {
      versionService.insertVersion(dto);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
  
  // 버전 수정
  @PutMapping("/{id}")
  public ResponseEntity<?> updateVersion(@PathVariable String id, @RequestBody VersionCreateRequest dto) {
    try {
      dto.setId(id);
      versionService.updateVersion(dto);
      return  ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
  
  // 버전 삭제
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteVersion(@PathVariable String id,
                            @RequestBody VersionCreateRequest dto) {
    try {
      versionService.deleteVersion(id, dto.getProjectId(), dto.getName());
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
  
  // 버전 목록조회(필터링)
  @GetMapping("/list")
  public ResponseEntity<List<VersionListResponse>> selectVersionList(@ModelAttribute VersionFilterRequest filter) {
    return ResponseEntity.ok(versionService.selectVersionList(filter));
  }
  
  // 공통코드 조회
  @GetMapping("/common")
  public List<CommonCodeResponse> findCommonCodeList() {
    return versionService.findCommonCodeList();
  }
  
  
}
