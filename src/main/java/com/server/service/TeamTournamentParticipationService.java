package com.server.service;

import com.server.dto.request.teamtournamentparticipation.CreateTeamTournamentParticipation;
import com.server.dto.request.teamtournamentparticipation.LeaveTeamTournamentParticipation;
import com.server.dto.request.teamtournamentparticipation.UpdateTeamTournamentParticipation;
import com.server.dto.response.teamtournamentparticipation.TeamTournamentParticipationResponse;
import com.server.entity.Tournament;

import java.util.List;

public interface TeamTournamentParticipationService {
    TeamTournamentParticipationResponse createParticipation(CreateTeamTournamentParticipation request);

    TeamTournamentParticipationResponse updateParticipation(UpdateTeamTournamentParticipation request);

    TeamTournamentParticipationResponse leaveTournament(LeaveTeamTournamentParticipation request);

    List<TeamTournamentParticipationResponse> getAllParticipationsByTournamentId(String id);

    TeamTournamentParticipationResponse getLeaderAndTeamByUserId(String userId);
    TeamTournamentParticipationResponse getParticipationByTeamId(String teamId);
    TeamTournamentParticipationResponse getParticipationByTeamIdAndTournamentId(String teamId, String tournamentId);
}