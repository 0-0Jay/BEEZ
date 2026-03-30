package com.beez.beez.users.web;

import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserSearchRequest;
import com.beez.beez.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {

  private final UsersService usersService;

  @GetMapping("/list")
  public ResponseEntity<List<UserListResponse>> findAllUsers(@ModelAttribute UserSearchRequest search) {
    return ResponseEntity.ok(usersService.findAllUsers(search));
  }

}
