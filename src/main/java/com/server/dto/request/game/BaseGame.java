package com.server.dto.request.game;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class BaseGame {
    @NotBlank(message = "Mã vui lòng không để trống.")
    private String code;

    @NotBlank(message = "Tên vui lòng không để trống.")
    private String name;

    @NotBlank(message = "Ảnh nền vui lòng không để trống.")
    private String banner;

}
