package com.dev.monkey_dev.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private ApiStatus status;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(new ApiStatus(StatusCode.SUCCESS))
                .data(data)
                .build();
    }

    public static ApiResponse<Object> successMessage(String message) {
        return ApiResponse.builder()
                .status(new ApiStatus(StatusCode.SUCCESS))
                .build();
    }

    public static ApiResponse<Object> error(String message, ApiStatus status) {
        return ApiResponse.builder()
                .status(status)
                .build();
    }
}
