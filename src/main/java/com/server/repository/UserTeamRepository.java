package com.server.repository;

import com.server.entity.User;
import com.server.entity.UserTeam;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, String> {

    @Query("SELECT ut FROM UserTeam ut JOIN ut.team t " +
            "WHERE t.room.code = :codeRoom AND ut.status = :status " +
            "ORDER BY ut.createdDate")
    List<UserTeam> findUserTeamByRoom(@Param("codeRoom") String codeRoom,
                                      @Param("status") StatusEnum status);


    UserTeam findUserTeamByTeam_idAndUser_id(String teamId, String userId);

    int countUserTeamByTeam_id(String teamId);

    Optional<UserTeam> findUserTeamByTeam_Room_CodeAndUser_Id(String codeRoom, String userId);

    List<UserTeam> findUserTeamByTeam_Room_Code(String codeRoom);

    List<UserTeam> findUserTeamByUser_Id(String userId);

    List<UserTeam> findUserTeamByUser_IdAndStatus(String userId, StatusEnum status);

    boolean existsUserTeamByTeam_IdAndRole(String teamId, RoleEnum role);

    List<UserTeam> findByTeam_IdOrderByCreatedDateAsc(String teamId);

    Optional<UserTeam> findUserTeamByTeam_Room_IdAndUser_IdAndRole(String teamId, String userId, RoleEnum role);

    Optional<UserTeam> findUserTeamByTeam_IdAndRole(String teamId, RoleEnum role);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM UserTeam U WHERE U.id = :id ")
    void deleteById(String id);

    List<UserTeam> findByTeamIdInAndRole(List<String> teamIds, RoleEnum role);

    @Modifying
    @Transactional
    void deleteAllByTeam_Room_Code(String codeRoom);

    Optional<UserTeam> findByUser_Id(String userId);
}
