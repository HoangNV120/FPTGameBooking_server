package com.server.dto.response.tournament;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TournamentResponse {
    private String id;
    private String name;
    private int numberOfTeam;
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
}
