package com.beez.beez.users.service;

public interface MailService {

  void sendPasswordResetMail(String email, String name, String token);
}
