package com.server.dto.request.tournament;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStreamLinkTournamentRequest {
    private String tournamentId;
    private String streamLink;
    private String userId;
}
