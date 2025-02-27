package com.server.dto.request.tournament;

import com.server.dto.request.tournamentmatch.TournamentMatchRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TournamentRequest {

    private String userId;
    @NotNull(message = "Tên giải đấu không được để trống")
    private String name;

    @Pattern(regexp = "[48]", message = "Số lượng đội không hợp lệ")
    private int numberOfTeam;
    private String description;
    @NotNull(message = "Phần thưởng không được để trống")
    private Integer totalPrize;
    private Integer top1Prize;
    private Integer top2Prize;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    @Pattern(regexp = "[56]", message = "Số lượng thành viên không hợp lệ")
    private Integer teamMemberCount;

    List<TournamentMatchRequest> tournamentMatchRequests;

}
