package com.server.dto.response.tournament;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TournamentResponse {
    private String id;
    private String name;
    private Integer numberOfTeam;
    private boolean flagDisplay;
    private String description;
    private String link;
    private String status;
    private String idGame;
    private Integer totalPrize;
    private Integer top1Prize;
    private Integer top2Prize;
    private Integer top4Prize;
    private Integer teamMemberCount;
    private boolean thirdPlaceMatch;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String userCreate;
    private String userUpdate;
    private String streamLink;
}
