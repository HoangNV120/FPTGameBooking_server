package com.server.dto.request.teamtournament;

import com.server.dto.request.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindTeamTournament extends PageableRequest {
    private String name;
    private String countUserTeamOrder;
}
