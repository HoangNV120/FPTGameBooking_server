package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchStatusEnum {
    PENDING(0, "Trận đấu đang chờ"),
    IN_PROGRESS(1, "Trận đấu đang diễn ra"),
    COMPLETED(2, "Trận đấu đã kết thúc"),
    CANCELLED(3, "Trận đấu bị hủy"),
    CONFIRMED(5, "Trận đấu đã xác nhận");

    private final Integer value;
    private final String description;

    /**
     * Phương thức chuyển đổi chuỗi tên (pending, in_progress, completed, cancelled) thành giá trị enum tương ứng.
     *
     * @param status chuỗi tên trạng thái, ví dụ "PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED".
     * @return MatchStatusEnum tương ứng với tên status.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho chuỗi.
     */
    public static MatchStatusEnum fromString(String status) {
        for (MatchStatusEnum s : MatchStatusEnum.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + status);
    }

    /**
     * Phương thức chuyển đổi từ value (integer) sang MatchStatusEnum.
     *
     * @param value giá trị của status, ví dụ 0 cho PENDING, 1 cho IN_PROGRESS, 2 cho COMPLETED, 3 cho CANCELLED.
     * @return MatchStatusEnum tương ứng với giá trị.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho value.
     */
    public static MatchStatusEnum fromValue(Integer value) {
        for (MatchStatusEnum status : MatchStatusEnum.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}
