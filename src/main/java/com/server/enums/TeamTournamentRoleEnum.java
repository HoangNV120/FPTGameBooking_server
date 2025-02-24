package com.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamTournamentRoleEnum {

    LEADER(1, "Leader"),
    MEMBER(2, "Member");

    private final Integer value;
    private final String description;

    public static TeamTournamentRoleEnum fromString(String role) {
        for (TeamTournamentRoleEnum s : TeamTournamentRoleEnum.values()) {
            if (s.name().equalsIgnoreCase(role)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho: " + role);
    }

    public static TeamTournamentRoleEnum fromValue(Integer value) {
        for (TeamTournamentRoleEnum role : TeamTournamentRoleEnum.values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho value: " + value);
    }
}
