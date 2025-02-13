package com.server.service;

import java.math.BigDecimal;

public interface EmailService {
     void sendEmailForgotPassword(String email, String password);

     void sendEmailPurchasePointRequest(String time,String adminEmail, String transactionId, String userId,
                                        String userEmail, String name, int point, String amount);
     void sendEmailPurchasePointResult(String time, String transactionId, String email, String name, int point,
                                       String amount, String subject, String template);
}
