package com.server.service.impl;

import com.server.dto.request.user.UserRequest;
import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.entity.TeamTournament;
import com.server.entity.User;
import com.server.entity.UserTeamTournament;
import com.server.enums.TeamTournamentRoleEnum;
import com.server.exceptions.RestApiException;
import com.server.repository.TeamTournamentRepository;
import com.server.repository.UserRepository;
import com.server.repository.UserTeamTournamentRepository;
import com.server.service.UserTeamTournamentService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserTeamTournamentServiceImpl implements UserTeamTournamentService {

  private final ModelMapper modelMapper;
  private final TeamTournamentRepository teamTournamentRepository;
  private final UserTeamTournamentRepository userTeamTournamentRepository;
  private final UserRepository UserRepository;

  @Override
  @Transactional
  public void leaveTeam(UserRequest request) {
    User user = UserRepository.findById(request.getId()).orElseThrow(()-> new RestApiException("User not found"));

    TeamTournament team = userTeamTournamentRepository.getTeamTournamentByUserId(user.getId()).orElseThrow(()->new RestApiException("Team not found"));

    TeamTournamentRoleEnum roleUser = userTeamTournamentRepository.findByUser(user)
        .map(UserTeamTournament::getTeamRole)
        .orElse(null);

    int memberCount = userTeamTournamentRepository.countByTeamId(team.getId());

    if(memberCount <= 1)
    {
      //xoa người dùng cu the ra khoi team
      userTeamTournamentRepository.deleteByUserAndTeam(user, team);

      // xoa teamUser ra khoi bang team
      userTeamTournamentRepository.deleteUserTeamTournamentByTeam(team);
      team.setDeleted(true);
      teamTournamentRepository.save(team);
      return;
    }

    UserTeamTournament newLeader = null;
    if(roleUser.equals(TeamTournamentRoleEnum.LEADER)){
      List<UserTeamTournament> users = userTeamTournamentRepository.findByTeamAndTeamRoleNotOrderByCreatedDateAsc(team,TeamTournamentRoleEnum.LEADER, PageRequest.of(0,1));
      if(!users.isEmpty()){
        newLeader = users.get(0);
      }
    }
    userTeamTournamentRepository.deleteByUserAndTeam(user, team);

    if (newLeader != null) {
      newLeader.setTeamRole(TeamTournamentRoleEnum.LEADER);
      userTeamTournamentRepository.save(newLeader);
    }
  }

  private TeamTournamentResponse convertToResponse(TeamTournament teamTournament) {
    return modelMapper.map(teamTournament, TeamTournamentResponse.class);
  }
}
