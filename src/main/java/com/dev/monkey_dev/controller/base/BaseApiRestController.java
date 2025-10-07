package com.dev.monkey_dev.controller.base;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.dev.monkey_dev.common.api.ApiResponse;

public abstract class BaseApiRestController {

    protected <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    protected <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }

    protected ResponseEntity<String> successMessage(String message) {
        return ResponseEntity.ok(message);
    }

    protected ResponseEntity<ApiResponse<Object>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message, status.value()));
    }
}
