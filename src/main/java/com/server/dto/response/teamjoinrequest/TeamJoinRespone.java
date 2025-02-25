package com.server.dto.response.teamjoinrequest;

import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.dto.response.teamtournament.TeamTournamentResponseClan;
import com.server.dto.response.user.UserReponseClan;
import com.server.dto.response.user.UserResponse;
import com.server.entity.TeamTournament;
import com.server.entity.User;
import com.server.entity.common.AuditTable;
import com.server.enums.RequestStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamJoinRespone {

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserReponseClan user;

  @ManyToOne
  @JoinColumn(name = "team_id", nullable = false)
  private TeamTournamentResponseClan team;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING'")
  private RequestStatusEnum status;

  @Column(columnDefinition = "text")
  private String description;
}
