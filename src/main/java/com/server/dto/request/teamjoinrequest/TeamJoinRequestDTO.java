package com.server.dto.request.teamjoinrequest;

import com.server.entity.TeamTournament;
import com.server.entity.User;
import com.server.enums.RequestStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class TeamJoinRequestDTO {
  private String userId;
  private String teamId;
  private String description;
}
