package com.beez.beez.users.service;

import com.beez.beez.users.dto.MailSendDto;
import com.beez.beez.users.dto.PasswordResetRequest;
import com.beez.beez.users.dto.UserListResponse;
import com.beez.beez.users.dto.UserTokenDto;
import com.beez.beez.users.mapper.UsersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordService {

  private final UsersMapper usersMapper;
  private final MailService mailService;
  private final PasswordEncoder passwordEncoder;

  public MailSendDto sendResetPasswordEmail(PasswordResetRequest dto) {
    UserListResponse res = usersMapper.findUserForPasswordReset(dto);
    if(res == null){
      throw new RuntimeException("아이디나 이메일 정보를 다시 확인해주세요.");
    }

    String userId = res.getId();
    String tokenValue = UUID.randomUUID().toString();

    UserTokenDto userTokenDto
      = UserTokenDto.builder()
      .userId(userId)
      .tokenValue(tokenValue)
      .tokenType("X0")
      .build();

    usersMapper.insertUserTokens(userTokenDto);

    return new MailSendDto(res.getEmail(), res.getName(), tokenValue);
  }
}
