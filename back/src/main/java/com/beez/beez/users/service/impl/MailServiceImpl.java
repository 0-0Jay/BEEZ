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
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      helper.setFrom("xcvdbals666@daum.net");
      helper.setTo(email);
      helper.setSubject("[BEEZ] 비밀번호 재설정 안내");

      String frontendUrl = "http://localhost:5173";
      String resetUrl = frontendUrl + "/auth/pwReset?token=" + token;

      String content = """
        <div style="font-family: 'Pretendard', sans-serif; max-width: 500px; margin: 0 auto; border: 1px solid #eee; border-top: 6px solid #f5a623; border-radius: 12px; overflow: hidden;">
          <div style="padding: 40px 30px; background-color: #ffffff;">
            <div style="text-align: center; margin-bottom: 30px;">
              <h2 style="color: #3a3835; margin: 0; font-size: 24px;">비밀번호 재설정 안내</h2>
              <div style="width: 40px; height: 2px; background-color: #f5a623; margin: 15px auto;"></div>
            </div>
            <p style="color: #736f68; font-size: 15px; line-height: 1.6;">
              안녕하세요, <strong>%s</strong>님.<br>
              BEEZ 계정의 비밀번호 재설정 요청을 확인했습니다.<br>
              아래 버튼을 클릭하여 새로운 비밀번호를 설정해 주세요.
            </p>
            <div style="text-align: center; margin: 35px 0;">
              <a href="%s" style="display: inline-block; background-color: #e8920e; color: #ffffff; padding: 14px 40px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 15px;">비밀번호 재설정하기</a>
            </div>
            <div style="background-color: #fff8ec; padding: 15px; border-radius: 8px; border: 1px dashed #f5a623; text-align: center;">
              <p style="margin: 0; color: #f44336; font-size: 13px; font-weight: bold;">
                ※ 이 링크는 발송 후 30분 동안만 유효합니다.
              </p>
            </div>
            <p style="color: #9a9690; font-size: 13px; margin-top: 25px; text-align: center;">
              본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.
            </p>
          </div>
          <div style="background-color: #f9f9f7; padding: 20px; text-align: center; border-top: 1px solid #eee;">
            <p style="margin: 0; color: #9a9690; font-size: 12px;">본 메일은 발신 전용입니다. BEEZ Team</p>
          </div>
        </div>
        """.formatted(name, resetUrl);
//      String content = name + "님, 안녕하세요. 비밀번호 재설정 토큰입니다: " + token;

      helper.setText(content, true);

      mailSender.send(mimeMessage);
    } catch (Exception e) {
      System.err.println("메일 전송 실패: " + e.getMessage());
    }
  }

  @Async
  @Override
  public void sendWelcomeEmail(String email, String name, String rawPassword) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      helper.setFrom("xcvdbals666@daum.net");
      helper.setTo(email);
      helper.setSubject("[BEEZ] 가입 완료");

      String frontendUrl = "http://localhost:5173";

      String content = """
        <div style="font-family: 'Pretendard', sans-serif; max-width: 500px; margin: 0 auto; border: 1px solid #eee; border-top: 6px solid #f5a623; border-radius: 12px; overflow: hidden;">
          <div style="padding: 40px 30px; background-color: #ffffff;">
            <div style="text-align: center; margin-bottom: 30px;">
              <h2 style="color: #3a3835; margin: 0; font-size: 24px;">BEEZ 가입을 환영합니다!</h2>
              <div style="width: 40px; height: 2px; background-color: #f5a623; margin: 15px auto;"></div>
            </div>
            <p style="color: #736f68; font-size: 15px; line-height: 1.6;">
              안녕하세요, <strong>%s</strong> 사원님!<br>
              사내 협업 툴 BEEZ의 계정이 생성되었습니다.<br>
              아래의 초기 접속 정보를 확인해 주세요.
            </p>
            <div style="background-color: #fafaf8; padding: 25px; border-radius: 10px; margin: 25px 0; border: 1px solid #f0efeb;">
              <p style="margin: 0 0 10px 0; color: #3a3835; font-size: 14px;"><strong>이메일(ID):</strong> %s</p>
              <p style="margin: 0; color: #3a3835; font-size: 14px;"><strong>초기 비밀번호:</strong> <span style="color: #e8920e; font-weight: bold;">%s</span></p>
            </div>
            <p style="color: #f44336; font-size: 13px; margin-bottom: 30px;">* 보안을 위해 최초 로그인 후 반드시 비밀번호를 변경해 주세요.</p>
            <div style="text-align: center;">
              <a href="%s" style="display: inline-block; background-color: #e8920e; color: #ffffff; padding: 14px 40px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 15px;">BEEZ 시작하기</a>
            </div>
          </div>
          <div style="background-color: #f9f9f7; padding: 20px; text-align: center; border-top: 1px solid #eee;">
            <p style="margin: 0; color: #9a9690; font-size: 12px;">본 메일은 발신 전용입니다. 문의사항은 관리자에게 연락해 주세요.</p>
          </div>
        </div>
        """.formatted(name, email, rawPassword, frontendUrl);

      helper.setText(content, true);

      System.out.println("메일 발송 직전! 대상: " + email);
      mailSender.send(mimeMessage);
      System.out.println("메일 발송 성공!");
    } catch (Exception e) {
      System.err.println("메일 전송 실패: " + e.getMessage());
    }
  }
}
