package com.server.entity;

import com.server.entity.common.AuditTable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tournament")
public class Tournament extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int numberOfTeam;

    @Column(nullable = false)
    private boolean flagDisplay;

    private String description;
    private String link;
    private String status;
    private String idGame;

    @Column(columnDefinition = "INT DEFAULT NULL")
    private Integer totalPrize;

    @Column(columnDefinition = "INT DEFAULT NULL")
    private Integer top1Prize;

    @Column(columnDefinition = "INT DEFAULT NULL")
    private Integer top2Prize;

    @Column(columnDefinition = "INT DEFAULT NULL")
    private Integer top4Prize;

    @Column(columnDefinition = "INT DEFAULT NULL")
    private Integer teamMemberCount;

    @OneToMany(mappedBy = "tournament", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamTournamentParticipation> teamTournamentParticipations;

    @OneToMany(mappedBy = "tournament", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentMatch> tournamentMatches;
}