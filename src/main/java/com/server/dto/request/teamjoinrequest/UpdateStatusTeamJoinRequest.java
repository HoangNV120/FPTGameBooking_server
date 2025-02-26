package com.server.dto.request.teamjoinrequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateStatusTeamJoinRequest {
  private String userId;
  private String teamId;
  private boolean decision;
}
