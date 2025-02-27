package com.server.controller;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.teamjoinrequest.TeamJoinRespone;
import com.server.service.TeamJoinRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/team-join-request")
@RequiredArgsConstructor
public class TeamJoinRequestController {
    private final TeamJoinRequestService teamJoinRequestService;

    @PostMapping("/requestJoinClan")
    public ResponseGlobal<TeamJoinRespone> requestJoinClan(@RequestBody TeamJoinRequestDTO request) {
        TeamJoinRespone teamJoinRespone = teamJoinRequestService.sendTeamJoinRequest(request);
        return new ResponseGlobal<>(teamJoinRespone);
    }

    @GetMapping("/viewRequest")
    public ResponseGlobal<List<TeamJoinRespone>> viewRequest(@RequestParam("userId") String userId) {
        List<TeamJoinRespone> teamJoinResponse = teamJoinRequestService.viewTeamJoinRequest(userId);
        return new ResponseGlobal<>(teamJoinResponse);
    }

    @PostMapping("/updateStatus")
    public ResponseGlobal<TeamJoinRespone> updateStatus(@RequestParam("status") boolean status, @RequestParam("userId") String userId, @RequestParam("leaderId") String leaderId, @RequestParam("teamId") String teamId) {
        TeamJoinRespone teamJoinRespone = teamJoinRequestService.ResponseTeamJoinRequest(status, userId, leaderId, teamId);
        return new ResponseGlobal<>(teamJoinRespone);
    }
}
