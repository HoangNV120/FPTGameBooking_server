package com.server.repository;

import com.server.dto.response.userteamtournament.UserTeamTournamentResponse;
import com.server.entity.TeamTournament;
import com.server.entity.User;
import com.server.entity.UserTeamTournament;
import com.server.enums.TeamTournamentRoleEnum;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserTeamTournamentRepository extends JpaRepository<UserTeamTournament, String> {
    int countByTeamId(String id);
    List<UserTeamTournament> findByTeamId(String id);
    Optional<UserTeamTournament> findByUserId(String userId);
    void deleteByUserAndTeam(User request, TeamTournament team);
    void deleteUserTeamTournamentByTeam(TeamTournament team);

    @Query("SELECT u.team FROM UserTeamTournament u WHERE u.user.id = :userId")
    Optional<TeamTournament> getTeamTournamentByUserId(@Param("userId") String id);
    Optional<UserTeamTournament> findByUser(User request);
    List<UserTeamTournament> findByTeamAndTeamRoleNotOrderByCreatedDateAsc(TeamTournament teamTournament,
        TeamTournamentRoleEnum roleEnum, Pageable pageable);
    Optional<UserTeamTournament> findByUserAndTeamId(User user, String teamId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u.team FROM UserTeamTournament u WHERE u.user.id = :userId")
    Optional<TeamTournament> getTeamTournamentByUserIdWithLock(@Param("userId") String userId);
}