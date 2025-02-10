package com.server.dto.request.game;

import com.server.dto.request.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FindGameRequest extends PageableRequest {
    private String code;
    private String name;
}
