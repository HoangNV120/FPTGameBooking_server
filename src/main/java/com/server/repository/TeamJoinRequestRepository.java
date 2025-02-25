package com.server.repository;

import com.server.entity.TeamJoinRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, String> {

  List<TeamJoinRequest> findByUserIdAndTeamId(String userId, String teamId);
  List<TeamJoinRequest> findByTeamId(String teamId);
}