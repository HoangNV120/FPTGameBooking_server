package com.server.dto.request.common;

import com.server.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class PageableRequest {

    private int pageNo = Constants.DEFAULT_PAGE;
    private int pageSize = Constants.DEFAULT_SIZE;
}
