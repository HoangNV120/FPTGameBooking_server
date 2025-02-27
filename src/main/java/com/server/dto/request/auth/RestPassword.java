package com.server.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RestPassword {
    @NotBlank(message = "oldPassword không để trống")
    private String oldPassword;
    @NotBlank(message = "newPassword không để trống")
    private String newPassword;
    private String idUser;
}
