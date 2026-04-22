package com.beez.beez.users.service;

public interface MailService {

  void sendPasswordResetMail(String email, String name, String token);
  void sendWelcomeEmail(String id, String email, String name, String rawPassword);
}
