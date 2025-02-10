package com.server.service.impl;

import com.server.config.email.SendEmailServiceConfig;
import com.server.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@EnableAsync
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SendEmailServiceConfig sendEmailServiceConfig;

    @Async
    @Override
    public void sendEmail(String email, String password) {
        Map<String, Object> modeMap = new HashMap<>();
        modeMap.put("email", email);
        modeMap.put("password", password);
        sendEmailServiceConfig.sendEmailPassword(email, password, modeMap, "SendEmailPassword");
    }

    @Async
    @Override
    public void sendActivationEmail(String email, String activationLink) {
        Map<String, Object> modeMap = new HashMap<>();
        modeMap.put("email", email);
        modeMap.put("activationLink", activationLink);
        sendEmailServiceConfig.sendEmailPassword(email, activationLink, modeMap, "SignUp");
    }
}
