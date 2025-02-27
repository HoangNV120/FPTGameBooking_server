package com.server.dto.request.user;

import com.server.enums.StatusEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserRequest extends BaseUser {

    @NotBlank(message = "Vui lòng không để trống")
    public String id;


    @NotBlank(message = "Trạng thái người dùng không được để trống.")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

}
