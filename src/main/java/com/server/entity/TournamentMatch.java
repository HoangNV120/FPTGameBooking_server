package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.MatchStageEnum;
import com.server.enums.MatchTypeEnum;
import com.server.enums.TournamentMatchStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private TeamTournamentParticipation team1;

    @ManyToOne
    @JoinColumn(name = "team2_id")
    private TeamTournamentParticipation team2;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('BO1','BO3','BO5')")
    private MatchTypeEnum type;

    @Column(columnDefinition = "INT DEFAULT NULL")
    private Integer team1Score;

    @Column(columnDefinition = "INT DEFAULT NULL")
    private Integer team2Score;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('WAITING','ONGOING','ENDED') DEFAULT 'WAITING'")
    private TournamentMatchStatusEnum status;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('QUARTER_FINALS', 'SEMI_FINALS', 'FINALS')")
    private MatchStageEnum stage;

    private int matchOrder;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String streamLink;
}