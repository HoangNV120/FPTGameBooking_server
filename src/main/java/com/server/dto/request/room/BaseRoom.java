package com.server.dto.request.room;

import com.server.enums.LevelRoomEnum;
import com.server.enums.PlayerModeEnum;
import com.server.enums.RankEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
public abstract class BaseRoom {
    private String infoUser;

    @NotBlank(message = "codeGame không để trống")
    @Size(min = 3, max = 50, message = "codeGame phải có từ 3 đến 50 ký tự")
    private String codeGame;

    @NotBlank(message = "Tên phòng không để trống")
    private String name;

    private Integer pointBetLevel;

    @NotNull(message = "Rank không để trống")
    private List<RankEnum> rankPlayer;

    @NotNull(message = "Chế độ chơi không để trống.")
    private List<PlayerModeEnum> playerModes;

    @NotNull(message = "Thời gian không để trống.")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotBlank(message = "Mô tả không để trống.")
    private String description;

    private LevelRoomEnum level;
}
