package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TournamentMatchStatusEnum {
    WAITING(1,"WAITING"),
    ONGOING(2,"ONGOING"),
    ENDED(3,"ENDED");

    private final Integer value;
    private final String description;

    public static TournamentMatchStatusEnum fromString(String status) {
        for (TournamentMatchStatusEnum s : TournamentMatchStatusEnum.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + status);
    }

    public static TournamentMatchStatusEnum fromValue(String value) {
        for (TournamentMatchStatusEnum status : TournamentMatchStatusEnum.values()) {
            if (status.getDescription().equals(value)) {
                return status;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}