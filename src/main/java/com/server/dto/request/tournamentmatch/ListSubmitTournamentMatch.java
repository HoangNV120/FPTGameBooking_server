package com.server.dto.request.tournamentmatch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListSubmitTournamentMatch {
    private String userId;
    private String tournamentId;
    private List<SubmitTournamentMatchRequest> matchRequests;
}
