package com.dev.monkey_dev.common.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    // private ApiStatus status;
    private String message;
    private int statusCode;
    private String timestamp;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                // .status(new ApiStatus(StatusCode.SUCCESS))
                .message(StatusCode.SUCCESS.getMessage())
                .statusCode(StatusCode.SUCCESS.getHttpStatus())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .data(data)
                .build();
    }

    public static ApiResponse<Object> successMessage(String message) {
        return ApiResponse.builder()
                // .status(new ApiStatus(StatusCode.SUCCESS))
                .message(message)
                .statusCode(StatusCode.SUCCESS.getHttpStatus())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                // .data(null)
                .build();
    }

    public static ApiResponse<Object> error(String message, int httpStatus) {
        return ApiResponse.builder()
                // .status(status)
                .message(message)
                .statusCode(httpStatus)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                // .data(null)
                .build();
    }
}
