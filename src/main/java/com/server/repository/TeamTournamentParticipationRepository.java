package com.server.repository;

import com.server.entity.TeamTournamentParticipation;
import com.server.enums.ParticipationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamTournamentParticipationRepository extends JpaRepository<TeamTournamentParticipation, String> {
    List<TeamTournamentParticipation> findAllByTournamentId(String tournamentId);

    List<TeamTournamentParticipation> findAllByTeamId(String teamId);

    long countByTournamentIdAndStatus(String tournamentId, ParticipationStatusEnum status);
}
