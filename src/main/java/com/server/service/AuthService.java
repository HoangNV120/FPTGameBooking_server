package com.server.service;


import com.server.dto.request.auth.LoginRequest;
import com.server.dto.request.auth.RestPassword;
import com.server.dto.request.auth.SignInRequest;
import com.server.dto.response.auth.LoginResponse;
import com.server.dto.response.auth.ProfileResponse;
import java.util.Optional;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);

    String signUp(SignInRequest loginRequest);

    ProfileResponse getProfileUser(String email);

    String forgotPassword(String email);

    String resetPassword(RestPassword req);
}
