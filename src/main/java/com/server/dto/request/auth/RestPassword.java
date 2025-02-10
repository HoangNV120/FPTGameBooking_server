package com.server.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
