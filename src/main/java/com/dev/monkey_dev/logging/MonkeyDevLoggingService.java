package com.dev.monkey_dev.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.dev.monkey_dev.common.serialization.JsonUtils;

/**
 * Service for logging HTTP requests and responses with appropriate log levels
 * based on HTTP status codes and request types.
 */
@Slf4j
@Component
public class MonkeyDevLoggingService extends LoggingServiceImpl {
    /**
     * Logs an HTTP request with INFO level.
     *
     * @param httpServletRequest the HTTP request
     * @param body               the request body
     */
    public void logRequest(HttpServletRequest httpServletRequest, Object body) {
        String logMessage = handleLoggingRequest(httpServletRequest, body);
        log.info(logMessage);
    }

    /**
     * Logs an HTTP request with DEBUG level for detailed debugging.
     *
     * @param httpServletRequest the HTTP request
     * @param body               the request body
     */
    public void logRequestDebug(HttpServletRequest httpServletRequest, Object body) {
        if (log.isDebugEnabled()) {
            String logMessage = handleLoggingRequest(httpServletRequest, body);
            log.debug(logMessage);
        }
    }

    /**
     * Logs an HTTP response with appropriate log level based on status code:
     * - 2xx: INFO
     * - 4xx: WARN
     * - 5xx: ERROR
     * - Other: INFO
     *
     * @param httpServletRequest  the HTTP request
     * @param httpServletResponse the HTTP response
     * @param body                the response body
     */
    public void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Object body) {
        // String logMessage = handleLoggingResponse(httpServletRequest,
        // httpServletResponse, body);
        StringBuilder logMessage = JsonUtils.logAfterResponse(httpServletRequest.getRequestURI(),
                HttpMethod.valueOf(httpServletRequest.getMethod()), body).append("\n");
        log.info(logMessage.toString().trim());
    }

    /**
     * Logs an HTTP response with DEBUG level for detailed debugging.
     *
     * @param httpServletRequest  the HTTP request
     * @param httpServletResponse the HTTP response
     * @param body                the response body
     */
    public void logResponseDebug(HttpServletRequest httpServletRequest, HttpMethod httpMethod,
            String url,
            Object body) {
        if (log.isDebugEnabled()) {
            // String logMessage = handleLoggingResponse(httpServletRequest,
            // httpServletResponse, body);
            StringBuilder logMessage = JsonUtils.logBeforeRequest(url, httpMethod, body).append("\n");
            log.debug(logMessage.toString().trim());
        }
    }

    /**
     * Logs an error that occurred during request/response processing.
     *
     * @param httpServletRequest the HTTP request (can be null)
     * @param message            the error message
     * @param throwable          the exception (can be null)
     */
    public void logError(HttpServletRequest httpServletRequest, String message, Throwable throwable) {
        if (httpServletRequest != null) {
            String requestInfo = String.format("[%s %s]", httpServletRequest.getMethod(),
                    httpServletRequest.getRequestURI());
            if (throwable != null) {
                log.error("{} {}", requestInfo, message, throwable);
            } else {
                log.error("{} {}", requestInfo, message);
            }
        } else {
            if (throwable != null) {
                log.error(message, throwable);
            } else {
                log.error(message);
            }
        }
    }

    /**
     * Logs a warning during request/response processing.
     *
     * @param httpServletRequest the HTTP request (can be null)
     * @param message            the warning message
     */
    public void logWarning(HttpServletRequest httpServletRequest, String message) {
        if (httpServletRequest != null) {
            String requestInfo = String.format("[%s %s]", httpServletRequest.getMethod(),
                    httpServletRequest.getRequestURI());
            log.warn("{} {}", requestInfo, message);
        } else {
            log.warn(message);
        }
    }
}
