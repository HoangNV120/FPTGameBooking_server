package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.ParticipationStatusEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "team_tournament_participation")
public class TeamTournamentParticipation extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamTournament team;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('PENDING', 'PARTICIPATING', 'STARTED', 'ENDED') DEFAULT 'PENDING'")
    private ParticipationStatusEnum status;

    private Integer place;
}