package com.server.dto.response.game;

import com.server.enums.DisplayEnum;
import com.server.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {
    private String code;
    private String name;
    private String banner;
    private DisplayEnum display;
    private StatusEnum status;
}
