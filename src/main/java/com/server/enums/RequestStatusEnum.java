package com.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestStatusEnum {

    PENDING(1, "Pending"),
    ACCEPTED(2, "Accepted"),
    REJECTED(3, "Rejected");

    private final Integer value;
    private final String description;

    public static RequestStatusEnum fromString(String status) {
        for (RequestStatusEnum s : RequestStatusEnum.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho: " + status);
    }

    public static RequestStatusEnum fromValue(Integer value) {
        for (RequestStatusEnum status : RequestStatusEnum.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho value: " + value);
    }
}
