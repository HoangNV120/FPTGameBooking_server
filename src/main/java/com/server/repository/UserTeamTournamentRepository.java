package com.server.repository;

import com.server.entity.UserTeamTournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTeamTournamentRepository extends JpaRepository<UserTeamTournament, String> {
    int countByTeamId(String id);
    List<UserTeamTournament> findByTeamId(String id);
    Optional<UserTeamTournament> findByUserId(String userId);
}