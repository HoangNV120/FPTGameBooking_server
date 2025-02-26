package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchTypeEnum {
    BO1(1, "BO1"),
    BO3(3, "BO3"),
    BO5(5, "BO5");

    private final Integer value;
    private final String description;

    public static MatchTypeEnum fromValue(Integer value) {
        for (MatchTypeEnum type : MatchTypeEnum.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }

    public static MatchTypeEnum fromString(String type) {
        for (MatchTypeEnum s : MatchTypeEnum.values()) {
            if (s.name().equalsIgnoreCase(type)) {
                return s;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + type);
    }
}