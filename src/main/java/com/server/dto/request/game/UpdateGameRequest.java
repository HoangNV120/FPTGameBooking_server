package com.server.dto.request.game;

import com.server.enums.DisplayEnum;
import com.server.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateGameRequest extends BaseGame{
    private DisplayEnum display;
    private StatusEnum status;
}
