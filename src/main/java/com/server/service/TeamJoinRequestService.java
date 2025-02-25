package com.server.service;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.request.user.UserRequest;
import com.server.dto.response.teamjoinrequest.TeamJoinRespone;
import com.server.dto.response.user.UserReponseClan;
import com.server.entity.TeamJoinRequest;
import java.util.List;

public interface TeamJoinRequestService {
  TeamJoinRespone sendTeamJoinRequest(TeamJoinRequestDTO request);
  List<TeamJoinRespone> viewTeamJoinRequest(String userId);
}
