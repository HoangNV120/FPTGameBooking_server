package com.server.dto.request.user;

import com.server.dto.request.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindUserRequest extends PageableRequest {

    private String name;
    private String email;
}
