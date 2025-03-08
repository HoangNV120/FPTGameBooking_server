package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchStageEnum {

    QUARTER_FINALS(1, "QUARTER_FINALS"),
    SEMI_FINALS(2, "SEMI_FINALS"),
    FINALS(3, "FINALS"),
    ROUND_OF_16(4, "ROUND_OF_16"),
    THIRD_PLACE(5, "THIRD_PLACE");


    private final int value;
    private final String stage;

    public static MatchStageEnum fromValue(int value) {
        for (MatchStageEnum stage : MatchStageEnum.values()) {
            if (stage.getValue() == value) {
                return stage;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }

    public static MatchStageEnum fromString(String stage) {
        for (MatchStageEnum s : MatchStageEnum.values()) {
            if (s.getStage().equalsIgnoreCase(stage)) {
                return s;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + stage);
    }
}
