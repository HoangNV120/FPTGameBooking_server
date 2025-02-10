package com.server.config.websockets;

import com.google.gson.Gson;
import com.server.dto.request.message.WebsocketMessage;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageEncoder implements Encoder.Text<WebsocketMessage> {
    @Override
    public String encode(WebsocketMessage message) {
        Gson gson = new Gson();
        return gson.toJson(message);
    }

    @Override
    public void init(EndpointConfig config) {
        log.info("EndpointConfig: config = {}", config);
    }

    @Override
    public void destroy() {
        log.info("destroy");
    }
}
