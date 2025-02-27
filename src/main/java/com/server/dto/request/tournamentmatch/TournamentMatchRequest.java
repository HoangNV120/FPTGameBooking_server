package com.server.dto.request.tournamentmatch;

import com.server.enums.MatchTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TournamentMatchRequest {
    @NotNull(message = "Loại không được để trống")
    private MatchTypeEnum type;
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;
}
