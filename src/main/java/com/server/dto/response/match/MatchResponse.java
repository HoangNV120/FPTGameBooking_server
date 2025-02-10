package com.server.dto.response.match;

import com.server.dto.response.team.TeamResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponse {
    private String id;
    private TeamResponse teamWin;
    private TeamResponse teamFail;
    private String linkVideo;
    private String description;
}
