package com.server.controller;

import com.server.dto.request.user.UserRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.user.UserResponse;
import com.server.service.UserTeamTournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-team-tournament")
@RequiredArgsConstructor
public class UserTeamTournamentController {
  private final UserTeamTournamentService userTeamTournamentService;

  @PostMapping("/leaveClan")
  public ResponseGlobal<UserResponse> leaveClan(@Valid @RequestBody UserRequest user){
    UserResponse userResponse = userTeamTournamentService.leaveTeam(user);
    return new ResponseGlobal<>(userResponse);
  }
}
