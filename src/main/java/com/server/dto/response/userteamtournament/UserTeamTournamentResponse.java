package com.server.dto.response.userteamtournament;

import com.server.dto.response.user.UserMinimalResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTeamTournamentResponse {
    private UserMinimalResponse user;
    private String teamRole;
    private String teamId;
}
