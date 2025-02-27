package com.server.controller;

import com.server.dto.request.tournamentmatch.ListSubmitTournamentMatch;
import com.server.dto.request.tournamentmatch.TournamentMatchRequest;
import com.server.dto.request.tournamentmatch.UpdateScoreTournamentMatch;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.tournamentmatch.TournamentMatchResponse;
import com.server.service.TournamentMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tournament-match")
@RequiredArgsConstructor
public class TournamentMatchController {

    private final TournamentMatchService tournamentMatchService;


    @PostMapping("/submit")
    public ResponseGlobal<List<TournamentMatchResponse>> submitTournamentMatches(@RequestBody ListSubmitTournamentMatch request) {
        List<TournamentMatchResponse> response = tournamentMatchService.submitTournamentMatches(request);
        return new ResponseGlobal<>(response);
    }

    @PutMapping("/update-score")
    public ResponseGlobal<TournamentMatchResponse> updateMatchScore(@RequestBody UpdateScoreTournamentMatch request) {
        TournamentMatchResponse response = tournamentMatchService.updateMatchScore(request);
        return new ResponseGlobal<>(response);
    }
}