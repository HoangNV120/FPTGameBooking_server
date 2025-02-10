package com.server.service.impl;

import com.server.constants.Constants;
import com.server.dto.request.user.CreateUserRequest;
import com.server.dto.request.user.FindUserRequest;
import com.server.dto.request.user.UpdateUserRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.user.UserResponse;
import com.server.entity.User;
import com.server.enums.LevelEnum;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.exceptions.RestApiException;
import com.server.repository.UserRepository;
import com.server.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Tìm tất cả người dùng dựa trên yêu cầu tìm kiếm.
     *
     * @param request chứa các tham số tìm kiếm người dùng.
     * @return PageableObject<UserResponse> chứa danh sách người dùng.
     */
    @Override
    public PageableObject<UserResponse> findAll(FindUserRequest request) {
        log.info("Find all user request: {}", request);

        // Chưa thực hiện tìm kiếm và phân trang, trả về null tạm thời.
        return null;
    }

    /**
     * Thêm mới người dùng vào hệ thống.
     *
     * @param req chứa thông tin người dùng cần tạo mới.
     * @return UserResponse thông tin người dùng đã được tạo.
     */
    @Override
    public UserResponse add(CreateUserRequest req) {
        Optional<User> optionalUser = userRepository.findByEmail(req.getEmail());
        if (optionalUser.isPresent()) {
            throw new RestApiException("Người dùng đã tồn tại.");
        }

        User add = new User();
        add.setEmail(req.getEmail());
        add.setName(req.getName());
        add.setPoint(0);
        add.setLevel(LevelEnum.fromString(req.getLevel()));
        add.setRole(RoleEnum.fromString(req.getRole()));
        add.setStatus(StatusEnum.ACTIVE);
        add.setPassword(passwordEncoder.encode(req.getPassword()));
        add.setAvatar(StringUtils.isNotBlank(req.getAvatar())
                ? req.getAvatar() : Constants.DEFAULT_URL_AVATAR);

        userRepository.save(add);

        return convertUser(add);
    }

    /**
     * Cập nhật thông tin người dùng.
     *
     * @param req chứa thông tin cập nhật người dùng.
     * @return UserResponse thông tin người dùng sau khi cập nhật.
     */
    @Override
    public UserResponse update(UpdateUserRequest req) {
        Optional<User> optionalUser = userRepository.findByEmail(req.getEmail());
        if (optionalUser.isEmpty()) {
            throw new NotFoundExceptionHandler("Người dùng không tồn tại.");
        }

        // Tạo đối tượng User với thông tin cập nhật.
        User update = new User();
        update.setEmail(req.getEmail());
        update.setName(req.getName());
        update.setPoint(0);
        update.setLevel(LevelEnum.fromString(req.getLevel()));
        update.setRole(RoleEnum.fromString(req.getRole()));
        update.setStatus(StatusEnum.ACTIVE);
        update.setPassword(passwordEncoder.encode(req.getPassword()));
        update.setAvatar(StringUtils.isNotBlank(req.getAvatar())
                ? req.getAvatar() : Constants.DEFAULT_URL_AVATAR);

        return convertUser(userRepository.save(update));
    }

    /**
     * Lấy thông tin người dùng theo ID.
     *
     * @param id của người dùng cần lấy thông tin.
     * @return UserResponse thông tin người dùng.
     */
    @Override
    public UserResponse getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundExceptionHandler("Người dùng không tồn tại."));

        return convertUser(user);
    }

    /**
     * Chuyển đổi đối tượng User thành UserResponse.
     *
     * @param user đối tượng User cần chuyển đổi.
     * @return UserResponse chứa thông tin người dùng.
     */
    private UserResponse convertUser(User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    /**
     * Tìm người dùng theo email.
     *
     * @param email của người dùng cần tìm.
     * @return UserResponse thông tin người dùng.
     */
    @Override
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundExceptionHandler("Người dùng không tồn tại."));

        return convertUser(user);
    }

}

