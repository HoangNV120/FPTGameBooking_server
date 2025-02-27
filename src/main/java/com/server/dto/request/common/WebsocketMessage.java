package com.server.dto.request.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class WebsocketMessage {
    private String from;
    private String to;
    private String content;
    private String messageType;
}
