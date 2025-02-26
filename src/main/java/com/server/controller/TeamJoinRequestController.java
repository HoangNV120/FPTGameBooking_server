package com.server.controller;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.request.teamjoinrequest.UpdateStatusTeamJoinRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.teamjoinrequest.TeamJoinRespone;
import com.server.entity.TeamJoinRequest;
import com.server.service.TeamJoinRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/viewRequest")
  public ResponseGlobal<List<TeamJoinRespone>> viewRequest(@RequestParam("userId") String userId){
    List<TeamJoinRespone> teamJoinResponse = teamJoinRequestService.viewTeamJoinRequest(userId);
    return new ResponseGlobal<>(teamJoinResponse);
  }

  @PostMapping("/updateStatus")
  public ResponseEntity<Void> updateStatus(@RequestParam("status")boolean status,@RequestParam("userId") String userId,@RequestParam("leaderId") String leaderId,@RequestParam("teamId") String teamId){
    teamJoinRequestService.updateStatusTeamJoinRequest(status,userId,leaderId,teamId);
    return ResponseEntity.ok().build();
  }
}
