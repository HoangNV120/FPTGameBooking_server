package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationEnum {
    INFO(0, "Thông báo thông tin"),
    WARNING(1, "Cảnh báo"),
    ERROR(2, "Lỗi"),
    SUCCESS(3, "Thành công"),
    UNREAD(4, "Chưa đọc"),
    READ(5, "Đã đọc");

    private final Integer value;
    private final String description;

    /**
     * Phương thức chuyển đổi chuỗi tên enum (ví dụ: "INFO", "UNREAD") thành giá trị enum tương ứng.
     *
     * @param notificationType chuỗi tên enum (ví dụ: "INFO", "UNREAD").
     * @return NotificationEnum tương ứng với tên.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho chuỗi.
     */
    public static NotificationEnum fromString(String notificationType) {
        for (NotificationEnum n : NotificationEnum.values()) {
            if (n.name().equalsIgnoreCase(notificationType)) {
                return n;
            }
        }
        // Ném ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + notificationType);
    }

    /**
     * Phương thức chuyển đổi từ value (integer) sang NotificationEnum.
     *
     * @param value giá trị của notificationType, ví dụ 4 cho UNREAD, 5 cho READ.
     * @return NotificationEnum tương ứng với giá trị.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho value.
     */
    public static NotificationEnum fromValue(Integer value) {
        for (NotificationEnum notification : NotificationEnum.values()) {
            if (notification.getValue().equals(value)) {
                return notification;
            }
        }
        // Ném ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}

