package com.server.config.websockets;

import com.sun.security.auth.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Map;

@Slf4j
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * Phương thức xác định người dùng dựa trên `userId` từ WebSocket request.
     *
     * @param request    yêu cầu HTTP khởi tạo WebSocket.
     * @param wsHandler  trình xử lý WebSocket.
     * @param attributes các thuộc tính cho phiên WebSocket.
     * @return đối tượng Principal đại diện cho người dùng.
     */
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        try {
            // Lấy các tham số query từ URI của yêu cầu WebSocket
            MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(request.getURI())
                    .build()
                    .getQueryParams();

            // Lấy giá trị tham số `userId`
            String userId = queryParams.getFirst("userId");

            // Kiểm tra `userId` null hoặc rỗng
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("userId không được null hoặc rỗng");
            }

            // Trả về đối tượng UserPrincipal với `userId`
            return new UserPrincipal(userId);
        } catch (Exception e) {
            log.info("Lỗi khi xác định người dùng: {}", e.getMessage());
            return null;
        }
    }
}
