package com.server.dto.response.tournamentmatch;

import com.server.dto.response.teamtournamentparticipation.TeamTournamentParticipationResponse;
import com.server.enums.MatchStatusEnum;
import com.server.enums.MatchTypeEnum;

public class TournamentMatchResponse {
    private String id;
    private TeamTournamentParticipationResponse team1;
    private TeamTournamentParticipationResponse team2;
    private MatchTypeEnum type;
    private Integer team1Score;
    private Integer team2Score;
    private MatchStatusEnum status;
    private String streamLink;
}
