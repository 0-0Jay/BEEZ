package com.beez.beez.group.web;

import com.beez.beez.group.dto.GroupInsertRequest;
import com.beez.beez.group.dto.GroupListResponse;
import com.beez.beez.group.dto.GroupUpdateRequest;
import com.beez.beez.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupController {

  private final GroupService groupService;

  // 그룹 목록 조회
  @GetMapping("/list")
  public ResponseEntity<List<GroupListResponse>> findAllGroup(String name) {
    List<GroupListResponse> list = groupService.findAllGroup(name);
    return ResponseEntity.ok(list);
  }

  // 그룹 등록
  @PostMapping("/create")
  public ResponseEntity<String> insertGroup(@RequestBody GroupInsertRequest dto) {
    groupService.insertGroup(dto);
    return ResponseEntity.ok("그룹 등록이 완료되었습니다.");
  }

  // 그룹 수정
  @PutMapping("/{id}")
  public ResponseEntity<String> updateGroup(@PathVariable String id, @RequestBody GroupUpdateRequest dto) {
    dto.setId(id);
    groupService.updateGroup(dto);
    return ResponseEntity.ok("그룹 수정이 완료되었습니다.");
  }

  // 그룹 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteGroup(@PathVariable String id) {
    groupService.deleteGroup(id);
    return  ResponseEntity.ok("그룹 삭제가 완료되었습니다.");
  }
}
