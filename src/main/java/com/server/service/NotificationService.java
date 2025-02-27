package com.server.service;

import com.server.dto.request.notification.FindNotificationRequest;
import com.server.dto.request.notification.UpdateNotificationRequest;
import com.server.dto.response.notification.CountNotificationResponse;
import com.server.dto.response.notification.NotificationResponse;
import com.server.entity.Notification;
import com.server.service.common.BaseService;

public interface NotificationService extends BaseService<NotificationResponse,
        Notification, UpdateNotificationRequest, FindNotificationRequest> {

    CountNotificationResponse countNotificationUnreadByUserId(String userId);
}
