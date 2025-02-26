package com.server.repository;

import com.server.entity.TournamentJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentJoinRequestRepository extends JpaRepository<TournamentJoinRequest, String> {
}
