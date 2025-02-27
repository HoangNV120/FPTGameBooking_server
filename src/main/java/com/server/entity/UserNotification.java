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
@Table
public class UserNotification extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_notification")
    private Notification notification;

    @Enumerated(EnumType.STRING)
    private NotificationEnum statusNotification;

}
