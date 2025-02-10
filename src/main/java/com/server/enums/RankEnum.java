package com.server.enums;

import com.server.exceptions.NotFoundExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankEnum {
    BRONZE(0, "Hạng Đồng"),
    SILVER(1, "Hạng Bạc"),
    GOLD(2, "Hạng Vàng"),
    PLATINUM(3, "Hạng Bạch Kim"),
    DIAMOND(4, "Hạng Kim Cương"),
    CHALLENGER(5, "Hạng Thách Đấu")
    ;

    private final Integer value;
    private final String description;

    /**
     * Phương thức chuyển đổi từ tên enum (BRONZE, SILVER, ...) sang RankEnum.
     *
     * @param rank chuỗi tên enum (ví dụ: "BRONZE", "SILVER").
     * @return RankEnum tương ứng với tên rank.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho chuỗi.
     */
    public static RankEnum fromString(String rank) {
        for (RankEnum r : RankEnum.values()) {
            if (r.name().equalsIgnoreCase(rank)) {
                return r;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho: " + rank);
    }

    /**
     * Phương thức chuyển đổi từ value (Integer) sang RankEnum.
     *
     * @param value giá trị của rank, ví dụ 0 cho BRONZE, 1 cho SILVER.
     * @return RankEnum tương ứng với giá trị.
     * @throws NotFoundExceptionHandler nếu không tìm thấy giá trị enum cho value.
     */
    public static RankEnum fromValue(Integer value) {
        for (RankEnum rank : RankEnum.values()) {
            if (rank.getValue().equals(value)) {
                return rank;
            }
        }
        throw new NotFoundExceptionHandler("Không tìm thấy giá trị enum cho value: " + value);
    }
}
