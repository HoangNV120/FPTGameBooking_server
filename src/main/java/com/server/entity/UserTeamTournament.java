package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.TeamTournamentRoleEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_team_tournament", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "team_id"}))
public class UserTeamTournament extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamTournament team;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('LEADER','MEMBER') DEFAULT 'MEMBER'")
    private TeamTournamentRoleEnum teamRole;

}