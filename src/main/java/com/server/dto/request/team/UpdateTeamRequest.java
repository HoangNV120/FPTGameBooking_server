package com.server.dto.request.team;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UpdateTeamRequest extends BaseTeam {
    private String id;
}
