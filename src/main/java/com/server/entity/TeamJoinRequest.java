package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.RequestStatusEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "team_join_request", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "team_id"}))
public class TeamJoinRequest extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamTournament team;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING'")
    private RequestStatusEnum status;

    @Column(columnDefinition = "text")
    private String description;
}