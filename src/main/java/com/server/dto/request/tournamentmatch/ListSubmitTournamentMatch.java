package com.server.dto.request.tournamentmatch;

import lombok.Getter;

import java.util.List;

@Getter
public class ListSubmitTournamentMatch {
    private String userId;
    private String tournamentId;
    private List<SubmitTournamentMatchRequest> matchRequests;
}
