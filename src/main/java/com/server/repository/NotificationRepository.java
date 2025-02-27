package com.server.repository;

import com.server.entity.Notification;
import com.server.enums.NotificationEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findByUser_IdOrderByCreatedDateDesc(String userId, Pageable pageable);

    long countByUser_IdAndStatusNotification(String userId, NotificationEnum statusNotification);
}
