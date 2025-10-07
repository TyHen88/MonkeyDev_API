package com.dev.monkey_dev.exception;

import com.dev.monkey_dev.common.api.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.dev.monkey_dev.controller")
public class GlobalExceptionHandler {

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
                return ResponseEntity.status(ex.getStatusCode().getHttpStatus())
                                .body(ApiResponse.error(ex.getMessage(), ex.getStatusCode().getHttpStatus()));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(ex.getMessage(),
                                                HttpStatus.NOT_FOUND.value()));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Object>> handleBadRequest(IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ex.getMessage(),
                                                HttpStatus.BAD_REQUEST.value()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Internal server error",
                                                HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
}
