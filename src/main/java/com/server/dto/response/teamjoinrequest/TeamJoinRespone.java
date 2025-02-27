package com.server.dto.response.teamjoinrequest;

import com.server.dto.response.user.UserMinimalResponse;
import com.server.enums.RequestStatusEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamJoinRespone {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserMinimalResponse user;

//  @ManyToOne
//  @JoinColumn(name = "team_id", nullable = false)
//  private TeamTournamentResponseClan team;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING'")
    private RequestStatusEnum status;

    @Column(columnDefinition = "text")
    private String description;
}
