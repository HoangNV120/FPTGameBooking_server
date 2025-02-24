package com.server.repository;

import com.server.entity.TeamTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TeamTournamentRepository extends JpaRepository<TeamTournament, String>, JpaSpecificationExecutor<TeamTournament> {
}