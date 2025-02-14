package com.server.service;


import com.server.dto.request.user.CreateUserRequest;
import com.server.dto.request.user.FindUserRequest;
import com.server.dto.request.user.UpdateUserRequest;
import com.server.dto.response.user.UserResponse;
import com.server.entity.User;
import com.server.service.common.BaseService;

public interface UserService extends BaseService<UserResponse, CreateUserRequest,
        UpdateUserRequest,FindUserRequest> {

    UserResponse findByEmail(String email);
}
