package com.server.dto.request.notification;

import com.server.dto.request.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FindNotificationRequest extends PageableRequest {
    private String userId;
}
