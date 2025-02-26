package com.server.repository;

import com.server.dto.request.teamjoinrequest.UpdateStatusTeamJoinRequest;
import com.server.entity.TeamJoinRequest;
import com.server.enums.RequestStatusEnum;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, String> {

  TeamJoinRequest findTeamJoinRequestByUserIdAndTeamId(String userId, String teamId);
  List<TeamJoinRequest> findByUserIdAndTeamId(String userId, String teamId);
  List<TeamJoinRequest> findByTeamIdAndStatus(String teamId, RequestStatusEnum status);

  @Modifying
  @Query("UPDATE TeamJoinRequest t SET t.status = :status WHERE t.user.id = :userId AND t.team.id = :teamId")
  void approveTeamJoinRequest(@Param("status") RequestStatusEnum status,@Param("userId") String userId, @Param("teamId") String teamId);

  @Modifying
  @Query("UPDATE TeamJoinRequest t SET t.status = :status WHERE t.user.id = :userId AND t.team.id <> :teamId")
  void rejectOtherTeamJoinRequests(@Param("status") RequestStatusEnum status, @Param("userId") String userId, @Param("teamId") String teamId);

  @Modifying
  @Query("UPDATE TeamJoinRequest t SET t.status = :status WHERE t.user.id = :userId AND t.team.id = :teamId")
  void rejectTeamJoinRequests(@Param("status") RequestStatusEnum status,@Param("userId") String userId, @Param("teamId") String teamId);

  @Query("SELECT t from TeamJoinRequest t WHERE t.user.id = :userId AND t.status = :status")
  List<TeamJoinRequest> findTeamJoinRequestsByUserIdAndStatus(@Param("userId")String userId,@Param("status") RequestStatusEnum statusEnum);

}