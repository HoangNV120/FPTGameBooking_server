package com.server.service;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.response.teamjoinrequest.TeamJoinRespone;
import com.server.entity.TeamJoinRequest;

public interface TeamJoinRequestService {
  TeamJoinRespone sendTeamJoinRequest(TeamJoinRequestDTO request);
}
