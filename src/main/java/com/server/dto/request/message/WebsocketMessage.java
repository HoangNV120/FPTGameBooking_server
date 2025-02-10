package com.server.dto.request.message;

import com.server.enums.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
