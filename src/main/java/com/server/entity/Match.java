package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.MatchStatusEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "matches")
public class Match extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_tournament")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "id_team_win")
    private Team teamWin;

    @ManyToOne
    @JoinColumn(name = "id_team_fail")
    private Team teamFail;

    private String linkVideo;
    private String description;

    @Enumerated(EnumType.STRING)
    private MatchStatusEnum matchStatus;
}
