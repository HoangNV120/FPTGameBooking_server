package com.server.controller;

import com.server.config.security.JwtUtils;
import com.server.dto.request.user.CreateUserRequest;
import com.server.dto.request.user.FindUserByIdRequest;
import com.server.dto.request.user.FindUserRequest;
import com.server.dto.request.user.UpdateUserRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.user.UserImageResponse;
import com.server.dto.response.user.UserResponse;
import com.server.exceptions.UnauthorizedException;
import com.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.springframework.http.HttpHeaders;


@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * Tìm kiếm người dùng dựa trên các tham số tìm kiếm.
     *
     * @param request chứa các tham số tìm kiếm người dùng.
     * @return ResponseGlobal<PageableObject < UserResponse>> chứa danh sách người dùng theo yêu cầu tìm kiếm.
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

    @PostMapping("/update-user-image")
    public ResponseGlobal<UserImageResponse> uploadImage(@RequestParam("userId") String id,
                                                         @RequestParam("file") MultipartFile avatar,
                                                         HttpServletRequest request) throws IOException {
        log.info("Upload image request: {}", avatar.getOriginalFilename());
        // Get authenticated user's ID from JWT token
        String jwt = jwtUtils.getJwtFromHeader(request);
        String authenticatedUserId = jwtUtils.getUserIdFromJwtToken(jwt);
        if (!authenticatedUserId.equals(id)) {
            throw new UnauthorizedException("You can only change your own avatar");
        }

        return new ResponseGlobal<>(userService.uploadImage(avatar, id));
    }

    @PostMapping("/find-by-id")
    public ResponseGlobal<UserResponse> findById(@RequestBody FindUserByIdRequest request) {
        log.info("Request find by id: {}", request);

        return new ResponseGlobal<>(userService.getById(request.getUserId()));
    }

    @GetMapping("/exportToExcel")
    public ResponseEntity<byte[]> exportToExcel() {
        List<UserResponse> userResponses = userService.findAllUsers();
        byte[] excelData = userService.exportToExcel(userResponses);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("users.xlsx").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}
