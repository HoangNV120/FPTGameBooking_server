package com.server.controller;

import com.server.dto.request.teamtournamentparticipation.CreateTeamTournamentParticipation;
import com.server.dto.request.teamtournamentparticipation.LeaveTeamTournamentParticipation;
import com.server.dto.request.teamtournamentparticipation.UpdateTeamTournamentParticipation;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.teamtournamentparticipation.TeamTournamentParticipationResponse;
import com.server.service.TeamTournamentParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/team-tournament-participations")
@RequiredArgsConstructor
public class TeamTournamentParticipationController {

    private final TeamTournamentParticipationService participationService;

    @PostMapping
    public ResponseGlobal<TeamTournamentParticipationResponse> createParticipation(
            @RequestBody CreateTeamTournamentParticipation request) {
        TeamTournamentParticipationResponse response = participationService.createParticipation(request);
        return new ResponseGlobal<>(response);
    }

    @PutMapping
    public ResponseGlobal<TeamTournamentParticipationResponse> updateParticipation(
            @RequestBody UpdateTeamTournamentParticipation request) {
        TeamTournamentParticipationResponse response = participationService.updateParticipation(request);
        return new ResponseGlobal<>(response);
    }

    @PostMapping("/leave")
    public ResponseGlobal<TeamTournamentParticipationResponse> leaveTournament(
            @RequestBody LeaveTeamTournamentParticipation request) {
        TeamTournamentParticipationResponse response = participationService.leaveTournament(request);
        return new ResponseGlobal<>(response);
    }
}