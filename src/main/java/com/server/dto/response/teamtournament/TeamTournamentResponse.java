package com.server.dto.response.teamtournament;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamTournamentResponse {
    private String id;
    private String name;
    private int memberCount;
    private String description;
    private String imageLink;
    private int recentMemberCount;
}
