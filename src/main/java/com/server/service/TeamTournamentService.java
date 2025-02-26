package com.server.service;

import com.server.dto.request.teamtournament.CreateTeamTournament;
import com.server.dto.request.teamtournament.FindTeamTournament;
import com.server.dto.request.teamtournament.UpdateTeamTournament;
import com.server.dto.request.user.UserRequest;
import com.server.dto.response.teamtournament.TeamTournamentImageResponse;
import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.entity.TeamTournament;
import com.server.entity.UserTeamTournament;
import com.server.enums.TeamTournamentRoleEnum;
import com.server.service.common.BaseService;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TeamTournamentService extends BaseService<TeamTournamentResponse, CreateTeamTournament, UpdateTeamTournament, FindTeamTournament> {
    TeamTournamentImageResponse uploadImage(MultipartFile file, String teamId) throws IOException;
}
