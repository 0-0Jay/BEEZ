package com.beez.beez.config.auth;

import com.beez.beez.permission.service.DynamicPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

  private final DynamicPermissionService dynamicPermissionService;

  @Override
  public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    HttpServletRequest httpServletRequest = object.getRequest();
    String method = httpServletRequest.getMethod();
    String url = httpServletRequest.getRequestURI();

    List<String> permIds = dynamicPermissionService.getRequiredPermissionId(method, url);

    // 필요한 권한이 아예 없으면 -> 누구나 입장 가능
    if (permIds == null || permIds.isEmpty()) {
      return new AuthorizationDecision(true);
    }

    // 저 정보가 없으면 바로 거절
    Authentication auth = authentication.get();
    if (auth == null || !auth.isAuthenticated()) {
      return new AuthorizationDecision(false);
    }

    // 유저가 로그인했는지 + 권한이 하나라도 맞는지 체크
    boolean isGranted = authentication.get().getAuthorities().stream()
      .anyMatch(grantedAuthority -> permIds.contains(grantedAuthority.getAuthority()));

    System.out.println("검사 중인 URL: " + url);
    System.out.println("DB에서 찾은 필요한 권한들: " + permIds);
    System.out.println("유저가 가진 권한들: " + auth.getAuthorities());

    return new AuthorizationDecision(isGranted);
  }
}
