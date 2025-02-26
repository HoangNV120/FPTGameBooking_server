package com.server.service.impl;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.server.dto.response.userteamtournament.UserTeamTournamentResponse;
import com.server.entity.UserTeamTournament;
import com.server.exceptions.RestApiException;
import com.server.repository.UserTeamTournamentRepository;
import com.server.service.UserTeamTournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserTeamTournamentServiceImpl implements UserTeamTournamentService {
    private final UserTeamTournamentRepository userTeamTournamentRepository;
    private final ModelMapper modelMapper;

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
}
