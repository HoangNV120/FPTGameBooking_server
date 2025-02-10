package com.server.config.websockets;

import com.google.gson.Gson;
import com.server.dto.request.message.WebsocketMessage;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<WebsocketMessage> {

    @Override
    public WebsocketMessage decode(String message) {
        Gson gson = new Gson();
        return gson.fromJson(message, WebsocketMessage.class);
    }

    @Override
    public boolean willDecode(String message) {
        return (message != null);
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
