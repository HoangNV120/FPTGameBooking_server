package com.server.dto.request.tournamentmatch;

import lombok.Getter;

@Getter
public class UpdateScoreTournamentMatch {
    private String tournamentMatchId;
    private Integer team1Score;
    private Integer team2Score;
}
