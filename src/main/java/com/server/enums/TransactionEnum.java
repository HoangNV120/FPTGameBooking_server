package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionEnum {

    PENDING(0, "Đang chờ xử lý"),
    SUCCESS(1, "Thành công"),
    FAILED(2, "Thất bại"),
    CANCELLED(3, "Đã hủy"),
    REFUNDED(4, "Đã hoàn tiền");

    private final Integer value;
    private final String description;

    /**
     * Phương thức chuyển đổi chuỗi tên enum (PENDING, SUCCESS, ...) thành giá trị enum tương ứng.
     *
     * @param transaction chuỗi tên enum (ví dụ: "PENDING", "SUCCESS").
     * @return TransactionEnum tương ứng với tên transaction.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho chuỗi.
     */
    public static TransactionEnum fromString(String transaction) {
        for (TransactionEnum t : TransactionEnum.values()) {
            if (t.name().equalsIgnoreCase(transaction)) {
                return t;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + transaction);
    }

    /**
     * Phương thức chuyển đổi từ value (integer) sang TransactionEnum.
     *
     * @param value giá trị của transaction, ví dụ 0 cho PENDING, 1 cho SUCCESS.
     * @return TransactionEnum tương ứng với giá trị.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho value.
     */
    public static TransactionEnum fromValue(Integer value) {
        for (TransactionEnum transaction : TransactionEnum.values()) {
            if (transaction.getValue().equals(value)) {
                return transaction;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}
