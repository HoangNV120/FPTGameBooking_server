package com.server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.server.constants.Constants;
import com.server.dto.request.tournament.TournamentRequest;
import com.server.dto.request.tournament.UpdateStreamLinkTournamentRequest;
import com.server.dto.request.tournamentmatch.ListSubmitTournamentMatch;
import com.server.dto.request.tournamentmatch.SubmitTournamentMatchRequest;
import com.server.dto.request.tournamentmatch.TournamentMatchRequest;
import com.server.dto.response.teamtournament.TeamTournamentImageResponse;
import com.server.dto.response.tournament.TournamentImageResponse;
import com.server.dto.response.tournament.TournamentResponse;
import com.server.dto.response.tournamentmatch.TournamentMatchResponse;
import com.server.entity.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private final Cloudinary cloudinary;


    @Override
    public List<TournamentResponse> getAllTournaments() {
        LocalDateTime now = LocalDateTime.now();
        return tournamentRepository.findAll().stream()
                .sorted(Comparator.comparing(Tournament::getStartDate))
                .map(this::convertToResponse)
                .toList();
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

        if (request.getNumberOfTeam() != 4 && request.getNumberOfTeam() != 8 && request.getNumberOfTeam() != 16) {
            throw new RestApiException("Số lượng đội không hợp lệ");
        }

        if(request.getTeamMemberCount()!=5 && request.getTeamMemberCount()!=6){
            throw new RestApiException("Số lượng thành viên không hợp lệ");
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
        if (tournament.isThirdPlaceMatch()) {
            expectedMatches += 1;
        }

        if (matchRequests.size() != expectedMatches) {
            throw new RestApiException("Invalid number of matches for the given number of teams");
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

            setMatchStage(match, i, numberOfTeams, tournament.isThirdPlaceMatch(),matches);
        }

        validateMatchTiming(matches);
        tournamentMatchRepository.saveAll(matches);
    }

    private void setMatchStage(TournamentMatch match, int index, int numberOfTeams, boolean hasThirdPlace, List<TournamentMatch> matches) {
        if (numberOfTeams == 4) {
            if (index < 2) {
                match.setStage(MatchStageEnum.SEMI_FINALS);
            } else {
                match.setStage(MatchStageEnum.FINALS);
            }
        } else if (numberOfTeams == 8) {
            if (index < 4) {
                match.setStage(MatchStageEnum.QUARTER_FINALS);
            } else if (index < 6) {
                match.setStage(MatchStageEnum.SEMI_FINALS);
            } else {
                match.setStage(MatchStageEnum.FINALS);
            }
        } else if (numberOfTeams == 16) {
            if (index < 8) {
                match.setStage(MatchStageEnum.ROUND_OF_16);
            } else if (index < 12) {
                match.setStage(MatchStageEnum.QUARTER_FINALS);
            } else if (index < 14) {
                match.setStage(MatchStageEnum.SEMI_FINALS);
            } else {
                match.setStage(MatchStageEnum.FINALS);
            }
        }

        if (hasThirdPlace && index == matches.size() - 2) {
            match.setStage(MatchStageEnum.THIRD_PLACE);
        }
    }

    private void validateMatchTiming(List<TournamentMatch> matches) {
        for (TournamentMatch match : matches) {
            if (match.getStartDate() == null || match.getEndDate() == null) {
                throw new RestApiException("Match start and end dates are required");
            }

            // Check for conflicts with matches of higher stages
            for (TournamentMatch otherMatch : matches) {
                if (match == otherMatch) continue;

                boolean timeOverlap = isTimeOverlap(match, otherMatch);
                boolean sameStage = match.getStage() == otherMatch.getStage();
                boolean higherStage = isHigherStage(otherMatch.getStage(), match.getStage());

                if (timeOverlap && higherStage) {
                    throw new RestApiException("Match timing conflicts with a higher stage match: " +
                            match.getStage() + " cannot overlap with " + otherMatch.getStage());
                }
            }
        }
    }

    private boolean isTimeOverlap(TournamentMatch match1, TournamentMatch match2) {
        return !match1.getEndDate().isBefore(match2.getStartDate()) &&
                !match2.getEndDate().isBefore(match1.getStartDate());
    }

    private boolean isHigherStage(MatchStageEnum stage1, MatchStageEnum stage2) {
        int stage1Order = getStageOrder(stage1);
        int stage2Order = getStageOrder(stage2);
        return stage1Order > stage2Order;
    }

    private int getStageOrder(MatchStageEnum stage) {
        return switch (stage) {
            case ROUND_OF_16 -> 1;
            case QUARTER_FINALS -> 2;
            case SEMI_FINALS -> 3;
            case THIRD_PLACE -> 4;
            case FINALS -> 5;
        };
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    @Override
    public void checkAndUpdateTournamentStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Tournament> pendingTournaments = tournamentRepository.findAll().stream()
                .filter(tournament -> tournament.getStartDate().isBefore(now) || tournament.getStartDate().isEqual(now))
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

    @Override
    public TournamentResponse updateStreamLink(UpdateStreamLinkTournamentRequest request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new RestApiException("Tournament not found"));

        if (!tournament.getUserCreate().equals(request.getUserId())) {
            throw new RestApiException("Unauthorized to update stream link");
        }

        tournament.setStreamLink(request.getStreamLink());
        tournament = tournamentRepository.save(tournament);
        return modelMapper.map(tournament, TournamentResponse.class);
    }

    @Override
    public TournamentImageResponse uploadImage(MultipartFile file, String tournamentId) throws IOException {
        deleteImage("tournament" + tournamentId);

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("public_id", "tournament" + tournamentId, "resource_type", "auto"));

        // Get tournament by ID
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RestApiException("Tournament not found"));

        // Update team tournament image link
        tournament.setLink(uploadResult.get("secure_url").toString());
        tournamentRepository.save(tournament);

        return new TournamentImageResponse(uploadResult.get("secure_url").toString());
    }

    private void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.out.println("Cannot delete old image: " + e.getMessage());
        }
    }
}