package com.server.dto.response.common;

import com.server.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseGlobal<T> {

    private String message;
    private int status;
    private ZonedDateTime zonedDateTime;
    private T data;

    // Error
    public ResponseGlobal(int status, String message, ZonedDateTime zonedDateTime) {
        this.status = status;
        this.message = message;
        this.zonedDateTime = zonedDateTime;
    }

    public ResponseGlobal(T data) {
        this.status = HttpStatus.OK.value();
        this.message = Constants.DEFAULT_MESSAGE_SUCCESSFUL;
        this.zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        this.data = data;
    }
}

