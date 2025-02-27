package com.server.dto.response.userteam;

import com.server.dto.response.team.TeamResponse;
import com.server.dto.response.user.UserMinimalResponse;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserTeamResponse {

    private String id;
    private UserMinimalResponse user;
    private TeamResponse team;
    private StatusEnum status;
    private RoleEnum role;
}
