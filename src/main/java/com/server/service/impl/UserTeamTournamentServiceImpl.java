package com.server.service.impl;

import com.server.dto.request.user.UserRequest;
import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.dto.response.user.UserMinimalResponse;
import com.server.dto.response.user.UserResponse;
import com.server.entity.TeamTournament;
import com.server.entity.User;
import com.server.entity.UserTeamTournament;
import com.server.enums.TeamTournamentRoleEnum;
import com.server.exceptions.RestApiException;
import com.server.repository.TeamTournamentRepository;
import com.server.repository.UserRepository;
import com.server.repository.UserTeamTournamentRepository;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.server.dto.response.userteamtournament.UserTeamTournamentResponse;
import com.server.entity.UserTeamTournament;
import com.server.exceptions.RestApiException;
import com.server.repository.UserTeamTournamentRepository;
import com.server.service.UserTeamTournamentService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserTeamTournamentServiceImpl implements UserTeamTournamentService {
    private final UserTeamTournamentRepository userTeamTournamentRepository;
    private final ModelMapper modelMapper;
    private final TeamTournamentRepository teamTournamentRepository;
    private final UserRepository UserRepository;

    @Override
    public List<UserTeamTournamentResponse> findByTeamId(String id) {
        List<UserTeamTournament> userTeamTournaments = userTeamTournamentRepository.findByTeamId(id);
        return userTeamTournaments.stream().map(this::ToResponse).toList();
    }

    @Override
    public UserTeamTournamentResponse findByUserId(String userId) {
        Optional<UserTeamTournament> userTeamTournament = userTeamTournamentRepository.findByUserId(userId);
        if (userTeamTournament.isEmpty()) {
            throw new RestApiException("UserTeamTournament not found");
        }
        return ToResponse(userTeamTournament.get());
    }


    private UserTeamTournamentResponse ToResponse(UserTeamTournament userTeamTournament) {
        return modelMapper.map(userTeamTournament, UserTeamTournamentResponse.class);
    }

    private UserTeamTournament ToEntity(UserTeamTournamentResponse userTeamTournamentResponse) {
        return modelMapper.map(userTeamTournamentResponse, UserTeamTournament.class);
    }


  @Override
  @Transactional
  public UserMinimalResponse leaveTeam(UserRequest request) {
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
      return null;
    }

    UserTeamTournament newLeader = null;
    if(TeamTournamentRoleEnum.LEADER.equals((roleUser))){
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
    return convertUser(request);
  }

  private TeamTournamentResponse convertToResponse(TeamTournament teamTournament) {
    return modelMapper.map(teamTournament, TeamTournamentResponse.class);
  }

  private UserMinimalResponse convertUser(UserRequest userRequest) {
    return modelMapper.map(userRequest, UserMinimalResponse.class);
  }
}
