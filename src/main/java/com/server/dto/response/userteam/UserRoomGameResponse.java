package com.server.dto.response.userteam;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRoomGameResponse {
    private String roomCode;
    private String gameCode;
}