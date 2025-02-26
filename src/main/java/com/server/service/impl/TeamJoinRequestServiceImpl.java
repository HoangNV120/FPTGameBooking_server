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
import javax.swing.text.html.Option;
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

    Optional<TeamTournament> userExisted = userTeamTournamentRepository.getTeamTournamentByUserId(user.getId());
    if(userExisted.isPresent()){
      throw new RestApiException("User already has a Team!");
    }

    Optional<TeamJoinRequest> teamJoinRequestCheck = teamJoinRequestRepository.findTeamJoinRequestByUserIdAndTeamId(requestDTO.getUserId(), requestDTO.getTeamId());
    if(teamJoinRequestCheck.isPresent()){
      throw new RestApiException("User send a request join a team");
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

    if(TeamTournamentRoleEnum.LEADER.equals(roleUser)){
      List<TeamJoinRequest> requestJoin = teamJoinRequestRepository.findByTeamId(team.getId());
      return requestJoin.stream()
          .map(this::convertToResponse)  // Gọi phương thức convert từng phần tử
          .collect(Collectors.toList()); // Thu thập lại thành List;
    }else {
      throw new RestApiException("Member cannot view request join Clan!");
    }
  }

  @Override
  @Transactional
  public TeamJoinRespone ResponseTeamJoinRequest(boolean status, String userId, String leader,String teamId) {
    try{
      User user = userRepository.findById(userId).orElseThrow(()-> new RestApiException("User Not Found"));
      User leader1 = userRepository.findById(leader).orElseThrow(()-> new RestApiException("Leader Not Found"));
      TeamJoinRequest team = teamJoinRequestRepository.findTeamJoinRequestByUserIdAndTeamId(userId,teamId).orElseThrow(()-> new RestApiException("Join request not found"));

      // dung leader moi co the accept or reject
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
      if(status){
        teamJoinRequestRepository.deleteTeamJoinRequestsByUserId(userId);
        userTeamTournamentRepository.addUserToTeamTournament(userId,teamId,TeamTournamentRoleEnum.MEMBER);
      }else{
        teamJoinRequestRepository.deleteTeamJoinRequestByUserIdAndTeamId(userId,teamId);
      }
      return convertToResponse(team);
    }catch (Exception e){
      throw new RestApiException("Failed to response user join request");
    }
  }

  private TeamJoinRespone convertToResponse(TeamJoinRequest teamJoinRequest) {
    return modelMapper.map(teamJoinRequest, TeamJoinRespone.class);
  }
}
