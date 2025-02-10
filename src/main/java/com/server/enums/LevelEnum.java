package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LevelEnum {
    NON_PREMIER(0, "Chưa nạp tiền"),
    PREMIER(1, "Đã nạp tiền");

    private final Integer value;
    private final String description;

    /**
     * Phương thức chuyển đổi chuỗi tên (non-premier, premier) thành giá trị enum tương ứng.
     *
     * @param level chuỗi tên vai trò, ví dụ "PREMIER" hoặc "NON_PREMIER".
     * @return LevelEnum tương ứng với tên level.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho chuỗi.
     */
    public static LevelEnum fromString(String level) {
        for (LevelEnum s : LevelEnum.values()) {
            if (s.name().equalsIgnoreCase(level)) {
                return s;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + level);
    }

    /**
     * Phương thức chuyển đổi từ value (integer) sang LevelEnum.
     *
     * @param value giá trị của level, ví dụ 0 cho NON_PREMIER, 1 cho PREMIER.
     * @return LevelEnum tương ứng với giá trị.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho value.
     */
    public static LevelEnum fromValue(Integer value) {
        for (LevelEnum level : LevelEnum.values()) {
            if (level.getValue().equals(value)) {
                return level;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}
