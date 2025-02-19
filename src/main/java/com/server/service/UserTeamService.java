package com.server.service;

import com.server.dto.request.userteam.CreateUserJoinTeamRequest;
import com.server.dto.response.userteam.UserRoomGameResponse;
import com.server.dto.response.userteam.UserTeamResponse;

import java.util.List;

public interface UserTeamService {

    List<UserTeamResponse> findByRoom(String codeRoom, String status, String userId);

    List<UserTeamResponse> findByUserIdAndStatusActive(String userId);

    UserTeamResponse kickMemberFromTeam(String teamId, String userId);

    UserTeamResponse processJoinTeamByRoom(CreateUserJoinTeamRequest request);

    UserTeamResponse updateStatus(String id);

    UserTeamResponse exitTeamRoom(String codeRoom, String userId);

    UserTeamResponse removeUserFromTeam(String userTeamId);

    UserRoomGameResponse findRoomCodeAndGameCodeByUserId(String userId);

}
