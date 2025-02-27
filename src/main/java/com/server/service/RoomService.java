package com.server.service;

import com.server.dto.request.room.CreateRoomRequest;
import com.server.dto.request.room.FindRoomRequest;
import com.server.dto.request.room.UpdateRoomRequest;
import com.server.dto.response.room.RoomResponse;
import com.server.service.common.BaseService;

public interface RoomService extends BaseService<RoomResponse, CreateRoomRequest,
        UpdateRoomRequest, FindRoomRequest> {

    RoomResponse findRoomByCode(String code);

    RoomResponse removeRoom(String codeGame, String codeRoom, String name);
}
