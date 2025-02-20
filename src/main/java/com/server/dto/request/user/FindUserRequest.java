package com.server.dto.request.user;

import com.server.dto.request.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindUserRequest extends PageableRequest {
    private String id;
    private String name;
    private String email;
    private String level;
    private String status;
    private String CreatedDateOrder;
    private String UpdatedDateOrder;
    private String pointOrder;
}
