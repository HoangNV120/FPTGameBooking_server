package com.server.dto.request.match;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class MatchRequest {

    private String id;
    private String userId;
    private String codeRoom;
    private String tournamentId;
    private String teamId;
    private String teamWinId;
    private String linkVideo;
    private String description;
}
