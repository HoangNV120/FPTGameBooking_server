package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.NotificationEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String senderId;
    private String content;
    private String type;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;  // Người nhận thông báo

    @Enumerated(EnumType.STRING)
    private NotificationEnum statusNotification;
}
