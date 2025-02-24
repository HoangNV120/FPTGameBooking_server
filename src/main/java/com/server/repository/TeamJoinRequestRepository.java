package com.server.repository;

import com.server.entity.TeamJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, String> {
}