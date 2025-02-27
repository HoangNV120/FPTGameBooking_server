package com.server.repository;

import com.server.dto.response.match.ResultMatchResponse;
import com.server.entity.Match;
import com.server.enums.MatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, String> {

    @Query("SELECT m FROM Match m WHERE m.teamWin.id = :teamId OR m.teamFail.id = :teamId")
    List<Match> findAllByTeamWinOrTeamFaiId(@Param("teamId") String teamId);

    @Query(value = """
            SELECT
                t.id AS team_id,
                t.name AS team_name,
                SUM(CASE WHEN m.id_team_win = t.id THEN 1 ELSE 0 END) AS win_count,
                SUM(CASE WHEN m.id_team_fail = t.id THEN 1 ELSE 0 END) AS lose_count
            FROM team t
            LEFT JOIN matches m ON t.id = m.id_team_win OR t.id = m.id_team_fail
            WHERE t.id IN :teamIds 
              AND (:matchStatus IS NULL OR m.match_status = :matchStatus)
            GROUP BY t.id, t.name
            ORDER BY win_count DESC
            """, nativeQuery = true)
    List<ResultMatchResponse> countMatchByTeamIdAndMatchStatus(
            @Param("teamIds") List<String> teamIds,
            @Param("matchStatus") String matchStatus);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Match U WHERE U.id = :id ")
    void deleteById(String id);
}
