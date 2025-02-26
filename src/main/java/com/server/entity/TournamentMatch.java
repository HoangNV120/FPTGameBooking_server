package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.MatchStatusEnum;
import com.server.enums.MatchTypeEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tournament_match")
public class TournamentMatch extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "team1_id")
    private TeamTournament team1;

    @ManyToOne
    @JoinColumn(name = "team2_id")
    private TeamTournament team2;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('BO1','BO3','BO5')")
    private MatchTypeEnum type;

    @Column(columnDefinition = "INT DEFAULT NULL")
    private Integer team1Score;

    @Column(columnDefinition = "INT DEFAULT NULL")
    private Integer team2Score;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('WAITING','ONGOING','ENDED') DEFAULT 'WAITING'")
    private MatchStatusEnum status;

    private String streamLink;
}