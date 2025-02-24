package com.server.dto.request.teamtournament;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTeamTournament extends BaseTeamTournament {
    private String userId;
    @Pattern(regexp = "[56]", message = "Số lượng thành viên không hợp lệ")
    private String memberCount;
}
