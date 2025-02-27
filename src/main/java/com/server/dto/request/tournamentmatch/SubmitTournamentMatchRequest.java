package com.server.dto.request.tournamentmatch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitTournamentMatchRequest {
    private String teamId;
    private int order;
}
