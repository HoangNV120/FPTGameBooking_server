package com.server.entity;

import com.server.entity.common.AuditTable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tournament_join_request", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"team_id", "tournament_id"})
})
public class TournamentJoinRequest extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamTournament team;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;
}