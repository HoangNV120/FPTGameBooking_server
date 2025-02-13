package com.server.dto.response.message;

import com.server.dto.response.room.RoomResponse;
import com.server.dto.response.team.TeamResponse;
import com.server.dto.response.user.UserMessageResponse;
import com.server.dto.response.user.UserResponse;
import com.server.enums.MessageStatusEnum;
import com.server.enums.MessageTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageResponse {
    private String id;
    private UserMessageResponse userSend;
    private UserMessageResponse userReceive;
    private TeamResponse team;
    private RoomResponse room;
    private MessageTypeEnum messageType;
    private MessageStatusEnum messageStatus;
    private String content;
}
