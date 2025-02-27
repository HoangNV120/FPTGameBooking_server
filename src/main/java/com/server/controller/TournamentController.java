package com.server.controller;

import com.server.config.security.JwtUtils;
import com.server.dto.request.tournament.TournamentRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.tournament.TournamentResponse;
import com.server.service.TournamentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tournament")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;
    private final JwtUtils jwtUtils;

    @PostMapping("/create")
    public ResponseGlobal<TournamentResponse> createTournament(@Valid @RequestBody TournamentRequest request,
                                                               HttpServletRequest httpRequest) {
        String jwt = jwtUtils.getJwtFromHeader(httpRequest);
        String userIdFromJwt = jwtUtils.getUserIdFromJwtToken(jwt);

        if (!userIdFromJwt.equals(request.getUserId())) {
            return new ResponseGlobal<>(HttpStatus.UNAUTHORIZED.value(),
                    "Không thể tạo giải đấu cho tài khoản khác",
                    ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        }

        TournamentResponse tournamentResponse = tournamentService.createTournament(request);
        return new ResponseGlobal<>(tournamentResponse);
    }

    @GetMapping("/{id}")
    public ResponseGlobal<TournamentResponse> getTournament(@PathVariable String id) {
        TournamentResponse tournamentResponse = tournamentService.getTournamentById(id);
        return new ResponseGlobal<>(tournamentResponse);
    }

    @GetMapping
    public ResponseGlobal<List<TournamentResponse>> getAllTournament() {
        List<TournamentResponse> tournamentResponse = tournamentService.getAllTournaments();
        return new ResponseGlobal<>(tournamentResponse);
    }
}