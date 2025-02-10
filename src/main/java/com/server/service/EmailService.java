package com.server.service;

public interface EmailService {
     void sendEmail(String email, String password);
     void sendActivationEmail(String email,String activeLink);
}
