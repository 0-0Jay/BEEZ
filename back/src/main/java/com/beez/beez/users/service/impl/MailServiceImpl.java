package com.beez.beez.users.service.impl;

import com.beez.beez.users.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

  private final JavaMailSender mailSender;

  @Async
  @Override
  public void sendPasswordResetMail(String email, String name, String token) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
      helper.setFrom("xcvdbals666@daum.net");
      helper.setTo(email);
      helper.setSubject("[BEEZ] 비밀번호 재설정 안내");

      String frontendUrl = "http://localhost:5173";
      String resetUrl = frontendUrl + "/auth/pwReset?token=" + token;

      String content =
        "<div style='margin:20px; padding:20px; border:1px solid #e2e2e2; border-radius:10px; font-family:sans-serif;'>" +
          "<h2>안녕하세요, " + name + "님.</h2>" +
          "<p>BEEZ 계정의 비밀번호 재설정 요청을 확인했습니다.</p>" +
          "<p>아래 <b>'비밀번호 재설정'</b> 버튼을 클릭하여 새로운 비밀번호를 설정해 주세요.</p>" +
          "<div style='margin:30px 0;'>" +
          "<a href='" + resetUrl + "' style='background-color:#E8920E; color:#FFF; padding:12px 25px; text-decoration:none; font-weight:bold; border-radius:5px;'>비밀번호 재설정하기</a>" +
          "</div>" +
          "<p style='color:#ff0000;'>※ 이 링크는 발송 후 30분 동안만 유효합니다.</p>" +
          "<p>본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.</p>" +
          "<hr style='border:0; border-top:1px solid #eee; margin:20px 0;'>" +
          "<p style='font-size:12px; color:#888;'>본 메일은 발신 전용입니다.</p>" +
          "</div>";
//      String content = name + "님, 안녕하세요. 비밀번호 재설정 토큰입니다: " + token;

      helper.setText(content, true);

      mailSender.send(mimeMessage);
    } catch (Exception e) {
      System.err.println("메일 전송 실패: " + e.getMessage());
    }
  }
}
