package com.server.repository;

import com.server.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationReponsitory extends JpaRepository<UserNotification, String> {
}
