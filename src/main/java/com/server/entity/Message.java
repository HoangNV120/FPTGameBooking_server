package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.MessageStatusEnum;
import com.server.enums.MessageTypeEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Message extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_user_send", nullable = false)
    private User userSend;

    @ManyToOne
    @JoinColumn(name = "id_user_receive")
    private User userReceive;

    @ManyToOne
    @JoinColumn(name = "id_team")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "id_room")
    private Room room;

    @Enumerated(EnumType.STRING)
    private MessageTypeEnum messageType;
    @Enumerated(EnumType.STRING)
    private MessageStatusEnum messageStatus;
    private String content;
}
