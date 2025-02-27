package com.server.dto.response.user;

import com.server.enums.LevelEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserReponseClan {
    private String name;
    private String email;
    private LevelEnum level;
    private String avatar;
}
