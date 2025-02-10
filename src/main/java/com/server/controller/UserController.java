package com.server.controller;

import com.server.dto.request.user.CreateUserRequest;
import com.server.dto.request.user.FindUserRequest;
import com.server.dto.request.user.UpdateUserRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.user.UserResponse;
import com.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Tìm kiếm người dùng dựa trên các tham số tìm kiếm.
     *
     * @param request chứa các tham số tìm kiếm người dùng.
     * @return ResponseGlobal<PageableObject<UserResponse>> chứa danh sách người dùng theo yêu cầu tìm kiếm.
     */
    @PostMapping("/search")
    public ResponseGlobal<PageableObject<UserResponse>> viewUsers(@RequestBody FindUserRequest request) {
        log.info("Request find: {}", request);

        return new ResponseGlobal<>(userService.findAll(request));
    }

    /**
     * Thêm mới một người dùng vào hệ thống.
     *
     * @param request chứa thông tin người dùng cần thêm.
     * @return ResponseGlobal<UserResponse> thông tin người dùng mới được thêm vào.
     */
    @PostMapping
    public ResponseGlobal<UserResponse> add(@Valid @RequestBody CreateUserRequest request) {
        log.info("Request add: {}", request);

        return new ResponseGlobal<>(userService.add(request));
    }

    /**
     * Cập nhật thông tin người dùng.
     *
     * @param request chứa thông tin cập nhật của người dùng.
     * @return ResponseGlobal<UserResponse> thông tin người dùng sau khi cập nhật.
     */
    @PutMapping
    public ResponseGlobal<UserResponse> update(@Valid @RequestBody UpdateUserRequest request) {
        log.info("Request update: {}", request);

        return new ResponseGlobal<>(userService.update(request));
    }

    /**
     * Tìm kiếm thông tin người dùng theo email.
     *
     * @param request chứa email của người dùng cần tìm.
     * @return ResponseGlobal<UserResponse> thông tin chi tiết người dùng.
     */
    @PostMapping("/detail")
    public ResponseGlobal<UserResponse> findByEmail(@Valid @RequestBody FindUserRequest request) {
        log.info("detail: {}", request);

        return new ResponseGlobal<>(userService.findByEmail(request.getEmail()));
    }

}
