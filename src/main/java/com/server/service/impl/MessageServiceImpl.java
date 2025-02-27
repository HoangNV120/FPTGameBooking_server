package com.server.service.impl;

import com.server.dto.request.message.WebsocketMessage;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.message.MessageResponse;
import com.server.entity.Message;
import com.server.entity.Room;
import com.server.entity.User;
import com.server.enums.MessageStatusEnum;
import com.server.enums.MessageTypeEnum;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.repository.MessageRepository;
import com.server.repository.RoomRepository;
import com.server.repository.UserRepository;
import com.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @Override
    public MessageResponse addWebsocketMessage(WebsocketMessage message) {
        log.info("addWebsocketMessage ===> Started ");
        log.info("WebsocketMessage: message = {}", message);

        if (message.getMessageType() != MessageTypeEnum.GROUP) {
            log.info("Add message to group");
        }
        Room room = null;
        if (StringUtils.isNotBlank(message.getCodeRoom())) {
            room = roomRepository.findRoomByCode(message.getCodeRoom())
                    .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy phòng"));
        }

        Optional<User> optionalUser = userRepository.findById(message.getUserSend());
        if (optionalUser.isEmpty()) {
            throw new NotFoundExceptionHandler("Người dùng không tồn tại");
        }

        Message messageEntity = Message.builder()
                .room(room)
                .userSend(optionalUser.get())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .messageStatus(MessageStatusEnum.DEFAULT)
                .build();
        messageRepository.save(messageEntity);

        return convertMessageResponse(messageEntity);
    }

    @Override
    public List<MessageResponse> getAllMessagesByCodeRoom(String codeRoom) {
        return messageRepository.findMessageByRoom_codeOrderByUpdatedDateAsc(codeRoom)
                .stream()
                .map(this::convertMessageResponse)
                .toList();
    }

    @Override
    @Async
    public <T> void sendMessage(String url, T data) {
        simpMessagingTemplate.convertAndSend(url, new ResponseGlobal<>(data));
    }

    @Override
    public MessageResponse pinMessageToRoom(WebsocketMessage requestMessage) {
        Message message = messageRepository.findById(requestMessage.getIdMessage())
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy tin nhắn"));

        message.setMessageStatus(MessageStatusEnum.PINNED);
        messageRepository.save(message);
        return convertMessageResponse(message);
    }

    @Override
    public List<MessageResponse> getAllMessagesByMessageType(String messageType) {

        return messageRepository.findAllByMessageTypeOrderByCreatedDateAsc(MessageTypeEnum.fromString(messageType))
                .stream()
                .map(this::convertMessageResponse)
                .toList();

    }

    private MessageResponse convertMessageResponse(Message message) {
        return modelMapper.map(message, MessageResponse.class);
    }
}
