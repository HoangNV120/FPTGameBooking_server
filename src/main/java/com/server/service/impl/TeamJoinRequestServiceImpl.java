package com.server.service.impl;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.response.teamjoinrequest.TeamJoinRespone;
import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.entity.TeamJoinRequest;
import com.server.entity.TeamTournament;
import com.server.entity.User;
import com.server.entity.UserTeamTournament;
import com.server.enums.RequestStatusEnum;
import com.server.enums.TeamTournamentRoleEnum;
import com.server.exceptions.RestApiException;
import com.server.repository.TeamJoinRequestRepository;
import com.server.repository.TeamTournamentRepository;
import com.server.repository.UserRepository;
import com.server.repository.UserTeamTournamentRepository;
import com.server.service.TeamJoinRequestService;
import java.util.List;
import java.util.stream.Collectors;
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
  private final UserTeamTournamentRepository userTeamTournamentRepository;

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

  @Override
  public List<TeamJoinRespone> viewTeamJoinRequest(String userId) {
    User user = userRepository.findById(userId).orElseThrow(()-> new RestApiException("User Not Found"));
    TeamTournament team = userTeamTournamentRepository.getTeamTournamentByUserId(user.getId()).orElseThrow(()-> new RestApiException("Team Not Found"));

    TeamTournamentRoleEnum roleUser = userTeamTournamentRepository.findByUser(user)
        .map(UserTeamTournament::getTeamRole)
        .orElse(null);

    if(roleUser.equals(TeamTournamentRoleEnum.LEADER)){
      List<TeamJoinRequest> requestJoin = teamJoinRequestRepository.findByTeamId(team.getId());
      return requestJoin.stream()
          .map(this::convertToResponse)  // Gọi phương thức convert từng phần tử
          .collect(Collectors.toList()); // Thu thập lại thành List;
    }else {
      new RestApiException("Member cannot view request join Clan!");
    }
    return null;
  }

  private TeamJoinRespone convertToResponse(TeamJoinRequest teamJoinRequest) {
    return modelMapper.map(teamJoinRequest, TeamJoinRespone.class);
  }

  private TeamJoinRequest convertToResponse(TeamJoinRespone teamJoinRespone) {
    return modelMapper.map(teamJoinRespone, TeamJoinRequest.class);
  }
}
