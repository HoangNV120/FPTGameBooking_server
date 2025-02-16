package com.server.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FindUserByIdRequest {
    private String userId;
}
