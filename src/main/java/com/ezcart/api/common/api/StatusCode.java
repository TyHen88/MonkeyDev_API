package com.ezcart.api.common.api;

/**
 * Enum representing standardized status codes and messages for API responses.
 */
public enum StatusCode {

    // 200 Success
    SUCCESS(20000, "Success", 200),

    // 400 Bad Request
    BAD_REQUEST(40000, "Invalid request data", 400),
    VALIDATION_FAILED(40001, "Validation failed", 400),
    INVALID_REQUEST_BODY(40002, "Invalid request body", 400),
    CREDENTIALS_REQUIRED(40003, "Username and password are required", 400),
    USERNAME_REQUIRED(40004, "Username is required", 400),
    PASSWORD_REQUIRED(40005, "Password is required", 400),
    REFRESH_TOKEN_REQUIRED(40006, "Refresh token is required", 400),
    REGISTRATION_REQUEST_REQUIRED(40007, "Registration request is required", 400),
    INVALID_PRODUCT_ID(40008, "Invalid product ID", 400),
    INVALID_CATEGORY_ID(40009, "Invalid category ID", 400),
    SLUG_REQUIRED(40010, "Slug is required", 400),
    PASSWORD_LENGTH_INVALID(40011, "Password length must be between 8 and 64", 400),
    ROLE_NOT_FOUND(40012, "One or more roles not found", 400),
    RESET_TOKEN_REQUIRED(40013, "Reset token is required", 400),
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
    INVALID_EMAIL(40040, "Email is not valid", 400),
    SECURITY_CODE_ENCRYPTION_REQUIRED(40041, "Security code must be encrypted", 400),
    SECURITY_KEY_ENCRYPTION_REQUIRED(40042, "Security key must be encrypted", 400),
    OAUTH_EMAIL_NOT_FOUND(40043, "Email not found from OAuth2 provider", 400),
    OAUTH_PROVIDER_MISMATCH(40044, "OAuth2 provider does not match the existing account", 400),
    OAUTH_PROVIDER_NOT_SUPPORTED(40045, "OAuth2 provider is not supported", 400),
    INCORRECT_PASSWORD(40055, "Incorrect username or password", 401),
    INACTIVE_USER(40056, "User account is disabled", 403),
    INVALID_TOKEN(40051, "Invalid token", 400),

    // 401 Unauthorized
    UNAUTHORIZED(40100, "Unauthorized", 401),
    INVALID_REFRESH_TOKEN(40101, "Invalid or expired refresh token", 401),

    // 403 Forbidden
    FORBIDDEN(40300, "Forbidden", 403),
    ACCESS_DENIED(40301, "You do not have permission to perform this action", 403),

    // 409 Conflict
    USER_ID_ALREADY_EXISTS(40913, "User already exists", 409),
    EMAIL_ALREADY_EXISTS(40914, "Email already exists", 409),
    USERNAME_ALREADY_EXISTS(40915, "Username already exists", 409),
    DEFAULT_ROLE_NOT_CONFIGURED(50002, "Default role is not configured", 500),

    // 452 Custom Client Errors
    PASSWORD_INCORRECT_452(45200, "Password is incorrect", 452),
    INVALID_SECRET(45201, "Secret is incorrect", 452),
    INCORRECT_CURRENT_PASSWORD(45202, "Current password is incorrect", 452),
    PASSWORD_INCORRECT_AGAIN(45203, "Password is incorrect", 452),
    NEW_PASSWORD_SAME_AS_OLD(45204, "New password must be different from current password", 452),

    // 404 Not Found
    USER_NOT_FOUND(40400, "User not found", 404),
    CLIENT_NOT_FOUND(40402, "Client not found", 404),
    ADDRESS_NOT_FOUND(40403, "Address not found", 404),
    CATEGORY_NOT_FOUND(40404, "Category not found", 404),
    PRODUCT_NOT_FOUND(40406, "Product not found", 404),
    PRODUCTS_NOT_FOUND(40407, "No products found", 404),

    // 409 Conflict
    CATEGORY_NOT_ACTIVE(40905, "Category is not active", 409),
    PRODUCT_NOT_ACTIVE(40907, "Product is not active", 409),
    PRODUCT_ALREADY_IN_CATEGORY(40908, "Product already in category", 409),
    // 500 Internal Server Error
    AUTHENTICATION_FAILED(50000, "Authentication failed", 500),
    INTERNAL_SERVER_ERROR(50001, "Internal server error", 500),

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

