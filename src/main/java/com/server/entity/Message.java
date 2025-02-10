package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.MessageStatusEnum;
import com.server.enums.MessageTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
