package com.server.repository;

import com.server.entity.TeamTournamentParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamTournamentParticipationRepository extends JpaRepository<TeamTournamentParticipation, String> {
}
