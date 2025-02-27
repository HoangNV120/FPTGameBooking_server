package com.server.service;

import com.server.dto.request.tournament.TournamentRequest;
import com.server.dto.response.tournament.TournamentResponse;

import java.util.List;

public interface TournamentService {
    List<TournamentResponse> getAllTournaments();

    TournamentResponse getTournamentById(String id);

    TournamentResponse createTournament(TournamentRequest request);
}
