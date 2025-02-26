package com.server.service;

import com.server.dto.request.user.UserRequest;
import com.server.dto.response.user.UserResponse;

public interface UserTeamTournamentService {
    UserResponse leaveTeam(UserRequest userId);
}
