package com.server.dto.response.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String role;
}
