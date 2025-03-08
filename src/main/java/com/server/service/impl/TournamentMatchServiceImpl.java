package com.server.service.impl;

import com.server.dto.request.tournamentmatch.*;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    public List<TournamentMatchResponse> submitTournamentMatches(ListSubmitTournamentMatch request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new RestApiException("Tournament not found"));

        if (!tournament.getUserCreate().equals(request.getUserId()) && !request.getUserId().equals("system")) {
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
            int matchIndex = (matchRequest.getOrder() - 1) / 2;
            TournamentMatch match = matches.get(matchIndex);

            TeamTournamentParticipation team = teamParticipations.stream()
                    .filter(t -> t.getId().equals(matchRequest.getTeamId()))
                    .findFirst()
                    .orElseThrow(() -> new RestApiException("Team not found for ID: " + matchRequest.getTeamId()));

            if ((matchRequest.getOrder() - 1) % 2 == 0) {
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

    @Override
    public List<TournamentMatchResponse> getTournamentMatches(FindTournamentMatch request) {
        List<TournamentMatch> matches = matchRepository.findAllByTournamentId(request.getTournamentId());

        return matches.stream()
                .sorted(Comparator.comparing(TournamentMatch::getMatchOrder))
                .map(match -> modelMapper.map(match, TournamentMatchResponse.class))
                .collect(Collectors.toList());
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
    @Override
    public void checkAndUpdateMatchStatus() {
        List<TournamentMatch> matches = matchRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (TournamentMatch match : matches) {
            if(match.getStartDate().isBefore(now) || match.getStartDate().equals(now)) {
                match.setStatus(TournamentMatchStatusEnum.ONGOING);
                matchRepository.save(match);
            }
            if ((match.getEndDate().isBefore(now)||match.getEndDate().equals(now)) && match.getStatus() == TournamentMatchStatusEnum.ONGOING) {
                match.setStatus(TournamentMatchStatusEnum.ENDED);
                matchRepository.save(match);

                if(match.getStage()==MatchStageEnum.THIRD_PLACE){ updateThirdPlaceMatch(match); }
                else if (match.getStage() != MatchStageEnum.FINALS) {
                    updateNextMatch(match);
                } else {
                    endTournament(match.getTournament());
                }
            }
        }
    }

    private void updateThirdPlaceMatch (TournamentMatch match) {
        if (Objects.equals(match.getTeam1Score(), match.getTeam2Score())) {
            // Nếu hòa, cộng thêm 1 điểm cho một đội ngẫu nhiên
            if (Math.random() < 0.5) {
                match.setTeam1Score(match.getTeam1Score() + 1);
            } else {
                match.setTeam2Score(match.getTeam2Score() + 1);
            }
        }

        if (match.getTeam1Score() > match.getTeam2Score()) {
            match.getTeam2().setPlace(4);
            match.getTeam1().setPlace(3);
        } else {
            match.getTeam1().setPlace(4);
            match.getTeam2().setPlace(3);
        }

        matchRepository.save(match);
        teamTournamentParticipationRepository.save(match.getTeam1());
        teamTournamentParticipationRepository.save(match.getTeam2());
    }

    private void updateNextMatch(TournamentMatch match) {
        Tournament tournament = match.getTournament();
        List<TournamentMatch> matches = matchRepository.findAllByTournamentId(tournament.getId());

        int nextMatchOrder = getNextMatchOrder(match.getMatchOrder(), matches.size(), tournament.isThirdPlaceMatch());
        TournamentMatch nextMatch = matches.stream()
                .filter(m -> m.getMatchOrder() == nextMatchOrder)
                .findFirst()
                .orElseThrow(() -> new RestApiException("Next match not found"));

        if (Objects.equals(match.getTeam1Score(), match.getTeam2Score())) {
            // Nếu hòa, cộng thêm 1 điểm cho một đội ngẫu nhiên
            if (Math.random() < 0.5) {
                match.setTeam1Score(match.getTeam1Score() + 1);
            } else {
                match.setTeam2Score(match.getTeam2Score() + 1);
            }
        }

        if (match.getStage() == MatchStageEnum.SEMI_FINALS && tournament.isThirdPlaceMatch()) {
            int thirdPlaceMatchOrder = matches.size() - 1;
            TournamentMatch thirdPlaceMatch = matches.stream()
                    .filter(m -> m.getMatchOrder() == thirdPlaceMatchOrder)
                    .findFirst()
                    .orElse(null);

            if (thirdPlaceMatch != null) {
                if (match.getTeam1Score() > match.getTeam2Score()) {
                    thirdPlaceMatch.setTeam2(match.getTeam2());
                } else {
                    thirdPlaceMatch.setTeam1(match.getTeam1());
                }
                matchRepository.save(thirdPlaceMatch);
            }
        } else {
            if (match.getStage() == MatchStageEnum.ROUND_OF_16) {
                if (match.getTeam1Score() > match.getTeam2Score()) {
                    match.getTeam2().setPlace(16);
                } else {
                    match.getTeam1().setPlace(16);
                }
            } else if (match.getStage() == MatchStageEnum.QUARTER_FINALS) {
                if (match.getTeam1Score() > match.getTeam2Score()) {
                    match.getTeam2().setPlace(8);
                } else {
                    match.getTeam1().setPlace(8);
                }
            } else if (match.getStage() == MatchStageEnum.SEMI_FINALS) {
                if (match.getTeam1Score() > match.getTeam2Score()) {
                    match.getTeam2().setPlace(4);
                } else {
                    match.getTeam1().setPlace(4);
                }
            }

            if (match.getTeam1Score() > match.getTeam2Score()) {
                nextMatch.setTeam1(match.getTeam1());
            } else {
                nextMatch.setTeam2(match.getTeam2());
            }
            matchRepository.save(nextMatch);
        }

        matchRepository.save(match);
        teamTournamentParticipationRepository.save(match.getTeam1());
        teamTournamentParticipationRepository.save(match.getTeam2());
    }



    private int getNextMatchOrder(int currentOrder, int totalMatches, boolean hasThirdPlaceMatch) {
        int finalMatchOrder = totalMatches; // Trận chung kết luôn là trận cuối cùng
        int thirdPlaceMatchOrder = hasThirdPlaceMatch ? totalMatches - 1 : -1; // Trận tranh hạng 3 nếu có

        if (hasThirdPlaceMatch) {
            totalMatches -= 1; // Loại trận tranh hạng 3 khỏi tổng số trận chính
        }

        if (totalMatches == 3) { // Giải đấu có 4 đội (bán kết -> chung kết)
            if (currentOrder < 2) {
                return 3; // Trận chung kết
            } else {
                return finalMatchOrder;
            }
        } else if (totalMatches == 7) { // Giải đấu có 8 đội
            if (currentOrder < 4) {
                return 5 + (currentOrder / 2); // Tứ kết -> Bán kết
            } else if (currentOrder < 6) {
                return finalMatchOrder; // Chung kết
            } else {
                return thirdPlaceMatchOrder; // Tranh hạng 3 nếu có
            }
        } else if (totalMatches == 15) { // Giải đấu có 16 đội
            if (currentOrder < 8) {
                return 9 + (currentOrder / 2); // Vòng 1/8 -> Tứ kết
            } else if (currentOrder < 12) {
                return 13 + (currentOrder / 2); // Tứ kết -> Bán kết
            } else if (currentOrder < 14) {
                return finalMatchOrder; // Chung kết
            } else {
                return thirdPlaceMatchOrder; // Tranh hạng 3 nếu có
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

        distributeTopPrizes(tournament, participations);
    }

    private void distributeTopPrizes(Tournament tournament, List<TeamTournamentParticipation> participations) {
        TournamentMatch finalMatch = matchRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(match -> match.getStage() == MatchStageEnum.FINALS)
                .findFirst()
                .orElseThrow(() -> new RestApiException("Final match not found"));

        TeamTournamentParticipation winningTeam;
        TeamTournamentParticipation losingTeam;

        if (Objects.equals(finalMatch.getTeam1Score(), finalMatch.getTeam2Score())) {
            // Nếu hòa, cộng thêm 1 điểm cho một đội ngẫu nhiên
            if (Math.random() < 0.5) {
                finalMatch.setTeam1Score(finalMatch.getTeam1Score() + 1);
            } else {
                finalMatch.setTeam2Score(finalMatch.getTeam2Score() + 1);
            }
            matchRepository.save(finalMatch);
        }

        if (finalMatch.getTeam1Score() > finalMatch.getTeam2Score()) {
            winningTeam = finalMatch.getTeam1();
            losingTeam = finalMatch.getTeam2();
            losingTeam.setPlace(2);
        } else {
            winningTeam = finalMatch.getTeam2();
            losingTeam = finalMatch.getTeam1();
            losingTeam.setPlace(2);
        }

        winningTeam.setPlace(1);


        teamTournamentParticipationRepository.save(winningTeam);
        teamTournamentParticipationRepository.save(losingTeam);

        if(tournament.getTop1Prize()!=null && tournament.getTop2Prize()!=null && tournament.getTop1Prize()>0 && tournament.getTop2Prize()>0){
            int top1Prize = tournament.getTop1Prize();
            int top2Prize = tournament.getTop2Prize();

            updateTeamPoints(winningTeam, top1Prize);
            updateTeamPoints(losingTeam, top2Prize);

        }

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