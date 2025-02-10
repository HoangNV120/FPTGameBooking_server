package com.server.dto.response.match;

import org.springframework.beans.factory.annotation.Value;

public interface ResultMatchResponse {

    @Value("#{target.team_id}")
    String getTeamId();

    @Value("#{target.team_name}")
    String getTeamName();

    @Value("#{target.win_count}")
    Integer getWinCount();

    @Value("#{target.lose_count}")
    Integer getLoseCount();
}

