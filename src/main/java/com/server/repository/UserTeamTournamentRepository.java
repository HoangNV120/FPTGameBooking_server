package com.server.repository;

import com.server.entity.UserTeamTournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTeamTournamentRepository extends JpaRepository<UserTeamTournament, String> {
}