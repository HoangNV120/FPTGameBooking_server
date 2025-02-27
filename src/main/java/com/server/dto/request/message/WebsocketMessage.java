package com.server.dto.request.message;

import com.server.enums.MessageTypeEnum;
import lombok.*;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WebsocketMessage {
    private String idMessage;
    private String userSend;
    private String roomCode;
    private String content;
    private String codeRoom;
    //phân loại kênh nhắn tin (nhắn team,1-1)
    private MessageTypeEnum messageType;

}
