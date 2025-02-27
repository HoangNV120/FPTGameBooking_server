package com.server.dto.request.teamtournamentparticipation;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public abstract class BaseTeamTournamentParticipation {
    @NotNull(message = "userId is required")
    private String userId;
    @NotNull(message = "teamId is required")
    private String teamId;
    @NotNull(message = "tournamentId is required")
    private String tournamentId;
}
