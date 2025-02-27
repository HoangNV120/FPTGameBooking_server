package com.server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.server.constants.Constants;
import com.server.dto.request.user.CreateUserRequest;
import com.server.dto.request.user.FindUserRequest;
import com.server.dto.request.user.UpdateUserRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.user.UserImageResponse;
import com.server.dto.response.user.UserResponse;
import com.server.entity.User;
import com.server.enums.LevelEnum;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.exceptions.RestApiException;
import com.server.repository.UserRepository;
import com.server.repository.specifications.UserSpecification;
import com.server.service.UserService;
import jakarta.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final Cloudinary cloudinary;

    /**
     * Tìm tất cả người dùng dựa trên yêu cầu tìm kiếm.
     *
     * @param request chứa các tham số tìm kiếm người dùng.
     * @return PageableObject<UserResponse> chứa danh sách người dùng.
     */
    @Override
    public PageableObject<UserResponse> findAll(FindUserRequest request) {
        log.info("Find all user request: {}", request);

        PageRequest pageRequest = PageRequest.of(request.getPageNo(), request.getPageSize());
        Page<User> userPage = userRepository.findAll(UserSpecification.getSpecifications(request), pageRequest);

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());

        Page<UserResponse> page = new PageImpl<>(userResponses, pageRequest, userPage.getTotalElements());

        return new PageableObject<>(page);
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

    /**
     * Active tài khoản khi bấm vào link.
     *
     * @param token của người dùng cần tìm.
     * @return UserResponse thông tin người dùng.
     */
    @Override
    public UserResponse UpdateStatusAccount(String token) {
        User user = userRepository.findUsersByActiveToken(token)
                .orElseThrow(() -> new RestApiException("Link kích hoạt không hợp lệ hoặc đã hết hạn."));

        user.setStatus(StatusEnum.ACTIVE);
        user.setActiveToken(null); // Xóa token sau khi kích hoạt
        userRepository.save(user);
        return convertUser(user);
    }

    /**
     * Tải lên một hình ảnh lên Cloudinary.
     *
     * @param file     Hình ảnh cần tải lên.
     * @param publicId ID công khai của hình ảnh.
     * @return URL của hình ảnh đã tải lên.
     * @throws IOException Nếu có lỗi trong quá trình tải lên.
     */

    @Override
    public UserImageResponse uploadImage(MultipartFile file, String publicId) throws IOException {
        // Xóa ảnh cũ trước khi upload (nếu tồn tại)
        deleteImage(publicId);

        // Upload ảnh mới
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("public_id", publicId));

        // Lấy thông tin người dùng từ ID công khai
        User user = userRepository.findById(publicId)
                .orElseThrow(() -> new NotFoundExceptionHandler("Người dùng không tồn tại."));

        // Cập nhật ảnh đại diện của người dùng
        user.setAvatar(uploadResult.get("secure_url").toString());
        userRepository.save(user);

        return new UserImageResponse(uploadResult.get("secure_url").toString());
    }

    @Override
    public byte[] exportToExcel(List<UserResponse> userResponses) {
        try (
                XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Users");
            XSSFRow headerRow = sheet.createRow(0);

            String[] headers = {"Stt", "ID", "Name", "Email", "Point", "Level", "Role", "Status", "Avatar", "Created Date", "Updated Date"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowIdx = 1; // Bắt đầu từ dòng 1
            int stt = 1; // STT bắt đầu từ 1
            for (UserResponse user : userResponses) {
                XSSFRow row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(stt++); // Cột STT bắt đầu từ 1
                row.createCell(1).setCellValue(user.getId());
                row.createCell(2).setCellValue(user.getName());
                row.createCell(3).setCellValue(user.getEmail());
                row.createCell(4).setCellValue(user.getPoint());
                row.createCell(5).setCellValue(user.getLevel().toString());
                row.createCell(6).setCellValue(user.getRole().toString());
                row.createCell(7).setCellValue(user.getStatus().toString());
                row.createCell(8).setCellValue(user.getAvatar());
                row.createCell(9).setCellValue(user.getCreatedDate() != null ? user.getCreatedDate().format(formatter) : "");
                row.createCell(10).setCellValue(user.getUpdatedDate() != null ? user.getUpdatedDate().format(formatter) : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel file", e);
        }
    }


    @Override
    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream().map(this::convertUser).collect(Collectors.toList());
    }

    private void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.out.println("Không thể xóa ảnh cũ: " + e.getMessage());
        }
    }

}

