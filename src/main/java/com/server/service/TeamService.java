package com.server.service;

import com.server.dto.request.team.CreateTeamRequest;
import com.server.dto.request.team.FindTeamRequest;
import com.server.dto.request.team.UpdateTeamRequest;
import com.server.dto.response.team.TeamResponse;
import com.server.service.common.BaseService;

import java.util.List;

public interface TeamService extends BaseService<TeamResponse, CreateTeamRequest, UpdateTeamRequest, FindTeamRequest> {

    TeamResponse findTeamByName(String name);

    List<TeamResponse> findAllTeamsByCodeRoom(String codeRoom);

}
