package com.server.dto.response.notification;

import com.server.dto.response.user.UserMinimalResponse;
import com.server.enums.NotificationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private String id;
    private String senderId;
    private String content;
    private String type;
    private UserMinimalResponse user;
    private NotificationEnum statusNotification;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;


}
