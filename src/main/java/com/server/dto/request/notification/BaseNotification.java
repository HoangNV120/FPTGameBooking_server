package com.server.dto.request.notification;

import com.server.dto.response.user.UserMinimalResponse;
import com.server.entity.User;
import com.server.enums.NotificationEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class BaseNotification {
    private String id;
    private String senderId;
    private String content;
    private String type;
    private User user;
    private NotificationEnum statusNotification;
}
