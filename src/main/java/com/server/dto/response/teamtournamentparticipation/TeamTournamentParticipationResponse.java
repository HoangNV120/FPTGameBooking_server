package com.server.dto.response.teamtournamentparticipation;

import com.server.entity.TeamTournament;
import com.server.enums.ParticipationStatusEnum;

public class TeamTournamentParticipationResponse {
    private String id;
    private TeamTournament team;
    private ParticipationStatusEnum status;
}
