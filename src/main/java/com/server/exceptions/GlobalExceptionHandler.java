package com.server.exceptions;

import com.server.config.message.DBMessageSourceConfig;
import com.server.dto.response.common.ResponseGlobal;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @ExceptionHandler({NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseGlobal<String> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.info(ex.getClass().getName());
        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        return new ResponseGlobal<>(HttpStatus.BAD_REQUEST.value(), error, ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseGlobal<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> map = new HashMap<>();
        String message = "";
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            map.put(fieldName, errorMessage);
        });

        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            FieldError error = ex.getBindingResult().getFieldErrors().get(0);
            message = error.getDefaultMessage();
        }

        return new ResponseGlobal<>(HttpStatus.BAD_REQUEST.value(), message, ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    @ExceptionHandler({NotFoundExceptionHandler.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseGlobal<String> handleNotFoundExceptionHandler(final NotFoundExceptionHandler ex) {
        log.info(ex.getClass().getName());
        return new ResponseGlobal<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    @ExceptionHandler({BadRequestApiException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseGlobal<String> handleBadRequestExceptionHandler(final BadRequestApiException ex) {
        log.info(ex.getClass().getName());
        return new ResponseGlobal<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    @ExceptionHandler({RestApiException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseGlobal<String> recordDuplicateExceptionHandler(final RestApiException ex) {
        log.info(ex.getClass().getName());
        return new ResponseGlobal<>(HttpStatus.CONFLICT.value(),
                String.format(new DBMessageSourceConfig().getMessages("409"), ex.getMessage()), ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseGlobal<String> exception(final Exception ex) {
        log.info(ex.getMessage());
        return new ResponseGlobal<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new DBMessageSourceConfig().getMessages("500"), ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseGlobal<String> handleCustomAccessDeniedException(CustomAccessDeniedException ex) {
        return new ResponseGlobal<>(HttpStatus.FORBIDDEN.value(), ex.getMessage(), ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    // Websocket exception
    @MessageExceptionHandler(MessageHandlingException.class)
    public void handleMessageException(MessageHandlingException ex, @Header("simpSessionId") String sessionId) {
        MessageHandlingException errorObject = new MessageHandlingException(ex.getMessage());
        simpMessagingTemplate.convertAndSend("/subscribe/error/" + sessionId, errorObject);
    }

    @MessageExceptionHandler
    public void handleException(Exception ex, @Header("simpSessionId") String sessionId) {
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
            List<ErrorModel> errors = violations.stream()
                    .map(violation -> new ErrorModel(getPropertyName(violation.getPropertyPath()), violation.getMessage()))
                    .toList();
            simpMessagingTemplate.convertAndSend("/subscribe/error/" + sessionId, errors);
        }
    }

    public String getPropertyName(Path path) {
        String pathStr = path.toString();
        String[] comps = pathStr.split("\\.");
        if (comps.length > 0) {
            return comps[comps.length - 1];
        } else {
            return pathStr;
        }
    }

}

