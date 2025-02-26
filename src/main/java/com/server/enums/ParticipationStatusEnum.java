package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipationStatusEnum {
    PARTICIPATING(1,"PARTICIPATING"),
    STARTED(2,"STARTED"),
    ENDED(3,"ENDED");

    private final Integer value;
    private final String description;

    public static ParticipationStatusEnum fromString(String status) {
        for (ParticipationStatusEnum s : ParticipationStatusEnum.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + status);
    }

    public static ParticipationStatusEnum fromValue(String value) {
        for (ParticipationStatusEnum status : ParticipationStatusEnum.values()) {
            if (status.getDescription().equals(value)) {
                return status;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}