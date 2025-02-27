package com.server.service;

import com.server.dto.request.teamtournamentparticipation.CreateTeamTournamentParticipation;
import com.server.dto.request.teamtournamentparticipation.LeaveTeamTournamentParticipation;
import com.server.dto.request.teamtournamentparticipation.UpdateTeamTournamentParticipation;
import com.server.dto.response.teamtournamentparticipation.TeamTournamentParticipationResponse;

public interface TeamTournamentParticipationService {
    TeamTournamentParticipationResponse createParticipation(CreateTeamTournamentParticipation request);

    TeamTournamentParticipationResponse updateParticipation(UpdateTeamTournamentParticipation request);

    TeamTournamentParticipationResponse leaveTournament(LeaveTeamTournamentParticipation request);
}