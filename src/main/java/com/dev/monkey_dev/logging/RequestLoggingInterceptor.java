package com.dev.monkey_dev.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final MonkeyDevLoggingService monkeyDevLoggingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // Use ContentCachingRequestWrapper to avoid getReader() issues
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

            // Read request body if it exists
            String requestBody = null;
            if (wrappedRequest.getContentLength() > 0 &&
                    wrappedRequest.getContentType() != null &&
                    wrappedRequest.getContentType().contains("application/json")) {

                try {
                    byte[] content = wrappedRequest.getContentAsByteArray();
                    if (content.length > 0) {
                        requestBody = new String(content, StandardCharsets.UTF_8);
                    }
                } catch (Exception e) {
                    log.warn("Failed to read request body: {}", e.getMessage());
                }
            }

            // Log the request
            monkeyDevLoggingService.logRequest(wrappedRequest, requestBody);

        } catch (Exception e) {
            log.error("Error in request logging interceptor: {}", e.getMessage(), e);
        }

        return true;
    }
}
