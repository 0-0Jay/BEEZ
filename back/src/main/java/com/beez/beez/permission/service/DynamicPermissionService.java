package com.beez.beez.permission.service;

import com.beez.beez.permission.dto.PermissionListResponse;
import com.beez.beez.permission.mapper.PermissionMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicPermissionService {

  private final PermissionMapper permissionMapper;

  Map<String, String> permissionMap;
  AntPathMatcher antPathMatcher = new AntPathMatcher();

  @PostConstruct
  protected void init() {
    List<PermissionListResponse> list = permissionMapper.findAllPermissions();
    this.permissionMap = new HashMap<>();

    list.forEach(p -> {
      String key = p.getMethod().toUpperCase() + ":" + p.getUrl();
      permissionMap.put(key, p.getId());
    });
  }

  public List<String> getRequiredPermissionId(String method, String url) {
    return permissionMap.keySet().stream()
      // 조건에 맞는 것만 필터링
      .filter(key -> {
        String[] split = key.split(":"); // "GET"이랑 "/api/users/**"로 쪼개기
        String splitMethod = split[0];
        String splitUrl = split[1];

        // 사용자가 보낸 method랑 같고 주소가 패턴에 맞는지 확인
        return method.equalsIgnoreCase(splitMethod)
          && antPathMatcher.match(splitUrl, url);
      })
      .map(key -> permissionMap.get(key))
      .collect(Collectors.toList());
  }
}
