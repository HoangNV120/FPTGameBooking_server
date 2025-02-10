package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DisplayEnum {

    VISIBLE(0, "Hiển thị"),
    HIDDEN(1, "Không hiển thị"),
    FEATURED(2, "Hiển thị nổi bật"),
    ARCHIVED(3, "Lưu trữ và không hiển thị");

    private final Integer value;
    private final String description;

    /**
     * Phương thức chuyển đổi chuỗi tên enum (VISIBLE, HIDDEN, ...) thành giá trị enum tương ứng.
     *
     * @param display chuỗi tên enum (ví dụ: "VISIBLE", "HIDDEN").
     * @return DisplayEnum tương ứng với tên display.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho chuỗi.
     */
    public static DisplayEnum fromString(String display) {
        for (DisplayEnum d : DisplayEnum.values()) {
            if (d.name().equalsIgnoreCase(display)) {
                return d;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + display);
    }

    /**
     * Phương thức chuyển đổi từ value (integer) sang DisplayEnum.
     *
     * @param value giá trị của display, ví dụ 0 cho VISIBLE, 1 cho HIDDEN.
     * @return DisplayEnum tương ứng với giá trị.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho value.
     */
    public static DisplayEnum fromValue(Integer value) {
        for (DisplayEnum display : DisplayEnum.values()) {
            if (display.getValue().equals(value)) {
                return display;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}
