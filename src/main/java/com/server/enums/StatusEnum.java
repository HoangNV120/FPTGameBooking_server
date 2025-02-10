package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    ACTIVE(0, "Đang hoạt động"),
    INACTIVE(1, "Không hoạt động"),
    PENDING(2, "Chờ phê duyệt hoặc xử lý"),
    DISCONTINUED(3, "Ngừng phát hành hoặc không còn khả dụng")
    ;

    private final Integer value;
    private final String description;


    /**
     * Phương thức chuyển đổi chuỗi tên enum (DANG_HOAT_DONG, KHONG_HOAT_DONG) thành giá trị enum tương ứng.
     *
     * @param status chuỗi tên enum (ví dụ: "DANG_HOAT_DONG", "KHONG_HOAT_DONG").
     * @return StatusEnum tương ứng với tên status.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho chuỗi.
     */
    public static StatusEnum fromString(String status) {
        for (StatusEnum s : StatusEnum.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + status);
    }

    /**
     * Phương thức chuyển đổi từ value (integer) sang StatusEnum.
     *
     * @param value giá trị của status, ví dụ 1 cho DANG_HOAT_DONG, 0 cho KHONG_HOAT_DONG.
     * @return StatusEnum tương ứng với giá trị.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho value.
     */
    public static StatusEnum fromValue(Integer value) {
        for (StatusEnum status : StatusEnum.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}
