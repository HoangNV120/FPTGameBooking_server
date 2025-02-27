package com.server.controller;

import com.server.dto.request.match.MatchRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.match.MatchResponse;
import com.server.dto.response.match.ResultMatchResponse;
import com.server.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
public class MatchRestController {

    private final MatchService matchService;

    @PostMapping("")
    public ResponseGlobal<MatchResponse> addMatchTeamWin(@Valid @RequestBody MatchRequest request) {
        log.info("add: {}", request);
        return new ResponseGlobal<>(matchService.addMatchTeamWin(request));
    }

    @PutMapping("")
    public ResponseGlobal<MatchResponse> update(@Valid @RequestBody MatchRequest request) {
        log.info("update: {}", request);
        return new ResponseGlobal<>(matchService.updateMatch(request));
    }

    @GetMapping("/result-match")
    public ResponseGlobal<List<ResultMatchResponse>> resultMatch(@RequestParam("teamOne") String teamOne,
                                                                 @RequestParam("teamTwo") String teamTwo) {
        log.info("resultMatch: teamOne = {}, teamLose = {}", teamOne, teamTwo);
        return new ResponseGlobal<>(matchService.resultMatch(teamOne, teamTwo));
    }

}
