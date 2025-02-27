package com.server.service;

import com.server.dto.request.match.MatchRequest;
import com.server.dto.response.match.MatchResponse;
import com.server.dto.response.match.ResultMatchResponse;

import java.util.List;

public interface MatchService {

    MatchResponse addMatchTeamWin(MatchRequest request);

    MatchResponse updateMatch(MatchRequest request);

    List<ResultMatchResponse> resultMatch(String teamWinId, String teamLoseId);
}
