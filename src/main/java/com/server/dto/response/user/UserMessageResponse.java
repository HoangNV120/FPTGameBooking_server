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
public class UserMessageResponse {
    private String id;
    private String name;
    private LevelEnum level;
    private StatusEnum status;
    private String avatar;
}
