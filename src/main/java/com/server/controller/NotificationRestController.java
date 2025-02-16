// NotificationRestController.java
package com.server.controller;

import com.server.config.security.JwtUtils;
import com.server.dto.request.notification.CountUnreadNotificationRequest;
import com.server.dto.request.notification.FindNotificationRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.notification.CountNotificationResponse;
import com.server.dto.response.notification.NotificationResponse;
import com.server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotificationService notificationService;
    private final JwtUtils jwtUtils;

    @PostMapping("/view")
    public ResponseGlobal<PageableObject<NotificationResponse>> view(@RequestBody FindNotificationRequest request, HttpServletRequest httpRequest) {
        String jwt = jwtUtils.getJwtFromHeader(httpRequest);
        String userIdFromJwt = jwtUtils.getUserIdFromJwtToken(jwt);

        if (!userIdFromJwt.equals(request.getUserId())) {
            return new ResponseGlobal<>(HttpStatus.UNAUTHORIZED.value(),
                    "Không thể truy cập vào thông báo của tài khoản khác",
                    LocalDateTime.now().plusHours(7));
        }

        return new ResponseGlobal<>(notificationService.findAll(request));
    }

    @PostMapping("/count-unread")
    public ResponseGlobal<CountNotificationResponse> countUnread(@RequestBody CountUnreadNotificationRequest request, HttpServletRequest httpRequest) {
        String jwt = jwtUtils.getJwtFromHeader(httpRequest);
        String userIdFromJwt = jwtUtils.getUserIdFromJwtToken(jwt);

        if (!userIdFromJwt.equals(request.getUserId())) {
            return new ResponseGlobal<>(HttpStatus.UNAUTHORIZED.value(),
                    "Không thể truy cập vào thông báo của tài khoản khác", LocalDateTime.now().plusHours(7));
        }

        return new ResponseGlobal<>(notificationService.countNotificationUnreadByUserId(request.getUserId()));
    }
}