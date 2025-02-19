package com.server.service.impl;

import com.server.config.email.SendEmailServiceConfig;
import com.server.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    }

    @Async
    @Override
    public void sendActivationEmail(String email, String activationLink) {
        Map<String, Object> modeMap = new HashMap<>();
        modeMap.put("email", email);
        modeMap.put("activationLink", activationLink);
        sendEmailServiceConfig.sendEmail(email, activationLink, modeMap, "SignUp");
    }

    @Async
    @Override
    public void sendEmailPurchasePointRequest(String time,String adminEmail, String transactionId, String userId,
                                              String userEmail, String name, int point, String amount) {
        Map<String, Object> modeMap = new HashMap<>();
        modeMap.put("time", time);
        modeMap.put("transactionId", transactionId);
        modeMap.put("userId", userId);
        modeMap.put("email", userEmail);
        modeMap.put("name", name);
        modeMap.put("point", String.valueOf(point));
        modeMap.put("amount", amount);
        sendEmailServiceConfig.sendEmail(adminEmail, "Purchase Point Request", modeMap, "PurchasePointRequest");
    }

    @Async
    @Override
    public void sendEmailPurchasePointResult(String time, String transactionId, String email, String name, int point,
                                             String amount, String subject, String template) {
        Map<String, Object> modeMap = new HashMap<>();
        modeMap.put("time", time);
        modeMap.put("transactionId", transactionId);
        modeMap.put("email", email);
        modeMap.put("name", name);
        modeMap.put("point", String.valueOf(point));
        modeMap.put("amount", amount);
        sendEmailServiceConfig.sendEmail(email, subject, modeMap, template);
    }
}
