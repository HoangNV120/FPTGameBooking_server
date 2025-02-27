package com.server.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email không để trống")
    private String username;

    @NotBlank(message = "password không để trống")
    private String password;
}
