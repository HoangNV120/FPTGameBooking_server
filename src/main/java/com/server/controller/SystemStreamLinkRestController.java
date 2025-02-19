package com.server.controller;

import com.server.dto.request.systemstreamlink.SystemStreamLinkRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.systemstreamlink.SystemStreamLinkResponse;
import com.server.service.SystemStreamLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/system-stream-link")
@RequiredArgsConstructor
public class SystemStreamLinkRestController {

    private final SystemStreamLinkService systemStreamLinkService;

    @PutMapping("/update")
    public ResponseGlobal<SystemStreamLinkResponse> updateSystemStreamLink(@RequestBody SystemStreamLinkRequest systemStreamLinkRequest) {
            SystemStreamLinkResponse systemStreamLinkResponse = systemStreamLinkService.updateStreamLink(systemStreamLinkRequest);
            return new ResponseGlobal<>(systemStreamLinkResponse);
    }

    @GetMapping("/view")
    public ResponseGlobal<SystemStreamLinkResponse> getSystemStreamLink() {
        SystemStreamLinkResponse systemStreamLinkResponse = systemStreamLinkService.getLatestStreamLink();
        return new ResponseGlobal<>(systemStreamLinkResponse);
    }
}
