package com.server.service.impl;

import com.server.config.security.JwtUtils;
import com.server.constants.Constants;
import com.server.dto.request.auth.LoginRequest;
import com.server.dto.request.auth.RestPassword;
import com.server.dto.request.auth.SignInRequest;
import com.server.dto.response.auth.LoginResponse;
import com.server.dto.response.auth.ProfileResponse;
import com.server.entity.User;
import com.server.enums.LevelEnum;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import com.server.exceptions.CustomAccessDeniedException;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.exceptions.RestApiException;
import com.server.repository.UserRepository;
import com.server.service.AuthService;
import com.server.service.EmailService;
import com.server.util.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    @Value("${spring.app.base-url}") // Base URL từ cấu hình
    private String baseUrl;

    /**
     * Xử lý đăng nhập của người dùng.
     *
     * @param loginRequest chứa thông tin đăng nhập của người dùng.
     * @return LoginResponse chứa thông tin trả về sau khi đăng nhập thành công, bao gồm token và role.
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("LoginRequest: {}", loginRequest);

        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new NotFoundExceptionHandler("Tài khoản hoặc mật khẩu không đúng.");
        }

        if(optionalUser.get().getStatus().equals(StatusEnum.INACTIVE)){
            throw new NotFoundExceptionHandler("Tài khoản chưa được kích hoạt vui lòng check mail xác nhận!");
        }

        Authentication authentication;
        try {
            // Xác thực người dùng thông qua AuthenticationManager.
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            // Nếu xác thực thất bại, ném ngoại lệ truy cập bị từ chối.
            throw new CustomAccessDeniedException("Tài khoản hoặc mật khẩu không đúng.");
        }

        // Lưu trữ thông tin xác thực vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Lấy thông tin người dùng đã xác thực.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Lấy quyền (role) của người dùng từ authorities.
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // Tạo JWT token và refresh token cho người dùng.
        String jwtToken = jwtUtils.generateJwtToken(userDetails, optionalUser.get().getId());
        String jwtRefreshToken = jwtUtils.generateJwtRefreshToken(userDetails, optionalUser.get().getId());

        log.info("login===> jwtToken = {}, jwtRefreshToken = {}", jwtToken, jwtRefreshToken);

        return LoginResponse.builder()
                .username(userDetails.getUsername())
                .role(role)
                .accessToken(jwtToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    /**
     * Xử lý đăng ký tài khoản người dùng mới.
     *
     * @param req chứa thông tin đăng ký của người dùng.
     * @return LoginResponse chứa thông tin trả về sau khi đăng ký thành công, bao gồm token.
     */
    @Override
    public String signUp(SignInRequest req) {
        log.info("SignInRequest: {}", req);

        Optional<User> optional = userRepository.findByEmail(req.getEmail());
        if (optional.isPresent()) {
            throw new RestApiException("Tài khoản đã tồn tại.");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPoint(0);
        user.setLevel(LevelEnum.fromString(req.getLevel()));
        user.setRole(RoleEnum.fromString(req.getRole()));
        user.setStatus(StatusEnum.INACTIVE);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setAvatar(StringUtils.isNotBlank(req.getAvatar())
                ? req.getAvatar() : Constants.DEFAULT_URL_AVATAR);

        //set token send to mail to active
        String activationToken = UUID.randomUUID().toString();
        user.setActiveToken(activationToken);
        userRepository.save(user);

        // Gửi email kích hoạt
        String activationLink = baseUrl + "/activate/" + activationToken;
        emailService.sendActivationEmail(req.getEmail(), activationLink);

        return "Mở Mail click vào link để kích hoạt tài khoản";
    }

    /**
     * Lấy thông tin hồ sơ người dùng dựa trên email.
     *
     * @param email của người dùng cần lấy hồ sơ.
     * @return ProfileResponse chứa thông tin hồ sơ người dùng như email, avatar và tên.
     */
    @Override
    public ProfileResponse getProfileUser(String email) {
        log.info("Email: {}", email);

        // Tìm kiếm người dùng trong cơ sở dữ liệu theo email.
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new NotFoundExceptionHandler("Người dùng không tồn tại.");
        }

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setEmail(optionalUser.get().getEmail());
        profileResponse.setAvatar(optionalUser.get().getAvatar());
        profileResponse.setName(optionalUser.get().getName());
        profileResponse.setLevel(optionalUser.get().getLevel());
        profileResponse.setPoint(optionalUser.get().getPoint());
        profileResponse.setId(optionalUser.get().getId());
        profileResponse.setRole(optionalUser.get().getRole());
        return profileResponse;
    }

    @Override
    public String forgotPassword(String email) {
        log.info("ForgotPassword ==> Email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException("Người dùng không tồn tại."));

        String password = new RandomGenerator().randomPasswordToString();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        emailService.sendEmail(email, password);

        return "Quên thành công";
    }

    @Override
    public String resetPassword(RestPassword req) {
        log.info("🔑 Reset password for user: {}", req.getIdUser());

        User user = userRepository.findById(req.getIdUser())
                .orElseThrow(() -> new RestApiException("Người dùng không tồn tại."));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new RestApiException("Mật khẩu cũ không đúng!");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        log.info("✅ Password reset successful for user: {}", user.getId());
        return "Đã đặt lại mật khẩu thành công!";
    }



}
