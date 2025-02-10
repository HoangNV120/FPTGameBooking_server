package com.server.dto.request.auth;

import com.server.constants.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    @NotBlank(message = "username không để trống")
    private String name;

    @NotBlank(message = "Email vui lòng không để trống.")
    @Email(message = "Email sai định dạng.")
    private String email;

    @NotBlank(message = "Cấp độ người dùng không được để trống.")
    private String level;

    @NotBlank(message = "Vai trò không được để trống.")
    private String role;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Pattern(regexp = Constants.Regexp.REGEXP_PASSWORD,
            message = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ cái, số và ký tự đặc biệt.")
    private String password;

    private String avatar;

}
