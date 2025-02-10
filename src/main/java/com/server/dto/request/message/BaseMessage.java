package com.server.dto.request.message;

import com.server.entity.Room;
import com.server.entity.Team;
import com.server.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class BaseMessage {
    private User userSend;
    private User userReceive;
    private Team team;
    private Room room;
    private String type;
    private String content;
}
