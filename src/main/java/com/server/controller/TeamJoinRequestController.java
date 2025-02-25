package com.server.controller;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.teamjoinrequest.TeamJoinRespone;
import com.server.entity.TeamJoinRequest;
import com.server.service.TeamJoinRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/team-join-request")
@RequiredArgsConstructor
public class TeamJoinRequestController {
  private final TeamJoinRequestService teamJoinRequestService;

  @PostMapping("/requestJoinClan")
  public ResponseGlobal<TeamJoinRespone> requestJoinClan(@RequestBody TeamJoinRequestDTO request)
  {
    TeamJoinRespone teamJoinRespone = teamJoinRequestService.sendTeamJoinRequest(request);
    return new ResponseGlobal<>(teamJoinRespone);
  }
}
