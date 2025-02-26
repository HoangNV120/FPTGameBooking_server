package com.server.repository;

import com.server.dto.request.teamjoinrequest.UpdateStatusTeamJoinRequest;
import com.server.entity.TeamJoinRequest;
import com.server.enums.RequestStatusEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, String> {

  Optional<TeamJoinRequest> findTeamJoinRequestByUserIdAndTeamId(String userId, String teamId);
  List<TeamJoinRequest> findByUserIdAndTeamId(String userId, String teamId);
  List<TeamJoinRequest> findByTeamId(String teamId);

  void deleteTeamJoinRequestByUserIdAndTeamId(String userId, String teamId);
  void deleteTeamJoinRequestsByUserId(String userId);

  @Query("SELECT t from TeamJoinRequest t WHERE t.user.id = :userId AND t.status = :status")
  List<TeamJoinRequest> findTeamJoinRequestsByUserIdAndStatus(@Param("userId")String userId,@Param("status") RequestStatusEnum statusEnum);

}