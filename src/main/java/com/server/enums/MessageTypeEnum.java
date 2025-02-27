package com.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    SYSTEM(0, "Tin nhắn hệ thống."),
    PRIVATE(1, "Tin nhắn giữa các cá nhân."),
    GROUP(2, "Tin nhắn trong nhóm."),
    ALL(3, "Tin nhắn trong server.");

    private final Integer value;
    private final String description;

    /**
     * Chuyển đổi từ tên enum thành đối tượng MessageTypeEnum tương ứng.
     *
     * @param msg chuỗi tên enum (ví dụ "PRIVATE" hoặc "GROUP").
     * @return MessageTypeEnum tương ứng với tên enum.
     * @throws IllegalArgumentException nếu không tìm thấy enum tương ứng.
     */
    public static MessageTypeEnum fromString(String msg) {
        for (MessageTypeEnum s : MessageTypeEnum.values()) {
            if (s.name().equalsIgnoreCase(msg)) {
                return s;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho: " + msg);
    }

    /**
     * Phương thức chuyển đổi từ value (integer) sang RoleEnum.
     *
     * @param value giá trị của phạm vi gửi message
     * @return RoleEnum tương ứng với giá trị.
     * @throws IllegalArgumentException nếu không tìm thấy giá trị enum cho value.
     */
    public static MessageTypeEnum fromValue(Integer value) {
        for (MessageTypeEnum msg : MessageTypeEnum.values()) {
            if (msg.getValue().equals(value)) {
                return msg;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho value: " + value);
    }
}
