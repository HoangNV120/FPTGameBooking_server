package com.server.controller;

import com.server.dto.request.teamtournament.CreateTeamTournament;
import com.server.dto.request.teamtournament.FindTeamTournament;
import com.server.dto.request.teamtournament.UpdateTeamTournament;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.teamtournament.TeamTournamentImageResponse;
import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.service.TeamTournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/team-tournament")
@RequiredArgsConstructor
public class TeamTournamentController {
    private final TeamTournamentService teamTournamentService;

    @GetMapping
    public ResponseGlobal<PageableObject<TeamTournamentResponse>> findAll(FindTeamTournament request) {
        PageableObject<TeamTournamentResponse> response = teamTournamentService.findAll(request);
        return new ResponseGlobal<>(response);
    }

    @PostMapping
    public ResponseGlobal<TeamTournamentResponse> add(@Valid @RequestBody CreateTeamTournament request) {
        TeamTournamentResponse response = teamTournamentService.add(request);
        return new ResponseGlobal<>(response);
    }

    @PutMapping
    public ResponseGlobal<TeamTournamentResponse> update(@Valid @RequestBody UpdateTeamTournament request) {
        TeamTournamentResponse response = teamTournamentService.update(request);
        return new ResponseGlobal<>(response);
    }

    @GetMapping("/{id}")
    public ResponseGlobal<TeamTournamentResponse> getById(@PathVariable String id) {
        TeamTournamentResponse response = teamTournamentService.getById(id);
        return new ResponseGlobal<>(response);
    }

    @PostMapping(value = "/upload-image", consumes = {"multipart/form-data"})
    public ResponseGlobal<TeamTournamentImageResponse> uploadImage(@RequestParam("teamId") String teamId,
                                                                   @RequestParam("file") MultipartFile file) throws IOException {
        TeamTournamentImageResponse response = teamTournamentService.uploadImage(file, teamId);
        return new ResponseGlobal<>(response);
    }
}
