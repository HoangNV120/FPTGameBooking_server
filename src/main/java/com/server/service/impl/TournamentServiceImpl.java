package com.server.service.impl;

import com.server.constants.Constants;
import com.server.dto.request.tournament.TournamentRequest;
import com.server.dto.response.tournament.TournamentResponse;
import com.server.entity.Tournament;
import com.server.entity.User;
import com.server.exceptions.RestApiException;
import com.server.repository.TournamentRepository;
import com.server.repository.UserRepository;
import com.server.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TournamentResponse> getAllTournaments() {
        LocalDateTime now = LocalDateTime.now();
        return tournamentRepository.findAll().stream()
                .filter(tournament -> now.isBefore(tournament.getStartDate()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TournamentResponse getTournamentById(String id) {
        return tournamentRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RestApiException("Tournament not found"));
    }

    @Override
    @Transactional
    public TournamentResponse createTournament(TournamentRequest request) {
        validateTournamentRequest(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RestApiException("User not found"));

        int totalCost = request.getTotalPrize() + Constants.DEFAULT_TOURNAMENT_COST;
        if (user.getPoint() < totalCost) {
            throw new RestApiException("Insufficient points");
        }

        user.setPoint(user.getPoint() - totalCost);
        userRepository.save(user);

        Tournament tournament = convertToEntity(request);
        tournament.setFlagDisplay(true);
        tournament.setTop4Prize(0);
        tournament.setStatus("PENDING");


        tournament = tournamentRepository.save(tournament);
        return convertToResponse(tournament);
    }

    private void validateTournamentRequest(TournamentRequest request) {
        if (request.getTop1Prize() + request.getTop2Prize() != request.getTotalPrize()) {
            throw new RestApiException("Top 1 and Top 2 prizes must equal total prize");
        }
    }

    private Tournament convertToEntity(TournamentRequest request) {
        return modelMapper.map(request, Tournament.class);
    }

    private TournamentResponse convertToResponse(Tournament tournament) {
        return modelMapper.map(tournament, TournamentResponse.class);
    }
}