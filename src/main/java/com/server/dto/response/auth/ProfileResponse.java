package com.server.dto.response.auth;

import com.server.enums.LevelEnum;
import com.server.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String id;
    private String name;
    private String email;
    private LevelEnum level;
    private RoleEnum role;
    private int point;
    private String avatar;
}
