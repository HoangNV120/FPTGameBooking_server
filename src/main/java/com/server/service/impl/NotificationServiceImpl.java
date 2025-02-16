package com.server.service.impl;

import com.server.dto.request.notification.CreateNotificationRequest;
import com.server.dto.request.notification.FindNotificationRequest;
import com.server.dto.request.notification.UpdateNotificationRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.notification.CountNotificationResponse;
import com.server.dto.response.notification.NotificationResponse;
import com.server.entity.Notification;
import com.server.enums.NotificationEnum;
import com.server.repository.NotificationRepository;
import com.server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ModelMapper modelMapper;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public PageableObject<NotificationResponse> findAll(FindNotificationRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPageNo(), request.getPageSize());

        // Lấy danh sách thông báo theo userId, sắp xếp theo createdDate giảm dần
        Page<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedDateDesc(request.getUserId(), pageRequest);

        // Chuyển đổi danh sách Notification sang NotificationResponse trước khi cập nhật trạng thái
        List<NotificationResponse> notificationResponses = notifications.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        // Cập nhật trạng thái của thông báo từ UNREAD sang READ
        List<Notification> unreadNotifications = notifications.getContent().stream()
                .filter(n -> n.getStatusNotification() == NotificationEnum.UNREAD)
                .peek(n -> n.setStatusNotification(NotificationEnum.READ))
                .toList();

        // Lưu thông báo vào cơ sở dữ liệu
        notificationRepository.saveAll(unreadNotifications);

        // Tạo Page<NotificationResponse> từ danh sách notificationResponses
        Page<NotificationResponse> responsePage = new PageImpl<>(notificationResponses, pageRequest, notifications.getTotalElements());

        // Trả về đối tượng PageableObject đã có sẵn constructor nhận Page<T>
        return new PageableObject<>(responsePage);
    }

    @Override
    public NotificationResponse add(Notification notification) {

        // Lưu thông báo vào cơ sở dữ liệu
        Notification savedNotification = notificationRepository.save(notification);
        NotificationResponse response = convertToResponse(savedNotification);

        // Gửi thông báo tới người dùng
        simpMessagingTemplate.convertAndSend("/subscribe/add-notification/" + notification.getUser().getId(),
                new ResponseGlobal<>(response));

        return response;
    }

    @Override
    public NotificationResponse update(UpdateNotificationRequest dto) {
        Notification notification = notificationRepository.findById(dto.getId()).orElse(null);
        if (notification != null) {
            notification.setStatusNotification(dto.getStatusNotification());
            Notification updatedNotification = notificationRepository.save(notification);
            return convertToResponse(updatedNotification);
        }
        return null;
    }

    @Override
    public NotificationResponse getById(String id) {
        return null;
    }

    private NotificationResponse convertToResponse(Notification notification) {
        return modelMapper.map(notification, NotificationResponse.class);
    }

    private Notification convertToEntity(CreateNotificationRequest dto) {
        return modelMapper.map(dto, Notification.class);
    }

    @Override
    public CountNotificationResponse countNotificationUnreadByUserId(String userId) {
        return new CountNotificationResponse(notificationRepository.countByUser_IdAndStatusNotification(userId, NotificationEnum.UNREAD));
    }
}
