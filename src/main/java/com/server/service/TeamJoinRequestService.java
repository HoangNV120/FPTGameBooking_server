package com.server.service;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.request.teamjoinrequest.UpdateStatusTeamJoinRequest;
import com.server.dto.request.user.UserRequest;
import com.server.dto.response.teamjoinrequest.TeamJoinRespone;
import com.server.dto.response.user.UserReponseClan;
import com.server.entity.TeamJoinRequest;
import com.server.enums.RequestStatusEnum;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface TeamJoinRequestService {
  TeamJoinRespone sendTeamJoinRequest(TeamJoinRequestDTO request);
  List<TeamJoinRespone> viewTeamJoinRequest(String userId);
  void updateStatusTeamJoinRequest(boolean status, String userId, String leader,String teamId);
}
