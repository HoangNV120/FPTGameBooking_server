package com.server.service;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.response.teamjoinrequest.TeamJoinRespone;

import java.util.List;

public interface TeamJoinRequestService {
    TeamJoinRespone sendTeamJoinRequest(TeamJoinRequestDTO request);

    List<TeamJoinRespone> viewTeamJoinRequest(String userId);

    TeamJoinRespone ResponseTeamJoinRequest(boolean status, String userId, String leader, String teamId);
}
