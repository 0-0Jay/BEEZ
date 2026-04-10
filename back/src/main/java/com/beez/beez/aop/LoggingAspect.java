package com.beez.beez.aop;

import com.beez.beez.logs.service.LogsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
  
  private final LogsService logsService;
  
  @AfterReturning("@annotation(loggable)")
  public void logAfter(JoinPoint joinPoint, Loggable loggable) {
    try {
      // 1. 작성자
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      String userId = auth.getName();
      
      // 2. 파라미터 정보 분석
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Parameter[] parameters = signature.getMethod().getParameters();
      Object[] args = joinPoint.getArgs();
      
      // 3. content, link 안의 {필드명} 치환
      String content = resolveTemplate(loggable.content(), parameters, args);
      String link    = resolveTemplate(loggable.link(), parameters, args);
      
      // 4. idField로 지정된 값 꺼내기
      String targetId = resolveValue(loggable.idField(), parameters, args);
      
      // 5. 로그 저장
      logsService.insertLogs(
        targetId,
        loggable.logType(),
        loggable.logCategory(),
        content,
        link,
        userId
      );
      
    } catch (Exception e) {
      log.error("로그 기록 실패: {}", e.getMessage());
    }
  }

  private String resolveTemplate(String template, Parameter[] parameters, Object[] args) {
    Pattern pattern = Pattern.compile("\\{(\\w+)}");
    Matcher matcher = pattern.matcher(template);
    StringBuffer sb = new StringBuffer();
    
    while (matcher.find()) {
      String fieldName = matcher.group(1);
      String value = resolveValue(fieldName, parameters, args);
      matcher.appendReplacement(sb, value);
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
  
  private String resolveValue (String fieldName, Parameter[] parameters, Object[] args) {
    // 1순위: 파라미터 이름과 직접 일치
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].getName().equals(fieldName)) {
        return args[i] != null ? args[i].toString() : "";
      }
    }
    
    // 2순위: DTO 안의 필드 탐색
    for (Object arg: args) {
      if (arg == null) continue;
      // String, Long 같은 단순 타입은 건너뜀
      if (arg.getClass().getPackageName().startsWith("java")) continue;
      
      String value = getFieldFromObject(arg, fieldName);
      if (!value.isEmpty()) return value;
    }
    return "";
  }
  
  // DTO 안에서 리플렉션으로 필드값 꺼내기
  private  String getFieldFromObject(Object obj, String fieldName) {
    try {
      Field field = obj.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      Object value = field.get(obj);
      return value != null ? value.toString() : "";
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return "";
    }
  }


}
