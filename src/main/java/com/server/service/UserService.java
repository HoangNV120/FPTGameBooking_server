package com.server.service;


import com.server.dto.request.user.CreateUserRequest;
import com.server.dto.request.user.FindUserRequest;
import com.server.dto.request.user.UpdateUserRequest;
import com.server.dto.response.user.UserImageResponse;
import com.server.dto.response.user.UserResponse;
import com.server.entity.User;
import com.server.service.common.BaseService;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends BaseService<UserResponse, CreateUserRequest,
        UpdateUserRequest,FindUserRequest> {

    UserResponse findByEmail(String email);

    UserResponse UpdateStatusAccount(String token);

    UserImageResponse uploadImage(MultipartFile file, String publicId) throws IOException;

    byte[] exportToExcel(List<UserResponse> userList);
    List<UserResponse> findAllUsers();
}
