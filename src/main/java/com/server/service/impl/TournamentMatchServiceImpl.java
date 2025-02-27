package com.server.service.impl;

import com.server.dto.request.tournamentmatch.ListSubmitTournamentMatch;
import com.server.dto.request.tournamentmatch.SubmitTournamentMatchRequest;
import com.server.dto.request.tournamentmatch.TournamentMatchRequest;
import com.server.dto.request.tournamentmatch.UpdateScoreTournamentMatch;
import com.server.dto.response.tournamentmatch.TournamentMatchResponse;
import com.server.entity.*;
import com.server.enums.MatchStageEnum;
import com.server.enums.MatchTypeEnum;
import com.server.enums.ParticipationStatusEnum;
import com.server.enums.TournamentMatchStatusEnum;
import com.server.exceptions.RestApiException;
import com.server.repository.TeamTournamentParticipationRepository;
import com.server.repository.TournamentMatchRepository;
import com.server.repository.TournamentRepository;
import com.server.repository.UserRepository;
import com.server.service.TournamentMatchService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentMatchServiceImpl implements TournamentMatchService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMatchRepository matchRepository;
    private final TeamTournamentParticipationRepository teamTournamentParticipationRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<TournamentMatchResponse> createTournamentMatches(List<TournamentMatchRequest> matchRequests) {
        if (matchRequests.isEmpty()) {
            throw new RestApiException("Match request list cannot be empty");
        }

        String tournamentId = matchRequests.get(0).getTournamentId();

        for (TournamentMatchRequest request : matchRequests) {
            if (!request.getTournamentId().equals(tournamentId)) {
                throw new RestApiException("All tournament IDs in the request list must be the same");
            }
        }

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RestApiException("Tournament not found"));

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

        try {
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

            matchRepository.saveAll(matches);

            return matches.stream()
                    .map(match -> modelMapper.map(match, TournamentMatchResponse.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            tournamentRepository.delete(tournament);
            throw new RestApiException("Failed to create matches, tournament has been deleted");
        }
    }

    @Override
    @Transactional
    public List<TournamentMatchResponse> submitTournamentMatches(ListSubmitTournamentMatch request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new RestApiException("Tournament not found"));

        if (!tournament.getUserCreate().equals(request.getUserId())) {
            throw new RestApiException("Unauthorized");
        }

        int numberOfTeams = tournament.getNumberOfTeam();
        if (request.getMatchRequests().size() != numberOfTeams) {
            throw new RestApiException("Invalid number of matches for the given number of teams");
        }

        List<TeamTournamentParticipation> teamParticipations = teamTournamentParticipationRepository.findAllByTournamentId(tournament.getId());
        if (teamParticipations.size() != numberOfTeams) {
            throw new RestApiException("Number of teams in the tournament does not match the expected number");
        }

        List<TournamentMatch> matches = matchRepository.findAllByTournamentId(tournament.getId());
        for (SubmitTournamentMatchRequest matchRequest : request.getMatchRequests()) {
            TournamentMatch match = matches.stream()
                    .filter(m -> m.getMatchOrder() == matchRequest.getOrder())
                    .findFirst()
                    .orElseThrow(() -> new RestApiException("Match not found for order: " + matchRequest.getOrder()));

            TeamTournamentParticipation team = teamParticipations.stream()
                    .filter(t -> t.getId().equals(matchRequest.getTeamId()))
                    .findFirst()
                    .orElseThrow(() -> new RestApiException("Team not found for ID: " + matchRequest.getTeamId()));

            if (matchRequest.getOrder() % 2 == 1) {
                match.setTeam1(team);
            } else {
                match.setTeam2(team);
            }
        }

        tournament.setStatus("ONGOING");
        tournamentRepository.save(tournament);
        matchRepository.saveAll(matches);

        return matches.stream()
                .map(match -> modelMapper.map(match, TournamentMatchResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TournamentMatchResponse updateMatchScore(UpdateScoreTournamentMatch request) {
        TournamentMatch match = matchRepository.findById(request.getTournamentMatchId())
                .orElseThrow(() -> new RestApiException("Match not found"));

        if (request.getTeam1Score() < 0 || request.getTeam2Score() < 0) {
            throw new RestApiException("Scores cannot be negative");
        }

        int maxScore = getMaxScore(match.getType());
        if (request.getTeam1Score() > maxScore || request.getTeam2Score() > maxScore) {
            throw new RestApiException("Scores cannot exceed the maximum allowed for the match type");
        }

        if (request.getTeam1Score() + request.getTeam2Score() > maxScore) {
            throw new RestApiException("Total score cannot exceed the maximum allowed for the match type");
        }

        match.setTeam1Score(request.getTeam1Score());
        match.setTeam2Score(request.getTeam2Score());
        matchRepository.save(match);

        return modelMapper.map(match, TournamentMatchResponse.class);
    }

    private int getMaxScore(MatchTypeEnum type) {
        return switch (type) {
            case BO1 -> 1;
            case BO3 -> 2;
            case BO5 -> 3;
            default -> throw new IllegalArgumentException("Unknown match type: " + type);
        };
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void checkAndUpdateMatchStatus() {
        List<TournamentMatch> matches = matchRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (TournamentMatch match : matches) {
            if (match.getEndDate().isBefore(now) && match.getStatus() == TournamentMatchStatusEnum.ONGOING) {
                match.setStatus(TournamentMatchStatusEnum.ENDED);
                matchRepository.save(match);

                if (match.getStage() != MatchStageEnum.FINALS) {
                    updateNextMatch(match);
                } else {
                    endTournament(match.getTournament());
                }
            }
        }
    }

    private void updateNextMatch(TournamentMatch match) {
        Tournament tournament = match.getTournament();
        List<TournamentMatch> matches = matchRepository.findAllByTournamentId(tournament.getId());

        int nextMatchOrder = getNextMatchOrder(match.getMatchOrder(), matches.size());
        TournamentMatch nextMatch = matches.stream()
                .filter(m -> m.getMatchOrder() == nextMatchOrder)
                .findFirst()
                .orElseThrow(() -> new RestApiException("Next match not found"));

        if (match.getTeam1Score() > match.getTeam2Score()) {
            nextMatch.setTeam1(match.getTeam1());
        } else if (match.getTeam2Score() > match.getTeam1Score()) {
            nextMatch.setTeam2(match.getTeam2());
        } else {
            Random random = new Random();
            if (random.nextBoolean()) {
                nextMatch.setTeam1(match.getTeam1());
            } else {
                nextMatch.setTeam2(match.getTeam2());
            }
        }

        matchRepository.save(nextMatch);
    }

    private int getNextMatchOrder(int currentOrder, int totalMatches) {
        if (totalMatches == 3) {
            return 3;
        } else if (totalMatches == 7) {
            if (currentOrder < 4) {
                return 5 + (currentOrder / 2);
            } else {
                return 7;
            }
        } else {
            throw new IllegalArgumentException("Unsupported number of matches: " + totalMatches);
        }
    }

    private void endTournament(Tournament tournament) {
        tournament.setStatus("ENDED");
        tournamentRepository.save(tournament);

        List<TeamTournamentParticipation> participations = teamTournamentParticipationRepository.findAllByTournamentId(tournament.getId());
        for (TeamTournamentParticipation participation : participations) {
            participation.setStatus(ParticipationStatusEnum.ENDED);
            teamTournamentParticipationRepository.save(participation);
        }

        if (tournament.getTop1Prize() != null && tournament.getTop1Prize() > 0 && tournament.getTop2Prize() != null && tournament.getTop2Prize() > 0) {
            distributeTopPrizes(tournament, participations);
        }
    }

    private void distributeTopPrizes(Tournament tournament, List<TeamTournamentParticipation> participations) {
        TournamentMatch finalMatch = matchRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(match -> match.getStage() == MatchStageEnum.FINALS)
                .findFirst()
                .orElseThrow(() -> new RestApiException("Final match not found"));

        TeamTournamentParticipation winningTeam;
        TeamTournamentParticipation losingTeam;

        if (finalMatch.getTeam1Score() > finalMatch.getTeam2Score()) {
            winningTeam = finalMatch.getTeam1();
            losingTeam = finalMatch.getTeam2();
        } else {
            winningTeam = finalMatch.getTeam2();
            losingTeam = finalMatch.getTeam1();
        }

        int top1Prize = tournament.getTop1Prize();
        int top2Prize = tournament.getTop2Prize();

        updateTeamPoints(winningTeam, top1Prize);
        updateTeamPoints(losingTeam, top2Prize);
    }

    private void updateTeamPoints(TeamTournamentParticipation team, int prize) {
        List<UserTeamTournament> userTeamTournaments = team.getTeam().getUserTeamTournaments();
        int prizePerUser = (int) Math.ceil((double) prize / userTeamTournaments.size());

        for (UserTeamTournament userTeamTournament : userTeamTournaments) {
            User user = userTeamTournament.getUser();
            user.setPoint(user.getPoint() + prizePerUser);
            userRepository.save(user);
        }
    }
}