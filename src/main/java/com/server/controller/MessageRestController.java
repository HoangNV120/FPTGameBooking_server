package com.server.controller;

import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.message.MessageResponse;
import com.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageRestController {

    private final MessageService messageService;

    @PostMapping("/search")
    public ResponseGlobal<List<MessageResponse>> viewMessage(@RequestParam("codeRoom") String codeRoom) {
        return new ResponseGlobal<>(messageService.getAllMessagesByCodeRoom(codeRoom));
    }

    @PostMapping("/messages-type")
    public ResponseGlobal<List<MessageResponse>> viewMessageType(@RequestParam("messageType") String messageType) {
        return new ResponseGlobal<>(messageService.getAllMessagesByMessageType(messageType));
    }
}
