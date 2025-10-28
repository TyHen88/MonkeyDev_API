package com.dev.monkey_dev.common.api;

/**
 * Enum representing standardized status codes and messages for API responses.
 */
public enum StatusCode {

    // 200 Success
    SUCCESS(20000, "Success", 200),

    // 400 Bad Request
    BAD_REQUEST(40000, "Bad request", 400),
    PASSWORD_ENCRYPTION_REQUIRED(40016, "Password must be encrypted", 400),
    PASSWORD_MISMATCH(40017, "Password does not match", 400),
    UNSUPPORTED_OPERATION(40019, "Unsupported operation", 400),
    OTP_CODE_INCORRECT(40025, "OTP code is incorrect", 400),
    OTP_VERIFICATION_DISABLED(40026, "OTP verification is disabled", 400),
    OTP_SEND_DISABLED_5_MIN(40027, "Sending OTP is disabled for 5 minutes", 400),
    OTP_SEND_DISABLED_15_MIN(40028, "Sending OTP is disabled for 15 minutes", 400),
    OTP_CODE_EXPIRED(40029, "OTP code has expired", 400),
    INVALID_PHONE_NUMBER(40033, "Invalid phone number", 400),
    EMAIL_REQUIRED(40038, "Email is required", 400),
    IS_ACTIVE_REQUIRED(40039, "Is active is required", 400),
    INVALID_EMAIL(40039, "Email is not valid", 400),
    SECURITY_CODE_ENCRYPTION_REQUIRED(40041, "Security code must be encrypted", 400),
    SECURITY_KEY_ENCRYPTION_REQUIRED(40042, "Security key must be encrypted", 400),
    INCORRECT_PASSWORD(40055, "Incorrect password", 400),
    INACTIVE_USER(40056, "Inactive user", 400),
    INVALID_TOKEN(40051, "Invalid token", 400),

    // 401 Unauthorized
    UNAUTHORIZED(40100, "Unauthorized", 401),

    // 403 Forbidden
    FORBIDDEN(40300, "Forbidden", 403),

    // 409 Conflict
    USER_ID_ALREADY_EXISTS(40913, "User ID already exists", 409),

    // 452 Custom Client Errors
    PASSWORD_INCORRECT_452(45200, "Password is incorrect", 452),
    INVALID_SECRET(45201, "Secret is incorrect", 452),
    INCORRECT_CURRENT_PASSWORD(45202, "Current password is incorrect", 452),
    PASSWORD_INCORRECT_AGAIN(45203, "Password is incorrect", 452),
    NEW_PASSWORD_SAME_AS_OLD(45204, "New password must be different from current password", 452),

    // 453 Custom Client Not Found
    USER_NOT_FOUND(45300, "User not found", 453),
    CLIENT_NOT_FOUND(45302, "Client not found", 453),

    // 500 Internal Server Error
    AUTHENTICATION_FAILED(50000, "Authentication failed", 500),

    // 502 Bad Gateway
    BAD_GATEWAY(50200, "Bad gateway", 502),

    // 503 Service Unavailable
    OTP_SEND_FAILED(50300, "Failed to send OTP", 503);

    private final int code;
    private final String message;
    private final int httpStatus;

    StatusCode(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
