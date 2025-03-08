package com.server.repository;

import com.server.entity.TournamentMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, String> {
    @Query("SELECT m FROM TournamentMatch m WHERE m.tournament.id = :tournamentId ORDER BY m.startDate ASC")
    List<TournamentMatch> findAllByTournamentId(@Param("tournamentId") String tournamentId);

}
