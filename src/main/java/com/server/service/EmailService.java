package com.server.service;


public interface EmailService {

    void sendEmailPurchasePointRequest(String time, String adminEmail, String transactionId, String userId,
                                       String userEmail, String name, int point, String amount);

    void sendEmailPurchasePointResult(String time, String transactionId, String email, String name, int point,
                                      String amount, String subject, String template);

    void sendEmail(String email, String password);

    void sendActivationEmail(String email, String activeLink);
}
