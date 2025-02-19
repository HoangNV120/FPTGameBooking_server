package com.server.service.impl;

import com.server.dto.request.systemstreamlink.SystemStreamLinkRequest;
import com.server.dto.response.systemstreamlink.SystemStreamLinkResponse;
import com.server.entity.SystemStreamLink;
import com.server.repository.SystemStreamLinkRepository;
import com.server.service.SystemStreamLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemStreamLinkImpl implements SystemStreamLinkService {

    private final SystemStreamLinkRepository systemStreamLinkRepository;
    private final ModelMapper modelMapper;

    @Override
    public SystemStreamLinkResponse getLatestStreamLink() {
        return new SystemStreamLinkResponse(systemStreamLinkRepository.findLatestStreamLink().getUrl());
    }

    @Override
    public SystemStreamLinkResponse updateStreamLink(SystemStreamLinkRequest request) {
        SystemStreamLink systemStreamLink = convertToEntity(request);
        systemStreamLinkRepository.save(systemStreamLink);
        return convertToResponse(systemStreamLink);
    }

    private SystemStreamLinkResponse convertToResponse(SystemStreamLink systemStreamLink) {
        return modelMapper.map(systemStreamLink, SystemStreamLinkResponse.class);
    }

    private SystemStreamLink convertToEntity(SystemStreamLinkRequest request) {
        return modelMapper.map(request, SystemStreamLink.class);
    }

}
