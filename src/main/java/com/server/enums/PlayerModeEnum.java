package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlayerModeEnum {

    ONE_VS_ONE(1, "Chế độ 1 vs 1"),
    THREE_VS_THREE(3, "Chế độ 3 vs 3"),
    FIVE_VS_FIVE(5, "Chế độ 5 vs 5");

    private final Integer value;
    private final String description;

    /**
     * Phương thức chuyển đổi chuỗi tên enum (ONE_VS_ONE, THREE_VS_THREE, ...) thành giá trị enum tương ứng.
     *
     * @param typeRoom chuỗi tên enum (ví dụ: "ONE_VS_ONE", "THREE_VS_THREE").
     * @return TypeRoomEnum tương ứng với tên typeRoom.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho chuỗi.
     */
    public static PlayerModeEnum fromString(String typeRoom) {
        for (PlayerModeEnum type : PlayerModeEnum.values()) {
            if (type.name().equalsIgnoreCase(typeRoom)) {
                return type;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + typeRoom);
    }

    /**
     * Phương thức chuyển đổi từ value (integer) sang TypeRoomEnum.
     *
     * @param value giá trị của typeRoom, ví dụ 1 cho ONE_VS_ONE, 3 cho THREE_VS_THREE.
     * @return TypeRoomEnum tương ứng với giá trị.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho value.
     */
    public static PlayerModeEnum fromValue(Integer value) {
        for (PlayerModeEnum type : PlayerModeEnum.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}
