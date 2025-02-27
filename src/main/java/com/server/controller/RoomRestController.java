package com.server.controller;

import com.server.dto.request.room.CreateRoomRequest;
import com.server.dto.request.room.FindRoomRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.room.RoomResponse;
import com.server.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/room")
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomService roomService;

    /**
     * Tìm kiếm danh sách room theo điều kiện.
     *
     * @param request Đối tượng chứa các tiêu chí tìm kiếm room (ví dụ: phân trang, lọc theo code, tên,...).
     * @return Đối tượng phản hồi toàn cục chứa danh sách room và thông tin phân trang.
     */
    @PostMapping("/search")
    public ResponseGlobal<PageableObject<RoomResponse>> view(@RequestBody FindRoomRequest request,
                                                             Principal principal) {
        log.info("Request find==> request = {}", request.toString());
        String name = ObjectUtils.isNotEmpty(principal)
                ? principal.getName() : null;
        request.setInfoUser(name);
        return new ResponseGlobal<>(roomService.findAll(request));
    }

    /**
     * Thêm một room mới vào hệ thống.
     *
     * @param request Đối tượng chứa thông tin room cần thêm mới (được xác thực bằng @Valid).
     * @return Đối tượng phản hồi toàn cục chứa thông tin room sau khi thêm thành công.
     */
    @PostMapping
    public ResponseGlobal<RoomResponse> add(@Valid @RequestBody CreateRoomRequest request,
                                            Principal principal) {
        log.info("Create room====> request = {}", request.toString());
        String name = ObjectUtils.isNotEmpty(principal)
                ? principal.getName() : null;
        log.info("Principal = {}", name);
        request.setInfoUser(name);

        return new ResponseGlobal<>(roomService.add(request));
    }

    @GetMapping("/find-room-by-code")
    public ResponseGlobal<RoomResponse> findRoomByCode(@RequestParam("codeRoom") String codeRoom) {
        log.info("FindRoomByCode====> codeRoom = {}", codeRoom);
        return new ResponseGlobal<>(roomService.findRoomByCode(codeRoom));
    }

    @DeleteMapping("/delete-room")
    public ResponseGlobal<RoomResponse> removeRoom(@RequestParam("codeRoom") String codeRoom,
                                                   @RequestParam("codeGame") String codeGame,
                                                   Principal principal) {
        log.info("removeRoom====> codeRoom = {}", codeRoom);
        String name = ObjectUtils.isNotEmpty(principal)
                ? principal.getName() : null;
        return new ResponseGlobal<>(roomService.removeRoom(codeGame, codeRoom, name));
    }
}

