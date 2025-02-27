package com.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    ROLE_ADMIN(0, "Administrator"),
    ROLE_USER(1, "User"),
    ROLE_ROOM_OWNER(2, "Room Owner"),
    ROLE_MEMBER(3, "Member"),
    ROLE_TEAM_LEADER(4, "Team Leader");

    private final Integer value;
    private final String description;

    /**
     * Phương thức chuyển đổi chuỗi tên vai trò (role) thành giá trị RoleEnum tương ứng.
     *
     * @param role chuỗi tên vai trò, ví dụ "ROLE_ADMIN" hoặc "ROLE_USER".
     * @return RoleEnum tương ứng với tên vai trò.
     * @throws IllegalArgumentException nếu không tìm thấy giá trị enum cho chuỗi.
     */
    public static RoleEnum fromString(String role) {
        for (RoleEnum s : RoleEnum.values()) {
            if (s.name().equalsIgnoreCase(role)) {
                return s;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho: " + role);
    }

    /**
     * Phương thức chuyển đổi từ value (integer) sang RoleEnum.
     *
     * @param value giá trị của role, ví dụ 1 cho ROLE_ADMIN, 2 cho ROLE_USER.
     * @return RoleEnum tương ứng với giá trị.
     * @throws IllegalArgumentException nếu không tìm thấy giá trị enum cho value.
     */
    public static RoleEnum fromValue(Integer value) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        // Ném ra ngoại lệ nếu không tìm thấy giá trị enum
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho value: " + value);
    }
}
