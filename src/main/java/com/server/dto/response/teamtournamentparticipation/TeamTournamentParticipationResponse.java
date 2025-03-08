package com.server.dto.response.teamtournamentparticipation;

import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.entity.TeamTournament;
import com.server.enums.ParticipationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamTournamentParticipationResponse {
    private String id;
    private TeamTournamentResponse team;
    private ParticipationStatusEnum status;
    private Integer place;
    private String leaderId;
}
