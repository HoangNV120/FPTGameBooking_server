package com.server.service;

import com.server.dto.request.systemstreamlink.SystemStreamLinkRequest;
import com.server.dto.response.systemstreamlink.SystemStreamLinkResponse;

public interface SystemStreamLinkService {
    SystemStreamLinkResponse getLatestStreamLink();

    SystemStreamLinkResponse updateStreamLink(SystemStreamLinkRequest request);
}
