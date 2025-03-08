package com.server.service;

import com.server.dto.request.tournament.TournamentRequest;
import com.server.dto.request.tournament.UpdateStreamLinkTournamentRequest;
import com.server.dto.response.tournament.TournamentImageResponse;
import com.server.dto.response.tournament.TournamentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TournamentService {
    List<TournamentResponse> getAllTournaments();

    TournamentResponse getTournamentById(String id);

    TournamentResponse createTournament(TournamentRequest request);
    void checkAndUpdateTournamentStatus();
    TournamentResponse updateStreamLink(UpdateStreamLinkTournamentRequest request);
    TournamentImageResponse uploadImage(MultipartFile file, String tournamentId) throws IOException;
}
