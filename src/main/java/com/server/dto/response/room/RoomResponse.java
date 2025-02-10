package com.server.dto.response.room;

import com.server.dto.response.game.GameResponse;
import com.server.enums.LevelEnum;
import com.server.enums.PlayerModeEnum;
import com.server.enums.RankEnum;
import com.server.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
public class RoomResponse {
    private String name;
    private String code;
    private Integer pointBetLevel;
    private StatusEnum status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private List<RankEnum> rankPlayer;
    private List<PlayerModeEnum> playerModes;
    private GameResponse game;
    private String nameUser;
    private String userCreate;
    private LevelEnum level;
}
