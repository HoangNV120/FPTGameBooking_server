package com.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageStatusEnum {

    DEFAULT(0, "Tin nhắn default."),
    PINNED(1, "Tin nhắn đã ghim."),
    EDITED(2, "Tin nhắn đã chỉnh sửa."),
    DELETED(3, "Tin nhắn đã xóa.");

    private final Integer value;
    private final String description;

    /**
     * Chuyển đổi từ tên enum thành đối tượng MessageStatusEnum tương ứng.
     *
     * @param msg chuỗi tên enum (ví dụ "PRIVATE" hoặc "GROUP").
     * @return MessageStatusEnum tương ứng với tên enum.
     * @throws IllegalArgumentException nếu không tìm thấy enum tương ứng.
     */
    public static MessageStatusEnum fromString(String msg) {
        for (MessageStatusEnum s : MessageStatusEnum.values()) {
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
    public static MessageStatusEnum fromValue(Integer value) {
        for (MessageStatusEnum msg : MessageStatusEnum.values()) {
            if (msg.getValue().equals(value)) {
                return msg;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho value: " + value);
    }
}
