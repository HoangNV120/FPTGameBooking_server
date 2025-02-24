package com.server.controller;

import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.userteam.UserRoomGameResponse;
import com.server.dto.response.userteam.UserTeamResponse;
import com.server.service.UserTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/user-team")
@RequiredArgsConstructor
public class UserTeamRestController {

    private final UserTeamService userTeamService;

    /**
     * Tìm kiếm người dùng dựa trên các tham số tìm kiếm.
     *
     * @param codeRoom code của room.
     * @param status   trạng thái của user join team.
     * @return ResponseGlobal<List < UserTeamResponse>> chứa danh sách người dùng theo team
     */
    @PostMapping
    public ResponseGlobal<List<UserTeamResponse>> viewUserTeam(@RequestParam("codeRoom") String codeRoom,
                                                               @RequestParam("status") String status,
                                                               @RequestParam("userId") String userId) {
        log.info("viewUserTeam: roomId = {}", codeRoom);
        return new ResponseGlobal<>(userTeamService.findByRoom(codeRoom, status, userId));
    }


    @PostMapping("/remove-user-team")
    public ResponseGlobal<UserTeamResponse> removeUserFromTeam(@RequestParam("userTeamId") String userTeamId) {
        log.info("removeUserFromTeam: userTeamId = {}", userTeamId);
        return new ResponseGlobal<>(userTeamService.removeUserFromTeam(userTeamId));
    }

    @PostMapping("/find-by-user-id")
    public ResponseGlobal<List<UserTeamResponse>> findByUserId(@RequestParam("userId") String userId) {
        log.info("findByUserId: userId = {}", userId);
        return new ResponseGlobal<>(userTeamService.findByUserIdAndStatusActive(userId));
    }

    @GetMapping("/get-room-code-and-game-code-by-user-id")
    public ResponseGlobal<UserRoomGameResponse> getRoomIdByUserId(@RequestParam("userId") String userId) {
        log.info("getRoomIdByUserId: userId = {}", userId);
        return new ResponseGlobal<>(userTeamService.findRoomCodeAndGameCodeByUserId(userId));
    }

    @GetMapping("/get-by-user-id-and-room-code")
    public ResponseGlobal<UserTeamResponse> getByUserIdAndRoomCode(@RequestParam("userId") String userId,
                                                                   @RequestParam("codeRoom") String codeRoom) {
        log.info("getByUserIdAndRoomCode: userId = {}, codeRoom = {}", userId, codeRoom);
        return new ResponseGlobal<>(userTeamService.getByTeamRoomIdAndUserIdAndRole(userId, codeRoom));
    }
}
