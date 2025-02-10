package com.server.config.websockets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Cung cấp `ServerEndpointExporter` để cấu hình các endpoint WebSocket.
     * @return ServerEndpointExporter instance.
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * Cấu hình Message Broker.
     * @param registry đối tượng cấu hình Message Broker.
     */
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        // Định nghĩa prefix cho các API để xử lý tin nhắn từ client (FE gửi).
        // Những API này thường được đánh dấu bằng @MessageMapping.
        // Tiền tố của URL gửi thông điệp
        registry.setApplicationDestinationPrefixes("/action");
        // Định nghĩa broker cho việc gửi message đến các client.
        // Client sẽ subscribe đường dẫn có prefix "/topic".
        registry.enableSimpleBroker("/subscribe");
    }

    /**
     * Cấu hình các STOMP endpoint mà client sử dụng để kết nối WebSocket.
     * @param registry đối tượng cấu hình StompEndpointRegistry.
     */
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        // Thêm endpoint WebSocket tại "/api/our-websocket"
        registry.addEndpoint("/api/our-websocket")
                .setHandshakeHandler(new UserHandshakeHandler()) // Xử lý handshake
                .setAllowedOriginPatterns("*") // Cho phép mọi origin (cần cải thiện bảo mật)
                .withSockJS(); // Hỗ trợ giao thức SockJS cho các trình duyệt không hỗ trợ WebSocket.
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("🟢 New WebSocket Connection - Session ID: {}", sessionId);

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("🔴 WebSocket Disconnected - Session ID: {}", sessionId);
    }

}
