package com.server.dto.response.user;

import com.server.enums.LevelEnum;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private int point;
    private LevelEnum level;
    private RoleEnum role;
    private StatusEnum status;
    private String avatar;
}
