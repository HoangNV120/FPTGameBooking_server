package com.server.service.impl;

import com.server.dto.request.teamjoinrequest.TeamJoinRequestDTO;
import com.server.dto.request.teamjoinrequest.UpdateStatusTeamJoinRequest;
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
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
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
    Optional<TeamTournament> userExisted = userTeamTournamentRepository.getTeamTournamentByUserId(user.getId());
    if(userExisted.isPresent()){
      throw new RestApiException("User already has a Team!");
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
      List<TeamJoinRequest> requestJoin = teamJoinRequestRepository.findByTeamIdAndStatus(team.getId(), RequestStatusEnum.PENDING);
      return requestJoin.stream()
          .map(this::convertToResponse)  // Gọi phương thức convert từng phần tử
          .collect(Collectors.toList()); // Thu thập lại thành List;
    }else {
      new RestApiException("Member cannot view request join Clan!");
    }
    return null;
  }

  @Override
  @Transactional
  public TeamJoinRespone updateStatusTeamJoinRequest(boolean status, String userId, String leader,String teamId) {
    try{
      User user = userRepository.findById(userId).orElseThrow(()-> new RestApiException("User Not Found"));
      User leader1 = userRepository.findById(leader).orElseThrow(()-> new RestApiException("Leader Not Found"));
      TeamJoinRequest team = teamJoinRequestRepository.findTeamJoinRequestByUserIdAndTeamId(userId,teamId);

      TeamTournamentRoleEnum roleUser = userTeamTournamentRepository.findByUser(leader1)
          .map(UserTeamTournament::getTeamRole)
          .orElse(null);

      UserTeamTournament leaderTeamInfo = userTeamTournamentRepository
          .findByUserAndTeamId(leader1, teamId)
          .orElseThrow(() -> new RestApiException("Leader is not in this team"));

      if (!TeamTournamentRoleEnum.LEADER.equals(leaderTeamInfo.getTeamRole())) {
        throw new RestApiException("User is not the leader of this team");
      }

      // Kiểm tra lại xem user đã có đội chưa
      Optional<TeamTournament> existingTeam = userTeamTournamentRepository.getTeamTournamentByUserIdWithLock(userId);
      if (existingTeam.isPresent()) {
        throw new RestApiException("User is already in another team");
      }
      //Kiem tra xem user da duoc duyet vao team nao hay chua
      List<TeamJoinRequest> pending = teamJoinRequestRepository.findTeamJoinRequestsByUserIdAndStatus(userId,RequestStatusEnum.PENDING);
      if(pending.isEmpty()){
        throw new RestApiException("User is already in another team");
      }
      if(TeamTournamentRoleEnum.LEADER.equals(roleUser)){
        if(status){
          teamJoinRequestRepository.approveTeamJoinRequest(RequestStatusEnum.ACCEPTED,userId, teamId);
          teamJoinRequestRepository.rejectOtherTeamJoinRequests(RequestStatusEnum.REJECTED,userId, teamId);
        }else{
          teamJoinRequestRepository.rejectTeamJoinRequests(RequestStatusEnum.REJECTED,userId, teamId);
        }
      }
      return convertToResponse(team);
    }catch (Exception e){
      throw new RestApiException("Failed to update team join request");
    }
  }

  private TeamJoinRespone convertToResponse(TeamJoinRequest teamJoinRequest) {
    return modelMapper.map(teamJoinRequest, TeamJoinRespone.class);
  }

  private TeamJoinRequest convertToResponse(TeamJoinRespone teamJoinRespone) {
    return modelMapper.map(teamJoinRespone, TeamJoinRequest.class);
  }
}
