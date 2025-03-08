package com.server.service.impl;

import com.server.dto.request.teamtournamentparticipation.CreateTeamTournamentParticipation;
import com.server.dto.request.teamtournamentparticipation.LeaveTeamTournamentParticipation;
import com.server.dto.request.teamtournamentparticipation.UpdateTeamTournamentParticipation;
import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.dto.response.teamtournamentparticipation.TeamTournamentParticipationResponse;
import com.server.entity.TeamTournament;
import com.server.entity.TeamTournamentParticipation;
import com.server.entity.Tournament;
import com.server.entity.UserTeamTournament;
import com.server.enums.ParticipationStatusEnum;
import com.server.enums.TeamTournamentRoleEnum;
import com.server.exceptions.RestApiException;
import com.server.repository.TeamTournamentParticipationRepository;
import com.server.repository.TeamTournamentRepository;
import com.server.repository.TournamentRepository;
import com.server.repository.UserTeamTournamentRepository;
import com.server.service.TeamTournamentParticipationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamTournamentParticipationServiceImpl implements TeamTournamentParticipationService {

    private final TeamTournamentParticipationRepository participationRepository;
    private final UserTeamTournamentRepository userTeamTournamentRepository;
    private final TeamTournamentRepository teamTournamentRepository;
    private final TournamentRepository tournamentRepository;
    private final ModelMapper modelMapper;

    @Override
    public TeamTournamentParticipationResponse createParticipation(CreateTeamTournamentParticipation request) {
        // Check if there is already a participation with the same teamId and tournamentId
        List<TeamTournamentParticipation> existingParticipations = participationRepository.findAllByTeamId(request.getTeamId());
        boolean isDuplicate = existingParticipations.stream()
                .anyMatch(participation -> participation.getTournament().getId().equals(request.getTournamentId()));
        if (isDuplicate) {
            throw new RestApiException("Participation already exists for this team and tournament");
        }

        // Retrieve the team and tournament entities
        TeamTournament teamTournament = teamTournamentRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RestApiException("Team not found"));
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new RestApiException("Tournament not found"));

        // Check if the member count of the team matches the number of teams in the tournament
        if (teamTournament.getMemberCount() != tournament.getTeamMemberCount()) {
            throw new RestApiException("Team member count does not match the tournament requirement");
        }

        // Check if the count of UserTeamTournament matches the member count of the team
        int userTeamCount = userTeamTournamentRepository.countByTeamId(request.getTeamId());
        if (userTeamCount != teamTournament.getMemberCount()) {
            throw new RestApiException("User team count does not match the team member count");
        }

        // Create and save the participation
        TeamTournamentParticipation participation = modelMapper.map(request, TeamTournamentParticipation.class);
        participation.setStatus(ParticipationStatusEnum.PENDING);
        participation.setTournament(tournament);
        participation.setTeam(teamTournament);
        participation = participationRepository.save(participation);
        return convertToResponse(participation);
    }

    @Override
    public TeamTournamentParticipationResponse updateParticipation(UpdateTeamTournamentParticipation request) {
        TeamTournamentParticipation participation = participationRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RestApiException("Participation not found"));

        if (!participation.getTournament().getUserCreate().equals(request.getUserId())) {
            throw new RestApiException("Unauthorized");
        }

        Tournament tournament = participation.getTournament();
        long participatingTeamsCount = participationRepository.countByTournamentIdAndStatus(tournament.getId(), ParticipationStatusEnum.PARTICIPATING);
        if (participatingTeamsCount >= tournament.getNumberOfTeam()) {
            throw new RestApiException("Tournament is already full");
        }

        if(tournament.getStatus()!=null && !tournament.getStatus().equals("PENDING")){
            throw new RestApiException("Tournament is already started");
        }

        if ("ACCEPT".equalsIgnoreCase(request.getStatus())) {
            participation.setStatus(ParticipationStatusEnum.PARTICIPATING);
        } else if ("REJECT".equalsIgnoreCase(request.getStatus())) {
            participationRepository.delete(participation);
            return null;
        }

        participation = participationRepository.save(participation);
        return convertToResponse(participation);
    }

    @Override
    public TeamTournamentParticipationResponse leaveTournament(LeaveTeamTournamentParticipation request) {
        TeamTournamentParticipation participation = participationRepository.findByTeamIdAndTournamentId(request.getTeamId(),
                        request.getTournamentId())
                .orElseThrow(() -> new RestApiException("Participation not found"));

        UserTeamTournament userTeamTournament = userTeamTournamentRepository.findByUserIdAndTeamId(request.getUserId(), request.getTeamId())
                .orElseThrow(() -> new RestApiException("User not part of the team"));

        if (userTeamTournament.getTeamRole().equals(TeamTournamentRoleEnum.LEADER) &&
                !participation.getStatus().equals(ParticipationStatusEnum.STARTED)) {
            participationRepository.delete(participation);
        } else {
            throw new RestApiException("Cannot leave tournament");
        }
        return convertToResponse(participation);
    }

    @Override
    public List<TeamTournamentParticipationResponse> getAllParticipationsByTournamentId(String id) {
        return participationRepository.findAllByTournamentId(id).stream()
                .map(this::convertToResponse)
                .toList();
    }

    private TeamTournamentParticipationResponse convertToResponse(TeamTournamentParticipation participation) {
        return modelMapper.map(participation, TeamTournamentParticipationResponse.class);
    }

    private TeamTournamentParticipation convertToEntity(CreateTeamTournamentParticipation request) {
        return modelMapper.map(request, TeamTournamentParticipation.class);
    }

    @Override
    public TeamTournamentParticipationResponse getLeaderAndTeamByUserId(String userId) {
        UserTeamTournament userTeamTournament = userTeamTournamentRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RestApiException("User is not part of any team"));

        // Get leader info
        UserTeamTournament leader = userTeamTournamentRepository
                .findByTeamIdAndTeamRole(userTeamTournament.getTeam().getId(), TeamTournamentRoleEnum.LEADER)
                .orElseThrow(() -> new RestApiException("Team leader not found"));

        TeamTournamentParticipation participation = new TeamTournamentParticipation();
        participation.setTeam(userTeamTournament.getTeam());

        TeamTournamentParticipationResponse participationResponse = convertToResponse(participation);
        participationResponse.setLeaderId(leader.getUser().getId());
        return participationResponse;
    }

    @Override
    public TeamTournamentParticipationResponse getParticipationByTeamId(String teamId) {
        TeamTournamentParticipation participation = participationRepository.findByTeamId(teamId)
                .orElseThrow(() -> new RestApiException("Participation not found for team"));
        return convertToResponse(participation);
    }

    @Override
    public TeamTournamentParticipationResponse getParticipationByTeamIdAndTournamentId(String teamId, String tournamentId) {
        TeamTournamentParticipation participation = participationRepository
                .findByTeamIdAndTournamentId(teamId, tournamentId)
                .orElseThrow(() -> new RestApiException("Participation not found"));
        return convertToResponse(participation);
    }
}