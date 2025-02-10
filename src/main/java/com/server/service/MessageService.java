package com.server.service;

import com.server.dto.request.message.WebsocketMessage;
import com.server.dto.response.message.MessageResponse;

import java.util.List;

public interface MessageService {

    MessageResponse addWebsocketMessage(WebsocketMessage message);

    List<MessageResponse> getAllMessagesByCodeRoom(String codeRoom);

    <T> void sendMessage(String url, T data);

    MessageResponse pinMessageToRoom(WebsocketMessage requestMessage);

    List<MessageResponse> getAllMessagesByMessageType(String messageType);
}
