package com.beez.beez.group.web;

import com.beez.beez.group.dto.*;
import com.beez.beez.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

  // 그룹 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<Map<String, Object>> findGroupDetail(@PathVariable String id){
    Map<String, Object> details = groupService.findGroupDetail(id);
    return ResponseEntity.ok(details);
  }

  // 그룹 등록
  @PostMapping("/create")
  public ResponseEntity<String> insertGroup(@RequestBody GroupInsertRequest dto) {
    try {
      groupService.insertGroup(dto);
      return ResponseEntity.ok("그룹 등록이 완료되었습니다.");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
    }
  }

  // 그룹 수정
  @PutMapping("/{id}")
  public ResponseEntity<String> updateGroup(@PathVariable String id, @RequestBody GroupUpdateRequest dto) {
    try{
      dto.setId(id);
      groupService.updateGroup(dto);
      return ResponseEntity.ok("그룹 수정이 완료되었습니다.");
    }catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
    }

  }

  // 그룹 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteGroup(@PathVariable String id) {
    try {
      groupService.deleteGroup(id);
      return ResponseEntity.ok("그룹 삭제가 완료되었습니다.");
    } catch (RuntimeException e) {
      if ("USED_IN_PROJECT".equals(e.getMessage())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
          .body("현재 프로젝트에 할당된 그룹은 삭제할 수 없습니다.");
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("서버 오류로 삭제에 실패했습니다.");
    }
  }
}
