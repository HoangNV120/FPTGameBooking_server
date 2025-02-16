package com.server.dto.response.common;

import com.server.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseGlobal<T> {

    private String message;
    private int status;
    private LocalDateTime localDateTime;
    private T data;

    // Error
    public ResponseGlobal(int status, String message, LocalDateTime localDateTime) {
        this.status = status;
        this.message = message;
        this.localDateTime = localDateTime;
    }

    public ResponseGlobal(T data) {
        this.status = HttpStatus.OK.value();
        this.message = Constants.DEFAULT_MESSAGE_SUCCESSFUL;
        this.localDateTime = LocalDateTime.now().plusMinutes(7);
        this.data = data;
    }
}

