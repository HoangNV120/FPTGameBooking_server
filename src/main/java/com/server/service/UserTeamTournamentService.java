package com.server.service;

import com.server.dto.response.userteamtournament.UserTeamTournamentResponse;

import java.util.List;

public interface UserTeamTournamentService {
    List<UserTeamTournamentResponse> findByTeamId(String id);
    UserTeamTournamentResponse findByUserId(String userId);
}
