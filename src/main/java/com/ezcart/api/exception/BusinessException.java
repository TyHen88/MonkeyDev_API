package com.ezcart.api.exception;

import com.ezcart.api.common.api.StatusCode;

/**
 * Handle exception for Business Exception
 */
public class BusinessException extends RuntimeException {
    private Object body;
    private final StatusCode statusCode;

    public BusinessException(StatusCode statusCode, Object body) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
        this.body = body;
    }

    public BusinessException(StatusCode statusCode, String message) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
        this.body = message;
    }

    public BusinessException(StatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Object getBody() {
        return body;
    }

}

