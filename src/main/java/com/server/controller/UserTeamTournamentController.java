package com.server.controller;

import com.server.dto.request.user.UserRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.user.UserMinimalResponse;
import com.server.dto.response.userteamtournament.UserTeamTournamentResponse;
import com.server.service.UserTeamTournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/leaveClan")
    public ResponseGlobal<UserMinimalResponse> leaveClan(@RequestBody UserRequest request) {
        UserMinimalResponse userResponse = userTeamTournamentService.leaveTeam(request);
        return new ResponseGlobal<>(userResponse);
    }
}
