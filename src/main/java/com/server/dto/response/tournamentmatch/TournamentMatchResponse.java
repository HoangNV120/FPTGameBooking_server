package com.server.dto.response.tournamentmatch;

import com.server.dto.response.teamtournamentparticipation.TeamTournamentParticipationResponse;
import com.server.enums.MatchStageEnum;
import com.server.enums.MatchStatusEnum;
import com.server.enums.MatchTypeEnum;
import com.server.enums.TournamentMatchStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TournamentMatchResponse {
    private String id;
    private TeamTournamentParticipationResponse team1;
    private TeamTournamentParticipationResponse team2;
    private MatchTypeEnum type;
    private Integer team1Score;
    private Integer team2Score;
    private TournamentMatchStatusEnum status;
    private String streamLink;
    private MatchStageEnum stage;
    private int matchOrder;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
