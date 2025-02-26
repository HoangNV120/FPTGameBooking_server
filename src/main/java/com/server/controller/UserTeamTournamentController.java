package com.server.controller;

import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.userteamtournament.UserTeamTournamentResponse;
import com.server.service.UserTeamTournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-team-tournament")
@RequiredArgsConstructor
public class UserTeamTournamentController {
    private final UserTeamTournamentService userTeamTournamentService;

    @GetMapping("get-by-team-id")
    public ResponseGlobal<List<UserTeamTournamentResponse>> getByTeamId(@RequestParam String teamId) {
        List<UserTeamTournamentResponse> response = userTeamTournamentService.findByTeamId(teamId);
        return new ResponseGlobal<>(response);
    }

    @GetMapping("get-by-user-id")
    public ResponseGlobal<UserTeamTournamentResponse> getByUserId(@RequestParam String userId) {
        UserTeamTournamentResponse response = userTeamTournamentService.findByUserId(userId);
        return new ResponseGlobal<>(response);
    }
}
