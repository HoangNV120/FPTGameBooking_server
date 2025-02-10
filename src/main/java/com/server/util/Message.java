package com.server.util;

import java.util.HashMap;
import java.util.Map;

public final class Message {

    public static String messages(String key) {
        Map<String, String> messages = new HashMap<>();
        messages.put("phone.valid", "Phone number is in an invalid format");
        messages.put("password.valid", "Password is invalid");
        messages.put("email.valid", "Email is invalid");
        messages.put("fullName.not.blank", "Full name cannot be blank");
        messages.put("201", "Created successfully");
        messages.put("200", "Success");
        messages.put("400", "Failed to create");
        messages.put("500", "System error =))");
        messages.put("404", "%s does not exist!");
        messages.put("403", "Your account does not have access rights!");
        messages.put("409", "%s already exists!");
        return messages.get(key);

    }
}
