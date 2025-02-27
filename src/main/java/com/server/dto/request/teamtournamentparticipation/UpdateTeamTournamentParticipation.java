package com.server.dto.request.teamtournamentparticipation;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateTeamTournamentParticipation extends BaseTeamTournamentParticipation {
    @NotNull(message = "status is required")
    private String status;
}
