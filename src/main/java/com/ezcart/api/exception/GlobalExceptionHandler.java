package com.ezcart.api.exception;

import com.ezcart.api.common.api.ApiResponse;
import com.ezcart.api.common.api.StatusCode;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice(basePackages = "com.ezcart.api.controller")
public class GlobalExceptionHandler {

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
                return ResponseEntity.status(ex.getStatusCode().getHttpStatus())
                                .body(ApiResponse.error(ex.getStatusCode().getMessage(),
                                                ex.getStatusCode().getHttpStatus()));
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
                                .body(ApiResponse.error(StatusCode.BAD_REQUEST.getMessage(),
                                                StatusCode.BAD_REQUEST.getHttpStatus()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(StatusCode.VALIDATION_FAILED.getMessage(),
                                                StatusCode.VALIDATION_FAILED.getHttpStatus()));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(StatusCode.INVALID_REQUEST_BODY.getMessage(),
                                                StatusCode.INVALID_REQUEST_BODY.getHttpStatus()));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(StatusCode.VALIDATION_FAILED.getMessage(),
                                                StatusCode.VALIDATION_FAILED.getHttpStatus()));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(StatusCode.ACCESS_DENIED.getMessage(),
                                                StatusCode.ACCESS_DENIED.getHttpStatus()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Internal server error",
                                                HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
}

