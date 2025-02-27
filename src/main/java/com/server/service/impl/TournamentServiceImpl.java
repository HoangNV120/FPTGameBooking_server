package com.server.service.impl;

import com.server.constants.Constants;
import com.server.dto.request.tournament.TournamentRequest;
import com.server.dto.request.tournamentmatch.ListSubmitTournamentMatch;
import com.server.dto.request.tournamentmatch.SubmitTournamentMatchRequest;
import com.server.dto.request.tournamentmatch.TournamentMatchRequest;
import com.server.dto.response.tournament.TournamentResponse;
import com.server.dto.response.tournamentmatch.TournamentMatchResponse;
import com.server.entity.TeamTournamentParticipation;
import com.server.entity.Tournament;
import com.server.entity.TournamentMatch;
import com.server.entity.User;
import com.server.entity.common.AuditTable;
import com.server.enums.MatchStageEnum;
import com.server.enums.ParticipationStatusEnum;
import com.server.enums.TournamentMatchStatusEnum;
import com.server.exceptions.RestApiException;
import com.server.repository.TeamTournamentParticipationRepository;
import com.server.repository.TournamentMatchRepository;
import com.server.repository.TournamentRepository;
import com.server.repository.UserRepository;
import com.server.service.TournamentMatchService;
import com.server.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMatchRepository tournamentMatchRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final TournamentMatchService tournamentMatchService;
    private final TeamTournamentParticipationRepository teamTournamentParticipationRepository;


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

        Tournament tournament = convertToEntity(request);
        tournament.setFlagDisplay(true);
        tournament.setTop4Prize(0);
        tournament.setStatus("PENDING");

        tournament = tournamentRepository.save(tournament);

        createTournamentMatches(request.getTournamentMatchRequests(), tournament);

        user.setPoint(user.getPoint() - totalCost);
        userRepository.save(user);

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

    private void createTournamentMatches(List<TournamentMatchRequest> matchRequests, Tournament tournament) {
        if (matchRequests.isEmpty()) {
            throw new RestApiException("Match request list cannot be empty");
        }

        int numberOfTeams = tournament.getNumberOfTeam();
        int expectedMatches = numberOfTeams - 1;

        if (matchRequests.size() != expectedMatches) {
            throw new RestApiException("Invalid number of matches for the given number of teams");
        }

        LocalDateTime latestEndDate = null;

        for (TournamentMatchRequest request : matchRequests) {
            if (Duration.between(request.getStartDate(), request.getEndDate()).toMinutes() < 30) {
                throw new RestApiException("Match duration must be at least 30 minutes");
            }

            if (latestEndDate != null && request.getStartDate().isBefore(latestEndDate)) {
                throw new RestApiException("Match start date must be after the previous match end date");
            }

            if (request.getStartDate().isBefore(tournament.getStartDate()) || request.getEndDate().isAfter(tournament.getEndDate())) {
                throw new RestApiException("Match dates must be within the tournament dates");
            }

            latestEndDate = request.getEndDate();
        }

        List<TournamentMatch> matches = matchRequests.stream()
                .map(request -> {
                    TournamentMatch match = modelMapper.map(request, TournamentMatch.class);
                    match.setTournament(tournament);
                    match.setStatus(TournamentMatchStatusEnum.WAITING);
                    match.setTeam1Score(0);
                    match.setTeam2Score(0);
                    return match;
                })
                .collect(Collectors.toList());

        // Assign match order and stage
        for (int i = 0; i < matches.size(); i++) {
            TournamentMatch match = matches.get(i);
            match.setMatchOrder(i + 1);

            if (matches.size() == 3) {
                if (i < 2) {
                    match.setStage(MatchStageEnum.SEMI_FINALS);
                } else {
                    match.setStage(MatchStageEnum.FINALS);
                }
            } else if (matches.size() == 7) {
                if (i < 4) {
                    match.setStage(MatchStageEnum.QUARTER_FINALS);
                } else if (i < 6) {
                    match.setStage(MatchStageEnum.SEMI_FINALS);
                } else {
                    match.setStage(MatchStageEnum.FINALS);
                }
            }
        }

        tournamentMatchRepository.saveAll(matches);
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    @Override
    public void checkAndUpdateTournamentStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Tournament> pendingTournaments = tournamentRepository.findAll().stream()
                .filter(tournament -> tournament.getStartDate().isAfter(now) || tournament.getStartDate().isEqual(now))
                .filter(tournament -> "PENDING".equals(tournament.getStatus()))
                .toList();

        for (Tournament tournament : pendingTournaments) {
            List<TeamTournamentParticipation> participations = teamTournamentParticipationRepository.findAllByTournamentId(tournament.getId());
            if (participations.size() >= tournament.getTeamMemberCount()) {
                tournament.setStatus("ONGOING");
                participations.forEach(participation -> participation.setStatus(ParticipationStatusEnum.STARTED));
                teamTournamentParticipationRepository.saveAll(participations);
                fillTeamsIntoMatches(tournament, participations);
            } else {
                tournament.setStatus("CLOSED");
                participations.forEach(participation -> participation.setStatus(ParticipationStatusEnum.ENDED));
                teamTournamentParticipationRepository.saveAll(participations);
                refundTotalPrizeToUser(tournament);
            }
            tournamentRepository.save(tournament);
        }
    }

    private void fillTeamsIntoMatches(Tournament tournament, List<TeamTournamentParticipation> participations) {
        List<SubmitTournamentMatchRequest> matchRequests = participations.stream()
                .sorted(Comparator.comparing(AuditTable::getCreatedDate))
                .map(participation -> new SubmitTournamentMatchRequest(participation.getTeam().getId(), participations.indexOf(participation) + 1))
                .collect(Collectors.toList());

        tournamentMatchService.submitTournamentMatches(new ListSubmitTournamentMatch(tournament.getId(),"system", matchRequests));
    }

    private void refundTotalPrizeToUser(Tournament tournament) {
        User user = userRepository.findById(tournament.getUserCreate())
                .orElseThrow(() -> new RestApiException("User not found"));
        user.setPoint(user.getPoint() + tournament.getTotalPrize());
        userRepository.save(user);
    }
}