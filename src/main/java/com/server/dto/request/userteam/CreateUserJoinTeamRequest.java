package com.server.dto.request.userteam;

import com.server.enums.RankEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreateUserJoinTeamRequest {
    private String userId;
    private String codeRoom;
    private RankEnum rankPlayer;
    private String description;
    private Integer pointBetLevel;
}
