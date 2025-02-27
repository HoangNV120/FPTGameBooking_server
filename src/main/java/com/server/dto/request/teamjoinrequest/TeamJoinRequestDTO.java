package com.server.dto.request.teamjoinrequest;

import lombok.*;

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
