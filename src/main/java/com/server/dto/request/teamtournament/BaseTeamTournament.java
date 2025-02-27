package com.server.dto.request.teamtournament;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseTeamTournament {
    @NotBlank(message = "Tên không được để trống")
    private String name;


    private String description;
    private String imageLink;
}
