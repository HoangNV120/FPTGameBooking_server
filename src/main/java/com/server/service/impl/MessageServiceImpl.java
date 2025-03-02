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
import com.server.util.SensitiveWordFilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SensitiveWordFilterUtil sensitiveWordFilterUtil;


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

        String filteredWords = sensitiveWordFilterUtil.filterSensitiveWords(message.getContent());
        String censoredContent = sensitiveWordFilterUtil.censorWords(message.getContent(), filteredWords);

        Message messageEntity = Message.builder()
                .room(room)
                .userSend(optionalUser.get())
                .content(censoredContent)
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

//    private String filterSensitiveWords(String inputText) {
//        String prompt = "Analyze the following text and return a JSON array of explicit, offensive, or inappropriate words ONLY. Do not include names, brands, or neutral words. Ensure the output is in JSON format. Text: \""
//                + inputText + "\".";
//
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
//        );
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity(API_URL, requestEntity, String.class);
//        return extractSensitiveWords(responseEntity.getBody());
//    }
//
//    private String extractSensitiveWords(String jsonResponse) {
//        try {
//            Map<?, ?> responseMap = objectMapper.readValue(jsonResponse, Map.class);
//            List<?> candidates = (List<?>) responseMap.get("candidates");
//
//            if (candidates != null && !candidates.isEmpty()) {
//                Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
//                Map<?, ?> content = (Map<?, ?>) candidate.get("content");
//                List<?> parts = (List<?>) content.get("parts");
//                if (parts != null && !parts.isEmpty()) {
//                    Map<?, ?> part = (Map<?, ?>) parts.get(0);
//                    String text = (String) part.get("text");
//                    text = text.replace("```json", "").replace("```", "").trim();
//                    return text.substring(1, text.length() - 1);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    private String censorWords(String text, String sensitiveWords) {
//        String[] words = sensitiveWords.replaceAll("[\\[\\]\"]", "").split(",");
//
//        for (String word : words) {
//            word = word.trim();
//            if (word.isEmpty()) continue;
//
//            String regex = "(?iu)(?<!\\pL)" + Pattern.quote(word) + "(?!\\pL)";
//
//            StringBuilder censored = new StringBuilder();
//            for (char c : word.toCharArray()) {
//                if (c == ' ') {
//                    censored.append(" ");
//                } else {
//                    censored.append("*");
//                }
//            }
//
//            text = text.replaceAll(regex, censored.toString());
//        }
//        return text;
//    }
}
