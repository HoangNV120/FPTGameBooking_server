package com.server.repository;

import com.server.entity.TournamentMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, String> {
}
