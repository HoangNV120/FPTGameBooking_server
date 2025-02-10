package com.server.dto.request.image;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateImageRequest {
    private String idType;
    private String url;
    private String name;
}
