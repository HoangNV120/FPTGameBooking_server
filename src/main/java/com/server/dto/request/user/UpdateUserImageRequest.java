package com.server.dto.request.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserImageRequest {
    private String id;
    private MultipartFile avatar;
}
