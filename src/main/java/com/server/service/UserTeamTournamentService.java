package com.server.service;

import com.server.dto.request.user.UserRequest;

public interface UserTeamTournamentService {
    void leaveTeam(UserRequest userId);
}
