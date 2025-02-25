package com.server.service.impl;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.response.teamjoinrequest.TeamJoinRespone;
import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.entity.TeamJoinRequest;
import com.server.entity.TeamTournament;
import com.server.entity.User;
import com.server.enums.RequestStatusEnum;
import com.server.exceptions.RestApiException;
import com.server.repository.TeamJoinRequestRepository;
import com.server.repository.TeamTournamentRepository;
import com.server.repository.UserRepository;
import com.server.service.TeamJoinRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamJoinRequestServiceImpl implements TeamJoinRequestService {

  private final TeamJoinRequestRepository teamJoinRequestRepository;
  private final UserRepository userRepository;
  private final TeamTournamentRepository teamTournamentRepository;
  private final ModelMapper modelMapper;

  @Override
  public TeamJoinRespone sendTeamJoinRequest(TeamJoinRequestDTO requestDTO ) {
    User user= userRepository.findById(requestDTO.getUserId()).orElseThrow(()-> new RestApiException("User Not Found"));
    TeamTournament team = teamTournamentRepository.findById(requestDTO.getTeamId()).orElseThrow(()-> new RestApiException("Team Not Found"));
    List<TeamJoinRequest> request = teamJoinRequestRepository.findByUserIdAndTeamId(requestDTO.getUserId(), requestDTO.getTeamId());
    if(!request.isEmpty()){
      throw new RestApiException("User has already requested to join this team!");
    }
    TeamJoinRequest teamJoinRequest = TeamJoinRequest.builder()
        .user(user)
        .team(team)
        .description(requestDTO.getDescription())
        .status(RequestStatusEnum.PENDING)
        .build();
    teamJoinRequestRepository.save(teamJoinRequest);
    return convertToResponse(teamJoinRequest);
  }

  private TeamJoinRespone convertToResponse(TeamJoinRequest teamJoinRequest) {
    return modelMapper.map(teamJoinRequest, TeamJoinRespone.class);
  }

  private TeamJoinRequest convertToResponse(TeamJoinRespone teamJoinRespone) {
    return modelMapper.map(teamJoinRespone, TeamJoinRequest.class);
  }
}
