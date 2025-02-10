package com.server.dto.request.team;

import com.server.dto.request.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FindTeamRequest extends PageableRequest {
    private String name;
}
