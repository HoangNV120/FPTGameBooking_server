package com.server.repository;

import com.server.entity.TeamTournamentParticipation;
import com.server.enums.ParticipationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamTournamentParticipationRepository extends JpaRepository<TeamTournamentParticipation, String> {
    List<TeamTournamentParticipation> findAllByTournamentId(String tournamentId);

    List<TeamTournamentParticipation> findAllByTeamId(String teamId);

    long countByTournamentIdAndStatus(String tournamentId, ParticipationStatusEnum status);
    Optional<TeamTournamentParticipation> findByTeamId(String teamId);
    Optional<TeamTournamentParticipation> findByTeamIdAndTournamentId(String teamId, String tournamentId);

}
