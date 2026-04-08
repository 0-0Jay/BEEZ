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

  private Map<String, String> permissionMap;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @PostConstruct
  protected void init() {
    refreshPermissionMap();
  }

  public void refreshPermissionMap() {
    List<PermissionListResponse> list = permissionMapper.findAllPermissions();
    Map<String, String> map = new HashMap<>();
    list.forEach(p->{
      String key = p.getMethod().toUpperCase() + ":" + p.getUrl();
      map.put(key, p.getId());
    });
    this.permissionMap = map;
  }

  public List<String> getRequiredPermissionId(String method, String url) {
    return permissionMap.keySet().stream()
      // 조건에 맞는 것만 필터링
      .filter(key -> {
        String[] split = key.split(":",2); // "GET"이랑 "/api/users/**"로 쪼개기
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
