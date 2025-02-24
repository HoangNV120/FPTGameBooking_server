package com.server.service;

import com.server.dto.request.teamtournament.CreateTeamTournament;
import com.server.dto.request.teamtournament.FindTeamTournament;
import com.server.dto.request.teamtournament.UpdateTeamTournament;
import com.server.dto.response.teamtournament.TeamTournamentImageResponse;
import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.service.common.BaseService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TeamTournamentService extends BaseService<TeamTournamentResponse, CreateTeamTournament, UpdateTeamTournament, FindTeamTournament> {
    TeamTournamentImageResponse uploadImage(MultipartFile file, String teamId) throws IOException;
}
