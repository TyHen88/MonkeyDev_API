package com.dev.monkey_dev.common.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiStatus {
    private int code;
    private String message;
    private int httpStatus;

    public ApiStatus(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public ApiStatus(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.httpStatus = statusCode.getHttpStatus();
    }
}
