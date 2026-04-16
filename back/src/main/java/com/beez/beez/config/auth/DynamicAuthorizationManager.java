package com.beez.beez.config.auth;

import com.beez.beez.permission.service.DynamicPermissionService;
import com.beez.beez.roles.dto.ProjectPermissionResponse;
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
    HttpServletRequest request = object.getRequest();
    String method = request.getMethod();
    String url = request.getRequestURI();

    if (method.equalsIgnoreCase("OPTIONS")) {
      return new AuthorizationDecision(true);
    }

    if (url.equals("/error")) {
      return new AuthorizationDecision(true);
    }

    Authentication auth = authentication.get();

    // 1. 인증 체크
    if (auth == null || !auth.isAuthenticated()) {
      return new AuthorizationDecision(false);
    }

    // 2. 관리자 통과
    boolean isAdmin = auth.getAuthorities().stream()
      .anyMatch(a -> a.getAuthority().equals("ROLE0001"));

    if (isAdmin) {
      return new AuthorizationDecision(true);
    }

    String projectId = request.getHeader("X-Project-Id");
    System.out.println("method: " + method + ", url: " + url);

    boolean isGlobalApi =
      (url.equals("/api/project/list") && method.equalsIgnoreCase("GET")) ||
        (url.equals("/api/project/pm") && method.equalsIgnoreCase("GET")) ||
        (url.equals("/api/project/check/title") && method.equalsIgnoreCase("GET")) ||
        (url.equals("/api/project/check/identifier") && method.equalsIgnoreCase("GET"));

    if (isGlobalApi) {
      return new AuthorizationDecision(true);
    }

    // 3. 글로벌 API (ROLE 기반)
    if (url.equals("/api/project") && method.equalsIgnoreCase("POST")) {

      boolean isAllowed = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE0001")
          || a.getAuthority().equals("ROLE0002"));

      return new AuthorizationDecision(isAllowed);
    }

    // 4. permission 조회
    List<String> requiredPerms =
      dynamicPermissionService.getRequiredPermissionId(method, url);

    boolean isPermissionApi =
      requiredPerms != null && !requiredPerms.isEmpty();

    if (isPermissionApi) {
      System.out.println("현재 유저 권한: " + auth.getAuthorities());
    }

    // 5. permission 없는 API → 그냥 통과
    if (!isPermissionApi) {
      System.out.println("권한 설정 안 된 API: " + method + " " + url);
      return new AuthorizationDecision(true);
    }

    // 6. permission 있는 API → projectId 필수
    if (projectId == null) {
      return new AuthorizationDecision(false);
    }

    // 7. 프로젝트 멤버 체크
    boolean isMember =
      rolesMapper.isProjectMember(auth.getName(), projectId) > 0;

    if (!isMember) {
      return new AuthorizationDecision(false);
    }

    // 8. 프로젝트 권한 조회
    List<ProjectPermissionResponse> projectPerms =
      rolesMapper.findPermissionsByProject(auth.getName(), projectId);

    // 9. 최종 권한 체크
    boolean isGranted =
      auth.getAuthorities().stream()
        .anyMatch(a -> requiredPerms.contains(a.getAuthority()))
        || projectPerms.stream()
        .map(ProjectPermissionResponse::getId)
        .anyMatch(requiredPerms::contains);

    System.out.println("=== 권한 체크 ===");
    System.out.println("========================================");
    System.out.println("검사 중인 URL: [" + method + "] " + url);
    System.out.println("user: " + auth.getName());
    System.out.println("requiredPerms: " + requiredPerms);
    System.out.println("projectPermIds: " +
      projectPerms.stream().map(ProjectPermissionResponse::getId).toList()
    );
    System.out.println("projectId: " + projectId + ", isMember: " + isMember);
    System.out.println("projectPerms: " + projectPerms);
    System.out.println("isGranted: " + isGranted);

    return new AuthorizationDecision(isGranted);
  }
}
