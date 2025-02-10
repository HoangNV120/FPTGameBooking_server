package com.server.config.auditing;

import com.server.constants.Constants;
import com.server.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareConfig implements AuditorAware<String> {
    /**
     * Nhận thông tin đăng nhập của người dùng hiện tại.
     *
     * @return ID của người dùng hiện tại hoặc một giá trị
     * mặc định cho người quản trị nếu không có người dùng nào được xác thực.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        // Nếu không có người dùng đăng nhập, trả về giá trị mặc định cho quản trị viên
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of(Constants.DEFAULT_USER_ID_ADMIN);
        }

        // Nếu có người dùng đăng nhập, lấy ID người dùng hiện tại
        User userPrincipal = (User) authentication.getPrincipal();
        String userId = userPrincipal.getId();
        return Optional.ofNullable(userId).or(() -> Optional.of(Constants.DEFAULT_USER_ID_ADMIN));
    }


}


