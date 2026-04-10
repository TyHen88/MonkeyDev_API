package com.ezcart.api.controller.base;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.ezcart.api.common.api.ApiResponse;

public abstract class BaseApiRestController {

    protected ResponseEntity<ApiResponse<Object>> success(Object data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    protected ResponseEntity<ApiResponse<Object>> created(Object data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }

    protected ResponseEntity<ApiResponse<Object>> createdMessage(String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.successMessage(message));
    }

    protected ResponseEntity<ApiResponse<Object>> successMessage(String message) {
        return ResponseEntity.ok(ApiResponse.successMessage(message));
    }

    protected ResponseEntity<ApiResponse<Object>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message, status.value()));
    }
}

