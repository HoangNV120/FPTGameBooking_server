package com.server.repository;

import com.server.entity.TournamentMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, String> {
    List<TournamentMatch> findAllByTournamentId(String tournamentId);
}
