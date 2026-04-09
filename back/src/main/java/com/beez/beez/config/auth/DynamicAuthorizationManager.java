package com.beez.beez.config.auth;

import com.beez.beez.permission.service.DynamicPermissionService;
import com.beez.beez.roles.mapper.RolesMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

  private final DynamicPermissionService dynamicPermissionService;
  private final RolesMapper rolesMapper;

  @Override
  public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    Authentication auth = authentication.get();

    // 인증 정보가 없으면 거절
    if (auth == null || !auth.isAuthenticated()) {
      return new AuthorizationDecision(false);
    }

    // 시스템 관리자(ROLE0001) 바로 통과
    boolean isAdmin = auth.getAuthorities().stream()
      .anyMatch(a -> a.getAuthority().equals("ROLE0001"));
    if (isAdmin) {
      return new AuthorizationDecision(true);
    }

    // 현재 요청의 권한 정보 조회
    HttpServletRequest request = object.getRequest();
    String method = request.getMethod();
    String url = request.getRequestURI();
    String projectId = request.getHeader("X-Project-Id");

    // 프로젝트 선택 전 호출해야 하는 API (목록 조회 등)
    // 이 API들은 projectId 헤더가 없어도 권한 검사를 진행함
    boolean isProjectListApi = url.equals("/api/project/list") && method.equalsIgnoreCase("GET");

    // 현재 요청에 필요한 권한 코드 목록 조회
    List<String> requiredPerms = dynamicPermissionService.getRequiredPermissionId(method, url);

    // 필요한 권한 설정이 아예 없는 API라면 -> 누구나 접근 가능으로 판단
    if (requiredPerms == null || requiredPerms.isEmpty()) {
      return new AuthorizationDecision(true);
    }

    // 프로젝트 실시간 권한 조회 (role_mapping 기반)
    List<String> projectPerms = new ArrayList<>();
    if (projectId != null) {
      // 헤더에 프로젝트 ID가 있을 때만 DB에서 해당 프로젝트의 내 권한을 가져옴
      projectPerms = rolesMapper.findPermissionsByProject(auth.getName(), projectId);
    }

    // 최종 승인
    // - 내 기본 권한(가입 시 부여된 것)에 필요한 권한이 있을 때
    // - 또는 현재 선택한 프로젝트에서의 내 역할(projectPerms)에 필요한 권한이 있을 때
    boolean isGranted = isProjectListApi // 프로젝트 목록 조회 API면 무조건 true
      || auth.getAuthorities().stream().anyMatch(a -> requiredPerms.contains(a.getAuthority()))
      || projectPerms.stream().anyMatch(requiredPerms::contains);

    // 디버깅 로그 (테스트할 때 큰 도움이 돼!)
    System.out.println("========================================");
    System.out.println("검사 중인 URL: [" + method + "] " + url);
    System.out.println("현재 유저가 가진 권한: " + auth.getAuthorities());
    System.out.println("헤더 프로젝트 ID: " + projectId);
    System.out.println("필요한 권한: " + requiredPerms);
    System.out.println("프로젝트 실시간 권한: " + projectPerms);
    System.out.println("최종 승인 여부: " + isGranted);
    System.out.println("========================================");

    return new AuthorizationDecision(isGranted);
  }
}
