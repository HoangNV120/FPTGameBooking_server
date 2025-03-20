package com.server.controller;

import com.server.config.security.JwtUtils;
import com.server.dto.request.auth.LoginRequest;
import com.server.dto.request.auth.RestPassword;
import com.server.dto.request.auth.SignInRequest;
import com.server.dto.response.auth.LoginResponse;
import com.server.dto.response.auth.ProfileResponse;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.user.UserResponse;
import com.server.exceptions.RestApiException;
import com.server.exceptions.UnauthorizedException;
import com.server.service.AuthService;
import com.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    /**
     * Đăng nhập người dùng
     *
     * @param request đối tượng chứa thông tin đăng nhập từ client
     * @return ResponseGlobal<LoginResponse> chứa thông tin trả về sau khi đăng nhập thành công
     */
    @PostMapping("/sign-in")
    public ResponseGlobal<LoginResponse> signIn(@Valid @RequestBody LoginRequest request) {
        log.info("Signing in====> request = {}", request.toString());
        return new ResponseGlobal<>(authService.login(request));
    }

    /**
     * Đăng ký người dùng mới
     *
     * @param request đối tượng chứa thông tin đăng ký từ client
     * @return ResponseGlobal<LoginResponse> chứa thông tin trả về sau khi đăng ký thành công
     */
    @PostMapping("/sign-up")
    public ResponseGlobal<String> signUp(@Valid @RequestBody SignInRequest request) {
        log.info("Signing up====> request = {}", request.toString());
        return new ResponseGlobal<>(authService.signUp(request));
    }

    @GetMapping("/activate")
    public ResponseGlobal<UserResponse> activateAccount(@RequestParam String token) {
        log.info("Active Account ====> Token = {}", token);
        return new ResponseGlobal<>(userService.UpdateStatusAccount(token));
    }

    /**
     * Lấy thông tin hồ sơ của người dùng đã đăng nhập
     *
     * @param principal chứa thông tin người dùng hiện tại (từ JWT token)
     * @return ResponseGlobal<ProfileResponse> chứa thông tin hồ sơ người dùng
     */
    @GetMapping("/profile")
    public ResponseGlobal<ProfileResponse> getProfile(Principal principal) {
        log.info("profile==> name= {}", ObjectUtils.isNotEmpty(principal)
                ? principal.getName() : null);

        return new ResponseGlobal<>(authService.getProfileUser(principal.getName()));
    }

    /**
     * Đăng xuất người dùng
     *
     * @param token JWT token từ header Authorization của client
     * @return ResponseEntity chứa thông báo đăng xuất thành công
     */
    @PostMapping("/logout")
    public ResponseGlobal<String> logout(@RequestHeader("Authorization") String token) {
        log.info("logout===> Authorization = {} ", token);
        String jwt = token.substring(7);
        jwtUtils.invalidateToken(jwt); // Hủy token
        return new ResponseGlobal<>("Logout successful");
    }

    /**
     * Quên mật khẩu người dùng
     *
     * @param email của người dùng
     * @return ResponseEntity chứa thông báo forgot thành công
     */
    @PostMapping("/forgot-password")
    public ResponseGlobal<String> forgotPassword(@RequestParam("email") String email) {
        log.info("ForgotPassword===> email = {} ", email);
        return new ResponseGlobal<>(authService.forgotPassword(email));
    }

    @PostMapping("/reset-password")
    public ResponseGlobal<String> resetPassword(@Valid @RequestBody RestPassword req, HttpServletRequest request) {
        log.info("resetPassword===> req = {} ", req);

        // Get authenticated user's ID from JWT token
        String jwt = jwtUtils.getJwtFromHeader(request);
        String authenticatedUserId = jwtUtils.getUserIdFromJwtToken(jwt);

        if (!authenticatedUserId.equals(req.getIdUser())) {
            throw new UnauthorizedException("You can only change your own password");
        }

        return new ResponseGlobal<>(authService.resetPassword(req));
    }

}
