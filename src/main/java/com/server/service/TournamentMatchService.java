package com.server.service;

import com.server.dto.request.tournamentmatch.FindTournamentMatch;
import com.server.dto.request.tournamentmatch.ListSubmitTournamentMatch;
import com.server.dto.request.tournamentmatch.TournamentMatchRequest;
import com.server.dto.request.tournamentmatch.UpdateScoreTournamentMatch;
import com.server.dto.response.tournament.TournamentResponse;
import com.server.dto.response.tournamentmatch.TournamentMatchResponse;

import java.util.List;

public interface TournamentMatchService {

    List<TournamentMatchResponse> submitTournamentMatches(ListSubmitTournamentMatch request);

    TournamentMatchResponse updateMatchScore(UpdateScoreTournamentMatch request);

    List<TournamentMatchResponse> getTournamentMatches(FindTournamentMatch request);
    void checkAndUpdateMatchStatus();
}