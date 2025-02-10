package com.server.dto.response.team;

import com.server.dto.response.room.RoomResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeamResponse {
    private String id;
    private String name;
    private String description;
    private RoomResponse room;
}
