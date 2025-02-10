package com.server.dto.request.team;

import com.server.entity.Room;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class BaseTeam {
    private String name;
    private String description;
    private Room room;
}
